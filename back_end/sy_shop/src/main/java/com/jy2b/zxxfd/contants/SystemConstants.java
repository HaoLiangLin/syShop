package com.jy2b.zxxfd.contants;

public class SystemConstants {
    // jwt密钥发行方
    public static final String JWT_ISSUER = "20zxXfd";
    // jwt密钥主题
    public static final String JWT_SUBJECT = "sy_shop";
    // jwt受众群体
    public static final String JWT_AUDIENCE = "sy_shop_users";

    // 用户默认昵称/账号前缀
    public static final String USER_NICK_NAME_PREFIX = "sy_blog_user";
    // 用户上传文件存放地址
    // public static final String UPLOAD_IMAGE_PATH = "E:\\毕业设计\\sy_shop\\back_end\\resources\\files";
    public static final String UPLOAD_IMAGE_PATH = "/root/sy_shop/back_end/resources/files";

    // 用户默认密码
    public static final String USER_DEFAULT_PASSWORD = "123456";

    // 用户签到赠送积分
    public static final Long SIGN_IN_POINTS = 10L;

    // 商品封面数量
    public static final int GOODS_IMAGES_LENGTH = 3;
    // 评价图片数量
    public static final int COMMENT_IMAGES_LENGTH = 10;
}
