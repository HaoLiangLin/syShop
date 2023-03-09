package com.jy2b.zxxfd.domain.vo;

import lombok.Getter;

/**
 * @author 林武泰
 */
@Getter
public enum BillType {
    disburse(0, "支出"),
    income(1, "收入");

    private final int code;
    private final String name;

    BillType(int code, String name){
        this.code = code;
        this.name = name;
    }
}
