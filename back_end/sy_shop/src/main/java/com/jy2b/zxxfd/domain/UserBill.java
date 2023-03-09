package com.jy2b.zxxfd.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author 林武泰
 */
@Data
public class UserBill {
    private String id; // 交易单号
    private Double amount; // 交易金额
    private String monetaryUnit; // 货币单位
    private String name; // 交易商品
    private String merchantName; // 商户名称
    private String type; // 账单类型 0：收入，1：支出
    private String payMethod; // 支付方式
    @DateTimeFormat(pattern = "yyyy年MM月dd日 HH:mm:ss")
    @JsonFormat(pattern = "yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime time; // 支付时间
    private String status; // 当前状态
    private String remark; // 支付备注
}
