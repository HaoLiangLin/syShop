package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
@TableName("tb_notice_category")
public class NoticeCategory {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("fid")
    private Long fid;

    @TableField("remark")
    private String remark;
}
