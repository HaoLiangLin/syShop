package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_goods_collection")
public class UserCollection {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("uid")
    private Long uid;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("gid")
    private Long gid;

    @TableField("create_time")
    private Date createTime;
}
