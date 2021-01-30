package smartdoc.dashboard.distributelock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Pointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import smartdoc.dashboard.core.ServiceException
import smartdoc.dashboard.core.Status
import java.lang.reflect.Method

/**
 * The interface ReentrantDistributeLock
 *
 * @see DistributeLock
 * @see LockKey
 * @author Maple
 */
internal interface ReentrantDistributeLock {
    fun lock(lockKey: String): Boolean
    fun unlock(lockKey: String): Boolean
}

/**
 * RedisReentrantDistributeLock based on redis
 */
@Component
open class RedisReentrantDistributeLock(@Autowired val redisTemplate: RedisTemplate<String, Any>) : ReentrantDistributeLock {
    override fun lock(lockKey: String): Boolean {
        return true
    }

    override fun unlock(lockKey: String): Boolean {
        return false
    }
}

/**
 * MongoReentrantDistributeLock based on mongodb
 */
@Component
open class MongoReentrantDistributeLock(@Autowired val mongoTemplate: MongoTemplate) : ReentrantDistributeLock {
    override fun lock(lockKey: String): Boolean {
        return true
    }

    override fun unlock(lockKey: String): Boolean {
        return false
    }
}

/**
 * Based on aop implement lock
 */
//@Component
//@Aspect
open class DistributeLockAspect(
        @Autowired val redisReentrantDistributeLock: RedisReentrantDistributeLock,
        @Autowired val mongoReentrantDistributeLock: MongoReentrantDistributeLock
) {

    @Pointcut(value = "@annotation(restdoc.web.distributelock.DistributeLock)")
    fun pointCut() {
    }

    @Around("pointCut()")
    fun around(point: ProceedingJoinPoint): Any? {

        val args: Array<Any> = point.args
        val parameterTypes = args.map { it.javaClass }.toTypedArray()
        val method: Method = point.signature.declaringType.getMethod(point.signature.name, *parameterTypes)
        val distributeLock: DistributeLock = method.getDeclaredAnnotation(DistributeLock::class.java)
        val parameters = method.parameters

        var lockKeyIndex: Int = -1

        for (item: Int in parameters.indices) {
            val lk: LockKey? = parameters[item].getDeclaredAnnotation(LockKey::class.java)
            if (lk != null) {
                lockKeyIndex = item
                break
            }
        }
        val lockKey: String
        if (lockKeyIndex == -1 )
            Status.INTERNAL_SERVER_ERROR.error("LockKey Error")
        if (args[lockKeyIndex] == null)
             Status.INTERNAL_SERVER_ERROR.error("LockKey Error")

        lockKey = args[lockKeyIndex].toString()

        val lockTool: ReentrantDistributeLock =
                if (distributeLock.type == DistributeLockType.MONGODB)
                    mongoReentrantDistributeLock
                else redisReentrantDistributeLock

        val lockSuccess = lockTool.lock(lockKey)

        if (lockSuccess) {
            val result: Any? = point.proceed(args)
            lockTool.unlock(lockKey)
            return result
        } else throw ServiceException(Status.INVALID_REQUEST)
    }
}