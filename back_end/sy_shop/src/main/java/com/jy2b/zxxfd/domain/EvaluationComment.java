package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("tb_evaluation_comment")
public class EvaluationComment {
    @TableId("id")
    private Long id;

    @TableField("uid")
    private Long uid;

    @TableField("evaluation_id")
    private Long evaluationId;

    @TableField("content")
    private String content;

    @TableField("time")
    private Date time;

    @TableField("liked")
    private Integer liked;

    @TableField("status")
    private Integer status;

    @TableField("fid")
    private Long fid;

    @TableField("isAuthor")
    private Integer isAuthor;
}
