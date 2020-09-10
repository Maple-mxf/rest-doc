package restdoc.web.base.mongo;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 实现mongodb的embedded文档的条件查询/分页查询/聚合查询  内嵌文档的添加/删除/修改
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

    MongoEntityInformation<T, ID> getEntityInformation();

    List<Map> groupSum(String sumField, String... groupFields);

    List<Map> groupSumBy(Criteria criteria, String sumField, String... groupFields);

    MapReduceResults<T> mapReduce(String mapFunction, String reduceFunction);

    Optional<T> findOne(Query query);

    T getOne(Query query);

    List<T> list(Query query);

    List<T> listSort(Query query, Sort sort);

    Page<T> page(Query query, Pageable pageable);

    long count(Query query);

    boolean exists(Query query);

    /**
     * 索引管理
     *
     * @see Index
     */
    String ensureIndex(Index index);

    List<IndexInfo> getIndexInfo();

    /**
     * update an entity by id
     */
    UpdateResult update(T entity);


    UpdateResult updateById(ID id, T entity);

    /**
     * update an entity by id and version{@link org.springframework.data.annotation.Version}
     */
    UpdateResult updateByIdAndVersion(T entity);

    /**
     * batch update
     */
    UpdateResult updateBatch(List<T> entities);

    /**
     * update by query as conditions
     */
    UpdateResult update(T entity, Query query);


    @Deprecated
    UpdateResult update(Query query, Update update);

    /**
     * delete by Id
     */
    DeleteResult delete(Query query);
}
