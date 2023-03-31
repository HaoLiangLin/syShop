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
@TableName("tb_notice")
public class Notice {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;
    @TableField("title")
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("cid")
    private Long cid;
    @TableField("content")
    private String content;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
}
