package com.jy2b.zxxfd.domain.vo;

import lombok.Getter;

/**
 * @author 林武泰
 */
@Getter
public enum BillStatusCode {
    paymentFailed(0, "支付失败"),
    paymentSuccess(1, "支付成功"),
    refundedSuccess(2, "已退款"),
    rechargeFailed(3, "充值失败"),
    rechargeSuccess(4, "充值成功");

    private final int code;
    private final String name;

    BillStatusCode(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
