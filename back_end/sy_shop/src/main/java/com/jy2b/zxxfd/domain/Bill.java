package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tb_bill")
public class Bill {
    @TableId("id")
    private Long id; // 唯一标识
    @TableField("user_id")
    private Long userId; // 用户ID
    @TableField("bill_id")
    private String billId; // 交易账单ID
    @TableField("name")
    private String name; // 交易名称
    @TableField("amount")
    private Double amount; // 交易金额
    @TableField("type")
    private String type; // 账单类型
    @TableField("time")
    private LocalDateTime time; // 交易时间
}
