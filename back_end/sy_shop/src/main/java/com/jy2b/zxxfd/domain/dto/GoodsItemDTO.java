package com.jy2b.zxxfd.domain.dto;

import com.jy2b.zxxfd.domain.GoodsItem;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * @author 林武泰
 */
@Data
public class GoodsItemDTO {
    private GoodsItem defaultOption; // 默认选择

    private List<HashMap<String,String>> colorAndIcon; // 颜色与图片

    private List<HashMap<String, String>> combo; // 套餐

    private List<HashMap<String, String>> size; // 尺寸

    private List<HashMap<String, String>> edition; // 版本
}
