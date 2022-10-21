package com.jy2b.zxxfd.domain.dto;

import lombok.Getter;

@Getter
public enum BillType {
    Recharge(1, "充值"),
    Shopping(2,"购物"),
    SignIn(3, "签到");

    private Integer code;
    private String name;

    BillType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
