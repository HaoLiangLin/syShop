package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_goods_evaluation")
public class GoodsEvaluation {
    @TableId("id")
    private Long id;

    @TableField("uid")
    private Long uid;

    @TableField("order_id")
    private Long orderId;

    @TableField("goods_id")
    private Long goodsId;

    @TableField("goodsItem_id")
    private Long goodsItemId;

    @TableField("stars")
    private Integer stars;

    @TableField("images")
    private String images;

    @TableField("content")
    private String content;

    @TableField("time")
    private Date time;

    @TableField("liked")
    private Integer liked;

    @TableField("status")
    private Integer status;
}
