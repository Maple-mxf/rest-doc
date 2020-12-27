package restdoc.web.core.init;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;
import restdoc.web.util.ReflectUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Maple
 * 实体类必须声明{@link Document} 注解
 * @since 2.0.RELEASE
 */
@Component
public class AutoCreateCollection implements CommandLineRunner {

    private String[] collectionEntityPackage = {"restdoc.web.model"};

    private final Logger log = LoggerFactory.getLogger(AutoCreateCollection.class);

    private final MongoTemplate mongoTemplate;

    private final MongoMappingContext mongoMappingContext;

    public AutoCreateCollection(MongoTemplate mongoTemplate, MongoMappingContext mongoMappingContext) {
        this.mongoTemplate = mongoTemplate;
        this.mongoMappingContext = mongoMappingContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initIndicesAfterStartup() {
        List<Class<?>> classList = Arrays.stream(collectionEntityPackage)
                .flatMap(pk -> {
                    try {
                        List<Class<?>> classes = Optional.ofNullable(ReflectUtil.getClasses(pk)).orElse(Lists.newArrayList());
                        return classes.stream();
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }
                })
                .filter(type -> {
                    Document document = type.getDeclaredAnnotation(Document.class);
                    return document != null;
                })
                .collect(Collectors.toList());


        for (Class<?> type : classList) {
            if (!mongoTemplate.collectionExists(type)) {
                mongoTemplate.createCollection(type);
                IndexOperations indexOps = mongoTemplate.indexOps(type);
                IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
                resolver.resolveIndexFor(type).forEach(indexOps::ensureIndex);
            }
        }

        log.info("complete create index");
    }

    /**
     * init create collection
     *
     * @see org.springframework.data.mongodb.core.index.HashIndexed
     * @see org.springframework.stereotype.Indexed
     */
    @Override
    public void run(String... args) {
    }
}
