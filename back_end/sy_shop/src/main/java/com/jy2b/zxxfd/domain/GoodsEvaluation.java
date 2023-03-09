package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
@TableName("tb_goods_evaluation")
public class GoodsEvaluation {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("uid")
    private Long uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("order_id")
    private Long orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("goods_id")
    private Long goodsId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
