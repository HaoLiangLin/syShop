package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CityDTO {
    private String cityName; // 市名称
    private String cityCode; // 市代码
    private List<Map<String, String>> area; // 多个区：区名称，区代码
}
