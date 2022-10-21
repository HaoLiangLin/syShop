package com.jy2b.zxxfd.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo {
    private Boolean success;
    private Object data;
    private String message;

    /**
     * 成功无返回
     * @return
     */
    public static ResultVo ok() {
        return new ResultVo(true, null, null);
    }

    /**
     * 成功返回参数
     * @param data
     * @return
     */
    public static ResultVo ok(Object data) {
        return new ResultVo(true, data, null);
    }

    /**
     * 成功返回参数与消息
     */
    public static ResultVo ok(Object data, String message) {
        return new ResultVo(true, data, message);
    }

    /**
     * 失败无返回
     */
    public static ResultVo fail() {
        return new ResultVo(false, null, null);
    }

    /**
     * 失败返回消息
     */
    public static ResultVo fail(String message) {
        return new ResultVo(false, null, message);
    }

}
