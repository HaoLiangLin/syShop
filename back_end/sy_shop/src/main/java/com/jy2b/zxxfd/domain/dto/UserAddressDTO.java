package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private String name; // 收货人名字
    private String phone;  // 联系电话
    private String province; // 省份
    private String city; // 城市
    private String district; // 区/县
    private String address; // 具体地址
    private Integer isDefault; // 是否默认收货地址，0：否，1：是
}
