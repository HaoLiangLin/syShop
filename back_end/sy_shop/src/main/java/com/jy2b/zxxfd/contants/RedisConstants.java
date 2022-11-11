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
    // 用户登录token过期期限 60分钟
    public static final Long LOGIN_USER_TTL = 60L;
    // 用户登录Security存放key
    public static final String LOGIN_KEY = "login:";
    // 用户登录Security存放key过期时限 60分钟
    public static final Long LOGIN_KEY_TTL = 60L;
    // 用户修改手机号验证码
    public static final String UPDATE_PHONE_CODE_KEY = "phone:code:";
    // 用户忘记密码验证码有效期
    public static final Long UPDATE_PHONE_CODE_TTL = 5L;
    // 用户忘记密码验证码
    public static final String FORGOT_PASSWORD_CODE_KEY = "password:code:";
    // 用户忘记密码验证码有效期
    public static final Long FORGOT_PASSWORD_CODE_TTL = 5L;
    // 用户签到
    public static final String SIGN_IN_KEY = "sign:";
    // 用户忘记密码手机号存放key
    public static final String FORGOT_PASSWORD_PHONE_KEY = "password:phone:";
    // 用户忘记密码手机号有效期 30分钟
    public static final Long FORGOT_PASSWORD_PHONE_TTL = 30L;
    // 用户信息key
    public static final String USER_INFO_KEY = "user:info:";
    // 用户收货地址key
    public static final String USER_ADDRESS_KEY = "user:address:";
    // 用户默认收货地址key
    public static final String USER_DEFAULT_ADDRESS_KEY = "user:address:default:";
    // 用户账单key
    public static final String BILL_KEY = "bill:";

    // 商品一级分类key
    public static final String GOODS_CATEGORY_FIRST = "goods:category:first";

    // 订单key
    public static final String ORDER_KEY = "order:";
    // 订单评价点赞key
    public static final String EVALUATION_LIKED_KEY = "evaluation:liked:";
    // 评价评论点赞Key
    public static final String COMMENT_LIKED_KEY = "comment:liked:";

    // 充值套餐key
    public static final String RECHARGE_COMBO_KEY = "recharge:combo";
    // 活动事件Key
    public static final String EVENTS_KEY = "events";

    // 公告一级类型key
    public static final String NOTICE_CATEGORY_FIRST = "notice:category:first";
    // 公告Key
    public static final String NOTICE_KEY = "notice:";

    // 缓存工具类
    public static final Long CACHE_NULL_TTL = 2L;

    public static final String LOCK_SHOP_KEY = "lock:shop:";
}
