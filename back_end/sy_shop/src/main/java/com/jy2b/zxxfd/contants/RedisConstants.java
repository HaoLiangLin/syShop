package com.jy2b.zxxfd.contants;

public class RedisConstants {
    // 用户注册验证码
    public static final String REGISTER_CODE_KEY = "register:code:";
    // 用户注册验证码有效期 5分钟
    public static final Long REGISTER_CODE_TTL = 5L;

    // 用户登录验证码
    public static final String LOGIN_CODE_KEY = "login:code:";
    // 用户登录验证码有效期 5分钟
    public static final Long LOGIN_CODE_TTL = 5L;

    // 用户登录token
    public static final String LOGIN_USER_KEY = "login:token:";
    // 用户登录token过期期限 30分钟
    public static final Long LOGIN_USER_TTL = 60L;
    // 用户登录Security存放key
    public static final String LOGIN_KEY = "login:";
    // 用户登录Security存放key过期时限 30分钟
    public static final Long LOGIN_KEY_TTL = 30L;
    // 用户忘记密码验证码
    public static final String FORGOT_PASSWORD_CODE_KEY = "password:code";
    // 用户忘记密码验证码有效期
    public static final Long FORGOT_PASSWORD_CODE_TTL = 5L;

    // 用户签到
    public static final String SIGN_IN_KEY = "sign:";

    // 用户忘记密码手机号存放key
    public static final String FORGOT_PASSWORD_PHONE_KEY = "password:phone";
    // 用户忘记密码手机号有效期 30分钟
    public static final Long FORGOT_PASSWORD_PHONE_TTL = 30L;

    // 用户账单key
    public static final String BILL_KEY = "bill:";

    // 订单key
    public static final String ORDER_KEY = "order:";

    public static final Long CACHE_NULL_TTL = 2L;

    public static final String LOCK_SHOP_KEY = "lock:shop:";
}
