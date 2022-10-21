package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_shopping_cart")
public class ShoppingCart {
    @TableId("id")
    private Long id;

    @TableField("uid")
    private Long uid;

    @TableField("gid")
    private Long gid;

    @TableField("quantity")
    private Integer quantity;

    @TableField("create_time")
    private Date createTime;
}
