package smartdoc.dashboard.distributelock

/**
 * DistributeLockType
 *
 * @author Maple
 * @since 2.0.RELEASE
 */
enum class DistributeLockType {

    /**
     * REDIS
     * @see RedisReentrantDistributeLock
     */
    REDIS,

    /**
     * MONGODB
     * @see MongoReentrantDistributeLock
     */
    MONGODB
}

/**
 * @author Maple
 *
 * @since 2.0.RELEASE
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributeLock(val name: String, val msgIOffNonGet: String = "Please retry", val type: DistributeLockType = DistributeLockType.REDIS)

/**
 * @author Maple
 *
 * @since 2.0.RELEASE
 */
@MustBeDocumented
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LockKey(val message: String = "")