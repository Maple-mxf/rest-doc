package restdoc.base.mongo;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.mongodb.repository.support.QuerydslMongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;

import java.io.Serializable;

public class BaseMongoRepositoryFactoryBean<T extends MongoRepository<S, ID>, S, ID extends Serializable>
        extends MongoRepositoryFactoryBean<T, S, ID> {

    public BaseMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new LCRRepositoryFactory<>(operations);
    }

    private static class LCRRepositoryFactory<S, ID extends Serializable> extends MongoRepositoryFactory {

        private final MongoOperations mongoOperations;

        public LCRRepositoryFactory(MongoOperations mongoOperations) {
            super(mongoOperations);
            this.mongoOperations = mongoOperations;
        }

        @Override
        protected Object getTargetRepository(RepositoryInformation information) {
            Class<?> repositoryInterface = information.getRepositoryInterface();
            MongoEntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
            if (isQueryDslRepository(repositoryInterface)) {
                return new QuerydslMongoRepository(entityInformation, mongoOperations);
            }
            else {
                return new BaseRepositoryImpl<>((MongoEntityInformation<S, ID>) entityInformation, this.mongoOperations);
            }
        }


        private static boolean isQueryDslRepository(Class<?> repositoryInterface) {
            return QuerydslUtils.QUERY_DSL_PRESENT && QuerydslPredicateExecutor.class.isAssignableFrom(repositoryInterface);
        }

        @NonNull
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return isQueryDslRepository(metadata.getRepositoryInterface()) ? QuerydslMongoRepository.class
                    : BaseRepositoryImpl.class;
        }
    }
}
