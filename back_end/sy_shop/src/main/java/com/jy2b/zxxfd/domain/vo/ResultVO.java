package com.jy2b.zxxfd.domain.vo;

import lombok.Data;

import static com.jy2b.zxxfd.domain.vo.StatusCode.FAIL;
import static com.jy2b.zxxfd.domain.vo.StatusCode.SUCCEED;

@Data
public class ResultVO {
    private Integer code; // 返回结果，成功：true/失败：false
    private Object data; // 返回数据
    private String message; // 返回消息

    /**
     * 请求结果返回封装
     * @param code 返回结果
     * @param data 返回数据
     * @param message 返回消息
     */
    public ResultVO(Integer code, Object data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 操作成功，无参
     * @return
     */
    public static ResultVO ok() {
        return new ResultVO(SUCCEED, null, "");
    }

    /**
     * 操作成功，只返回消息
     * @param message 返回消息
     * @return ResultVO
     */
    public static ResultVO ok(String message) {
        return new ResultVO(SUCCEED, null, message);
    }

    /**
     * 操作成功，返回数据与消息
     * @param data 返回数据
     * @param message  返回消息
     * @return ResultVO
     */
    public static ResultVO ok(Object data, String message) {
        return new ResultVO(SUCCEED, data, message);
    }

    /**
     * 操作失败，无参
     * @return ResultVO
     */
    public static ResultVO fail() {
        return new ResultVO(FAIL, null, "");
    }

    /**
     * 操作失败
     * @param errMessage 返回失败消息
     * @return ResultVO
     */
    public static ResultVO fail(String errMessage) {
        return new ResultVO(FAIL, null, errMessage);
    }

}
