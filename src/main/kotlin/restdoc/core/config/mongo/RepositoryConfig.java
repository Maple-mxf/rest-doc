package restdoc.core.config.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import restdoc.base.mongo.BaseMongoRepositoryFactoryBean;
import restdoc.base.mongo.BaseRepositoryImpl;

@Configuration
@AutoConfigureBefore(value = {MongoAutoConfiguration.class})
@EnableMongoRepositories(basePackages =
        {"restdoc.repository"},
        repositoryBaseClass = BaseRepositoryImpl.class,
        repositoryFactoryBeanClass = BaseMongoRepositoryFactoryBean.class
)
public class RepositoryConfig {

    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public MongoMappingContext mongoMappingContext() {
        MongoMappingContext mongoMappingContext = new MongoMappingContext();
        mongoMappingContext.setAutoIndexCreation(false);
        mongoMappingContext.setApplicationContext(applicationContext);
        return mongoMappingContext;
    }
}
