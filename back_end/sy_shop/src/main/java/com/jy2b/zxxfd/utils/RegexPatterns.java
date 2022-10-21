package com.jy2b.zxxfd.utils;


public abstract class RegexPatterns {
    /**
     * 负浮点数
     */
    public static final String NEGATIVE_FLOAT = "^-([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*)$";
    /**
     * 用户名正则 英文字母开头，包含大小写字母，数字和下划线
     */
    public static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9_]+$";
    /**
     * 手机号正则
     */
    public static final String PHONE_REGEX = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * 密码正则。4~32位的字母、数字、下划线
     */
    public static final String PASSWORD_REGEX = "^\\w{4,32}$";
    /**
     * 验证码正则, 6位数字或字母
     */
    public static final String VERIFY_CODE_REGEX = "^[a-zA-Z\\d]{6}$";

}
