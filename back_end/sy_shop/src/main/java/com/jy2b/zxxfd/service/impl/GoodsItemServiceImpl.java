package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.GoodsItemDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.GoodsItemSaveFromDTO;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IGoodsItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GoodsItemServiceImpl extends ServiceImpl<GoodsItemMapper, GoodsItem> implements IGoodsItemService {
    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Override
    public ResultVo saveItem(GoodsItemSaveFromDTO itemFromDTO) {
        // 判断商品是否存在
        String gid = itemFromDTO.getGid();
        if (gid == null) {
            return ResultVo.fail("请指定添加属性的商品！");
        }
        Goods goods = goodsMapper.selectById(gid);
        if (goods == null) {
            return ResultVo.fail("商品不存在！");
        }

        // 获取属性颜色
        String color = itemFromDTO.getColor();
        // 判断属性颜色是否不为空
        if (StrUtil.isBlank(color)) {
            return ResultVo.fail("添加商品属性失败，颜色不能为空！");
        }

        // 判断属性图片是否不为空
        if (StrUtil.isBlank(itemFromDTO.getIcon())) {
            // TODO return ResultVo.fail("添加商品属性失败，图片不能为空！");
            itemFromDTO.setIcon("测试图片");
        }

        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();

        // 获取属性尺寸
        String size = itemFromDTO.getSize();
        // 判断尺寸是否不为空
        if (StrUtil.isNotBlank(size)) {
            queryWrapper.eq("size", size);
        }
        // 获取属性套餐
        String combo = itemFromDTO.getCombo();
        // 判断套餐是否不为空
        if (StrUtil.isNotBlank(combo)) {
            queryWrapper.eq("combo", combo);
        }
        // 获取属性版本
        String edition = itemFromDTO.getEdition();
        // 判断版本是否不为空
        if (StrUtil.isNotBlank(edition)) {
            queryWrapper.eq("edition", edition);
        }
        queryWrapper.eq("gid", gid).eq("color", color);
        // 查询是否有重复项
        int colorCount = count(queryWrapper);
        if (colorCount > 0) {
            return ResultVo.fail("商品属性已存在！");
        }

        // 判断属性价格是否不为空，且大于零
        if (itemFromDTO.getPrice() == null || itemFromDTO.getPrice() <= 0) {
            return ResultVo.fail("添加商品属性价格不能为空或小于等于零！");
        }
        // 判断属性折扣是否不为空，且大于零
        if (itemFromDTO.getDiscount() == null || itemFromDTO.getDiscount() <= 0) {
            itemFromDTO.setDiscount(1D);
        }
        // 判断属性库存是否不为空，且大于零
        if (itemFromDTO.getStock() == null || itemFromDTO.getStock() <= 0) {
            return ResultVo.fail("添加商品属性库存不能为空或小于等于零！");
        }
        // 添加商品属性
        GoodsItem goodsItem = BeanUtil.copyProperties(itemFromDTO, GoodsItem.class);
        boolean result = save(goodsItem);
        return result ? ResultVo.ok("添加商品属性成功！") : ResultVo.fail("添加商品属性失败！");
    }

    @Override
    public ResultVo updateItem(GoodsItem goodsItem) {
        // 获取要修改的商品属性id
        Long id = goodsItem.getId();
        // 判断商品属性是否存在
        GoodsItem item = getById(id);
        if (item == null) {
            return ResultVo.fail("商品属性不存在！");
        }
        // 获取商品属性归属商品id
        Long gid = goodsItem.getGid();
        // 判断商品是否存在
        Goods goods = goodsMapper.selectById(gid);
        if (goods == null) {
            return ResultVo.fail("商品不存在！");
        }
        // 判断属性颜色是否不为空
        if (StrUtil.isBlank(goodsItem.getColor())) {
            return ResultVo.fail("修改商品属性失败，颜色不能为空！！");
        }
        // 判断属性图片是否不为空
        if (StrUtil.isBlank(goodsItem.getIcon())) {
            return ResultVo.fail("修改商品属性图片不能为空！");
        }
        // 判断属性价格是否不为空，且大于零
        if (goodsItem.getPrice() == null || goodsItem.getPrice() <= 0) {
            return ResultVo.fail("修改商品属性价格不能为空或小于等于零！");
        }
        // 判断属性折扣是否不为空，且大于零
        if (goodsItem.getDiscount() == null || goodsItem.getDiscount() <= 0) {
            goodsItem.setDiscount(1D);
        }
        // 判断属性库存是否不为空，且大于零
        if (goodsItem.getStock() == null || goodsItem.getStock() <= 0) {
            return ResultVo.fail("修改商品属性库存不能为空或小于等于零！");
        }
        // 判断属性状态是否不为空
        if (goodsItem.getStatus() != null) {
            if (goodsItem.getStatus() < 0 || goodsItem.getStatus() > 1) {
                return ResultVo.fail("修改商品属性状态不存在！");
            }
        }
        // 修改商品属性
        boolean result = updateById(goodsItem);
        return result ? ResultVo.ok("修改商品属性成功！") : ResultVo.fail("修改商品属性失败！");
    }

    @Override
    public ResultVo queryItemList(Integer page, Integer size, GoodsItemQueryFromDTO itemFromDTO) {
        if (itemFromDTO == null) {
            return ResultVo.fail("商品不存在！");
        }
        // 获取商品id
        Long gid = itemFromDTO.getGid();
        // 查询商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVo.fail("商品不存在！");
        }
        // 分页
        Page<GoodsItem> goodsItemPage = new Page<>(page, size);

        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("gid", gid);

        // 判断属性颜色是否不为空
        if (StrUtil.isNotBlank(itemFromDTO.getColor())) {
            queryWrapper.like("color", itemFromDTO.getColor());
        }
        // 判断属性套餐是否不为空
        if (StrUtil.isNotBlank(itemFromDTO.getCombo())) {
            queryWrapper.like("combo", itemFromDTO.getCombo());
        }
        // 判断属性尺寸是否不为空
        if (StrUtil.isNotBlank(itemFromDTO.getSize())) {
            queryWrapper.like("size", itemFromDTO.getSize());
        }
        // 判断属性版本是否不为空
        if (StrUtil.isNotBlank(itemFromDTO.getEdition())) {
            queryWrapper.like("edition", itemFromDTO.getEdition());
        }
        // 判断是否根据销量排序
        if (StrUtil.isNotBlank(itemFromDTO.getSalesSort())) {
            switch (itemFromDTO.getSalesSort()) {
                case "Asc":
                    // 销量升序排序 1 2 3 4 5
                    queryWrapper.orderByAsc("sales");
                    break;
                case "Des":
                    // 销量降序排序 5 4 3 2 1
                    queryWrapper.orderByDesc("sales");
            }
        }
        // 判断是否根据库存排序
        if (StrUtil.isNotBlank(itemFromDTO.getStockSort())) {
            switch (itemFromDTO.getStockSort()) {
                case "Asc":
                    // 销量升序排序 1 2 3 4 5
                    queryWrapper.orderByAsc("stock");
                    break;
                case "Des":
                    // 销量降序排序 5 4 3 2 1
                    queryWrapper.orderByDesc("stock");
            }
        }
        // 判断是否根据价格排序
        if (StrUtil.isNotBlank(itemFromDTO.getPriceSort())) {
            switch (itemFromDTO.getPriceSort()) {
                case "Asc":
                    // 销量升序排序 1 2 3 4 5
                    queryWrapper.orderByAsc("price");
                    break;
                case "Des":
                    // 销量降序排序 5 4 3 2 1
                    queryWrapper.orderByDesc("price");
            }
        }
        // 判断属性状态是否不为空
        if (itemFromDTO.getStatus() != null) {
            if (itemFromDTO.getStatus() == 0 || itemFromDTO.getStatus() == 1) {
                queryWrapper.eq("status", itemFromDTO.getStatus());
            }
        }
        itemMapper.selectPage(goodsItemPage, queryWrapper);

        return ResultVo.ok(goodsItemPage);
    }

    @Override
    public ResultVo queryItemByGid(Long gid) {
        // 查询商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否为空
        if (goods == null) {
            return ResultVo.fail("商品不存在！");
        }

        // 根据商品id查询商品属性
        List<GoodsItem> goodsItems = query().eq("gid", gid).eq("status", 1).list();

        if (goodsItems.isEmpty()) {
            return ResultVo.ok("该商品暂无商品属性！");
        }

        // 获取第一个商品属性为默认选中
        GoodsItem goodsItem = goodsItems.get(0);

        // 获取默认选中属性颜色
        String color = goodsItem.getColor();

        // 设置属性可选项
        GoodsItemDTO itemDTO = setItemDTO(gid, color);

        // 获取颜色可选项
        List<GoodsItem> itemList = query().select("DISTINCT color,icon").eq("gid", gid)
                .eq("status", 1).list();
        // 获取颜色与属性图片
        ArrayList<HashMap<String, String>> colorAndIcon = setColorAndIcon(itemList);
        // 设置颜色与图片可选项
        itemDTO.setColorAndIcon(colorAndIcon);

        // 设置默认选择
        itemDTO.setDefaultOption(goodsItem);

        return ResultVo.ok(itemDTO);
    }

    @Override
    public ResultVo queryItemByColor(Long gid, String color) {
        // 判断颜色是否为空
        if (StrUtil.isBlank(color)) {
            return ResultVo.fail("属性颜色不能为空！");
        }

        GoodsItemDTO itemDTO = setItemDTO(gid, color);

        // 获取第一个商品属性
        GoodsItem item = query().eq("gid", gid).eq("color", color).list().get(0);
        if (item != null) {
            itemDTO.setDefaultOption(item);
        }

        return ResultVo.ok(itemDTO);
    }

    private ArrayList<HashMap<String, String>> setColorAndIcon(List<GoodsItem> itemList) {
        ArrayList<HashMap<String, String>> colorAndIcon = new ArrayList<>();
        for (GoodsItem item : itemList) {
            if (item != null) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("color", item.getColor());
                hashMap.put("icon", item.getIcon());
                colorAndIcon.add(hashMap);
            }
        }
        return colorAndIcon;
    }

    private GoodsItemDTO setItemDTO(Long gid, String color) {
        GoodsItemDTO itemDTO = new GoodsItemDTO();

        List<GoodsItem> itemList;

        ArrayList<String> sizeList = new ArrayList<>();
        // 不为空，根据名称选项，列举该商品属性的尺寸选项
        itemList = query().select("DISTINCT size").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getSize())) {
                sizeList.add(item.getSize());
            }
        }
        // 设置尺寸可选项
        itemDTO.setSize(sizeList);

        ArrayList<String> comboList = new ArrayList<>();
        // 根据名称选项，列举该商品属性的套餐选项
        itemList = query().select("DISTINCT combo").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getCombo())) {
                comboList.add(item.getCombo());
            }
        }
        // 设置套餐可选项
        itemDTO.setCombo(comboList);

        ArrayList<String> editionList = new ArrayList<>();
        // 不为空，根据名称选项，列举该商品属性的套餐选项
        itemList = query().select("DISTINCT edition").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getEdition())) {
                editionList.add(item.getEdition());
            }
        }
        // 设置版本可选项
        itemDTO.setEdition(editionList);

        return itemDTO;
    }
}
