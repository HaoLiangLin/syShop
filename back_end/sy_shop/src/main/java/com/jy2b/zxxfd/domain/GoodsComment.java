package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_goods_comment")
public class GoodsComment {
    @TableId("id")
    private Long id;

    @TableField("uid")
    private Long uid;

    @TableField("order_id")
    private Long orderId;

    @TableField("gid")
    private Long gid;

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
