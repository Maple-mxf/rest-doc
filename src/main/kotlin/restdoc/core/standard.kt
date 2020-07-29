package restdoc.core

import java.lang.RuntimeException

/**
 */
enum class Status(val status: String,
                  val code: String,
                  val message: String?) {

    /**
     * Process Success
     */
    OK("OK", "200", "OK"),

    /**
     * Client Not Found
     */
    NOT_FOUND("NotFound", "U_404", "找不到请求地址"),

    /**
     * Client Param Error Or Method Error
     */
    BAD_REQUEST("BadRequest", "C_400", "客户端请求的语法错误，服务器无法理解"),

    /**
     * Forbidden
     */
    FORBIDDEN("Forbidden", "C_403", "服务器理解请求客户端的请求，但是拒绝执行此请求"),

    /**
     * Unauthorized
     */
    UNAUTHORIZED("Unauthorized", "C_401", "请求要求用户的身份认证"),

    /**
     * Unsupported Media Type
     * 415
     */
    UNSUPPORTED_MEDIA_TYPE("UnsupportedMediaType", "C415", "服务器无法处理请求附带的媒体格式"),

    /**
     * Server Error
     */
    INTERNAL_SERVER_ERROR("InternalServerError", "S500", "服务器内部错误"),

    /**
     * Third Service Error
     */
    THIRD_SERVICE_ERROR("ThirdServiceError", "T500", "第三方服务错误")
}

data class Result(
        val status: String = Status.OK.status,
        val code: String = Status.OK.code,
        val message: String? = null,
        val data: Any? = null
)

/**
 *
 */
class BizServiceException(override val message: String?, val status: Status) : RuntimeException(message) {
    constructor(status: Status) : this(message = status.message, status = status)
}

/**
 *
 */
fun ofBizError(status: Status, message: String): BizServiceException = throw BizServiceException(message, status)

/**
 *
 */
fun ofBizError(status: Status): BizServiceException = throw BizServiceException(status)

/**
 *
 */
fun ok(): Result = Result()

/**
 *
 */
fun ok(data: Any): Result = Result(data = data)

/**
 *
 */
fun failure(status: Status): Result = Result(status = status.status, code = status.code, message = status.message)