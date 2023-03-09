package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 林武泰
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user_address")
public class UserAddress {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableId("id")
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("uid")
    private Long uid;
    @TableField("name")
    private String name;
    @TableField("phone")
    private String phone;
    @TableField("province")
    private String province;
    @TableField("city")
    private String city;
    @TableField("district")
    private String district;
    @TableField("address")
    private String address;
    @TableField("isDefault")
    private Integer isDefault;
}
