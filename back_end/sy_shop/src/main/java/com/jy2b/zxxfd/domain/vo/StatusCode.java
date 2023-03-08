package com.jy2b.zxxfd.domain.vo;

/**
 * 封装返回状态码
 * @author 林武泰
 * @version 1.0
 */
public class StatusCode {
    public static final int FAIL = 20010; // 操作失败
    public static final int SUCCEED = 20011; // 操作成功
    public static final int UNAUTHORIZED = 401; // 认证失败
    public static final int INVALID_REQUEST = 416; // 请求无效
    public static final int INTERNAL_SERVER_ERROR = 500; // 服务端异常
}
