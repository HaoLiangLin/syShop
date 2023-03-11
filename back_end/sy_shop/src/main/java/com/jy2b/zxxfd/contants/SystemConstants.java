package com.jy2b.zxxfd.contants;

/**
 * @author 林武泰
 * 系统静态常量
 */
public class SystemConstants {
    // 系统名称
    public static final String APPLICATION_NAME = "MyShop我的商城";
    // jwt密钥发行方
    public static final String JWT_ISSUER = "20zxXfd";
    // jwt密钥主题
    public static final String JWT_SUBJECT = "sy_shop";
    // jwt受众群体
    public static final String JWT_AUDIENCE = "sy_shop_users";
    // 用户默认昵称/账号前缀
    public static final String USER_NICK_NAME_PREFIX = "sy_shop_user";
    // 管理员登录密令
    public static final Integer ADMIN_LOGIN_KEY = 200114216;

    /*排序规则*/
    public static final Integer DESCENDING_SORT = 0; // 降序排序
    public static final Integer ASCENDING_SORT = 1; // 升序排序

    // 用户上传文件存放地址
    // public static final String UPLOAD_IMAGE_PATH = "E:\\毕业设计\\sy_shop\\back_end\\resources\\files";
    public static final String UPLOAD_IMAGE_PATH = "/root/sy_shop/back_end/resources/files";

    // 用户签到赠送积分
    public static final Double SIGN_IN_POINTS = 10D;

    // 商品封面数量
    public static final int GOODS_IMAGES_LENGTH = 10;

    // 评价图片数量
    public static final int COMMENT_IMAGES_LENGTH = 10;
}
