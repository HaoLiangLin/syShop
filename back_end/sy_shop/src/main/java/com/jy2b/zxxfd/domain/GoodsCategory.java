package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_goods_category")
public class GoodsCategory {
    @TableId("id")
    private Long id;

    @TableField("name")
    private String name;

    @TableField("icon")
    private String icon;

    @TableField("fid")
    private Long fid;

    @TableField("remark")
    private String remark;
}
