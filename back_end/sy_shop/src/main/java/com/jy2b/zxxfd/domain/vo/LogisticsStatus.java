package com.jy2b.zxxfd.domain.vo;

import lombok.Getter;

@Getter
public enum LogisticsStatus {
    unshipped(0, "未发货"),
    shipped(1, "待收货"),
    received(2, "已收货"),
    notYetExchange(3, "待换货"),
    haveExchange(4, "已换货"),
    notYetRefund(5, "待退货"),
    haveRefund(6, "已退货");

    private final int code;
    private final String name;

    LogisticsStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
