package com.jy2b.zxxfd.domain.dto;

import com.jy2b.zxxfd.domain.GoodsItem;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class GoodsItemDTO {
    private GoodsItem defaultOption; // 默认选择

    private List<HashMap<String,String>> colorAndIcon; // 颜色与图片

    private List<String> combo; // 套餐

    private List<String> size; // 尺寸

    private List<String> edition; // 版本
}
