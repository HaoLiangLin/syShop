package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_shopping_cart")
public class ShoppingCart {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("uid")
    private Long uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("gid")
    private Long gid;

    @TableField("quantity")
    private Integer quantity;

    @TableField("create_time")
    private Date createTime;
}
