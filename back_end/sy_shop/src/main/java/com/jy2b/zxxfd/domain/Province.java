package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 林武泰
 */
@TableName("tb_province")
@Data
public class Province {
    @TableId("id")
    private Integer id; // 唯一标识
    @TableField("code")
    private Long code; // 行政区划代码
    @TableField("name")
    private String name; // 名称
    @TableField("province")
    private String province; // 省/直辖市
    @TableField("city")
    private String city; // 市
    @TableField("area")
    private String area; // 区
    @TableField("town")
    private String town; // 城镇地区
}
