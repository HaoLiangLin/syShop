package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_goods_item")
public class GoodsItem {
    @TableId("id")
    private Long id;

    @TableField("gid")
    private Long gid;

    @TableField("icon")
    private String icon;

    @TableField("color")
    private String color;

    @TableField("combo")
    private String combo;

    @TableField("size")
    private String size;

    @TableField("edition")
    private String edition;

    @TableField("price")
    private Double price;

    @TableField("discount")
    private Double discount;

    @TableField("stock")
    private Long stock;

    @TableField("sales")
    private Long sales;

    @TableField("remark")
    private String remark;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;
}
