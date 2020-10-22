package restdoc.web.base.auth;

/**
 *
 */
public interface Token {

    //  访问的cookie name
    String ACCESS_TOKEN = "restdoc_console_access_token";

    // 如果用户长期使用 则续期
    // ACCESS_TOKEN 过期时间  单位为second  失效时间为两月
    long EXPIRE_TIME = 5184000L;

    // 密码加密迭代次数
    int HASH_ITERATION_COUNT = 1025;
}
