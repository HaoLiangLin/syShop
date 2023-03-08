package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProvinceDTO {
    private String provinceName; // 省名称
    private String provinceCode; // 省代码
    private List<CityDTO> city; // 多个市
}
