package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IGoodsItemService;
import com.jy2b.zxxfd.utils.UploadUtils;
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
        Long gid = itemFromDTO.getGid();
        if (gid == null) {
            return ResultVo.fail("请指定添加属性的商品");
        }
        Goods goods = goodsMapper.selectById(gid);
        if (goods == null) {
            return ResultVo.fail("商品不存在");
        }

        // 获取属性颜色
        String color = itemFromDTO.getColor();
        // 判断属性颜色是否不为空
        if (StrUtil.isBlank(color)) {
            return ResultVo.fail("添加商品属性失败，颜色不能为空");
        }

        List<GoodsItem> list = query().eq("color", color).eq("gid", gid).list();

        // 判断属性图片是否不为空
        if (list.isEmpty()) {
            if (StrUtil.isBlank(itemFromDTO.getIcon())) {
                return ResultVo.fail("添加商品属性失败，图片不能为空！");
            }
        } else {
            if (StrUtil.isNotBlank(itemFromDTO.getIcon())) {
                UploadUtils.deleteFile(itemFromDTO.getIcon());
            }
            itemFromDTO.setIcon(list.get(0).getIcon());
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
            return ResultVo.fail("商品属性已存在");
        }

        // 判断属性价格是否不为空，且大于零
        if (itemFromDTO.getPrice() == null || itemFromDTO.getPrice() <= 0) {
            return ResultVo.fail("添加商品属性价格不能为空或小于等于零");
        }
        // 判断属性折扣是否不为空，且大于零
        if (itemFromDTO.getDiscount() == null || itemFromDTO.getDiscount() <= 0) {
            itemFromDTO.setDiscount(1D);
        }
        // 判断属性库存是否不为空，且大于零
        if (itemFromDTO.getStock() == null || itemFromDTO.getStock() <= 0) {
            return ResultVo.fail("添加商品属性库存不能为空或小于等于零");
        }
        // 添加商品属性
        GoodsItem goodsItem = BeanUtil.copyProperties(itemFromDTO, GoodsItem.class);
        boolean result = save(goodsItem);
        if (!result) {
            String icon = goodsItem.getIcon();
            UploadUtils.deleteFile(icon);
        }
        return result ? ResultVo.ok(null,"添加商品属性成功") : ResultVo.fail("添加商品属性失败");
    }

    @Override
    public ResultVo deleteItem(Long id) {
        // 查询商品属性
        GoodsItem goodsItem = getById(id);

        if (goodsItem == null) {
            return ResultVo.fail("商品属性不存在");
        }

        // 获取商品属性销量
        Long sales = goodsItem.getSales();
        if (sales > 0) {
            boolean result = update().set("status", 0).eq("id", id).update();
            return result ? ResultVo.ok(null, "下架商品属性成功") : ResultVo.fail("下架商品属性失败");
        }

        // 获取商品属性颜色
        String color = goodsItem.getColor();

        // 获取商品属性图标
        String icon = goodsItem.getIcon();

        boolean result = removeById(id);

        if (result) {
            // 统计相同颜色
            Integer count = query().eq("color", color).eq("gid", goodsItem.getGid()).count();
            if (count < 1) {
                if (StrUtil.isNotBlank(icon)) {
                    UploadUtils.deleteFiles(icon);
                }
            }
        }
        return result ? ResultVo.ok(null, "删除商品属性成功") : ResultVo.fail("删除商品属性失败");
    }

    @Override
    public ResultVo updateItem(Long id, GoodsItemSaveFromDTO itemFromDTO) {
        // 判断商品属性是否存在
        GoodsItem item = getById(id);
        if (item == null) {
            return ResultVo.fail("商品属性不存在");
        }
        // 获取商品属性归属商品id
        Long gid = itemFromDTO.getGid();
        if (gid != null) {
            // 判断商品是否存在
            Goods goods = goodsMapper.selectById(gid);
            if (goods == null) {
                return ResultVo.fail("商品不存在");
            }
        } else {
            gid = item.getGid();
        }

        // 判断属性颜色是否不为空
        String color = itemFromDTO.getColor();
        if (color != null) {
            if (StrUtil.isBlank(color)) {
                return ResultVo.fail("修改商品属性失败，颜色不能为空");
            } else {
                List<GoodsItem> list = query().eq("color", color).eq("gid", gid).list();
                // 判断属性图片是否不为空
                if (list.isEmpty()) {
                    return ResultVo.fail("颜色不存在");
                }
            }
        } else {
            color = item.getColor();
        }

        // 判断属性图片是否不为空
        if (itemFromDTO.getIcon() != null) {
            if (StrUtil.isBlank(itemFromDTO.getIcon())) {
                return ResultVo.fail("修改商品属性图片不能为空");
            }
        }

        // 判断属性价格是否不为空，且大于零
        if (itemFromDTO.getPrice() != null) {
            if (itemFromDTO.getPrice() <= 0) {
                return ResultVo.fail("修改商品属性价格不能为空或小于等于零");
            }
        }
        // 判断属性折扣是否不为空，且大于零
        if (itemFromDTO.getDiscount() != null) {
            if (itemFromDTO.getDiscount() <= 0) {
                itemFromDTO.setDiscount(1D);
            }
        }
        // 判断属性库存是否不为空，且大于零
        if (itemFromDTO.getStock() != null) {
            if (itemFromDTO.getStock() <= 0) {
                return ResultVo.fail("修改商品属性库存不能为空或小于等于零");
            }
        }
        // 判断属性状态是否不为空
        if (itemFromDTO.getStatus() != null) {
            if (itemFromDTO.getStatus() < 0 || itemFromDTO.getStatus() > 1) {
                return ResultVo.fail("修改商品属性状态不存在");
            }
        }
        // 修改商品属性
        GoodsItem goodsItem = BeanUtil.toBean(itemFromDTO, GoodsItem.class);
        goodsItem.setId(id);

        boolean result = updateById(goodsItem);
        if (result) {
            if (StrUtil.isNotBlank(itemFromDTO.getIcon())) {
                boolean update = update().set("icon", itemFromDTO.getIcon()).eq("color", color).eq("gid", gid).update();
                if (!update) {
                    throw new RuntimeException("修改商品属性失败");
                }
            }
        }
        return result ? ResultVo.ok(null,"修改商品属性成功") : ResultVo.fail("修改商品属性失败");
    }

    @Override
    public ResultVo queryItemList(Long id, GoodsItemQueryFromDTO itemFromDTO) {
        // 查询商品
        Goods goods = goodsMapper.selectById(id);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVo.fail("商品不存在");
        }

        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("gid", id);

        if (itemFromDTO != null) {

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
        }
        List<GoodsItem> itemList = list(queryWrapper);

        return ResultVo.ok(itemList);
    }

    @Override
    public ResultVo queryItemListPage(Integer page, Integer size, GoodsItemQueryFromDTO itemFromDTO) {
        if (itemFromDTO == null) {
            return ResultVo.fail("商品不存在");
        }
        // 获取商品id
        Long gid = itemFromDTO.getGid();
        // 查询商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVo.fail("商品不存在");
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
            return ResultVo.fail("商品不存在");
        }

        // 根据商品id查询商品属性
        List<GoodsItem> goodsItems = query().eq("gid", gid).eq("status", 1).list();

        if (goodsItems.isEmpty()) {
            return ResultVo.fail("该商品暂无商品属性");
        }

        // 获取第一个商品属性为默认选中
        GoodsItem goodsItem = goodsItems.get(0);
        // 判断默认选择商品属性是否售罄
        if (goodsItem.getStock() < 1) {
            for (GoodsItem item : goodsItems) {
                if (item.getStock() > 0) {
                    goodsItem = item;
                    break;
                }
            }
            if (goodsItem.getStock() < 1) {
                return ResultVo.fail("商品已售罄");
            }
        }

        // 根据默认选中属性颜色，设置尺寸、套餐、版本可选项
        GoodsItemFromDTO goodsItemFromDTO = new GoodsItemFromDTO();
        goodsItemFromDTO.setGid(gid);
        goodsItemFromDTO.setColor(goodsItem.getColor());
        GoodsItemDTO itemDTO = setItemDTO(goodsItemFromDTO);

        // 获取颜色与属性图片
        ArrayList<HashMap<String, String>> colorAndIcon = setColorAndIcon(gid, goodsItem.getColor());

        // 设置默认选择
        itemDTO.setDefaultOption(goodsItem);
        // 设置颜色与图片可选项
        itemDTO.setColorAndIcon(colorAndIcon);

        return ResultVo.ok(itemDTO);
    }

    @Override
    public ResultVo queryItem(GoodsItemFromDTO itemFromDTO) {
        // 获取商品属性颜色
        String color = itemFromDTO.getColor();
        // 判断颜色是否为空
        if (StrUtil.isBlank(color)) {
            return ResultVo.fail("属性颜色不能为空");
        }

        GoodsItemDTO goodsItemDTO = setItemDTO(itemFromDTO);

        // 获取商品id
        Long gid = itemFromDTO.getGid();
        // 获取属性尺寸
        String size = itemFromDTO.getSize();
        // 获取属性套餐
        String combo = itemFromDTO.getCombo();
        // 获取属性版本
        String edition = itemFromDTO.getEdition();

        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gid", gid).eq("color", color);
        if (size != null) {
            queryWrapper.eq("size", size);
        }
        if (combo != null) {
            queryWrapper.eq("combo", combo);
        }
        if (edition != null) {
            queryWrapper.eq("edition", edition);
        }

        List<GoodsItem> goodsItems = list(queryWrapper);
        if (!goodsItems.isEmpty()) {
            goodsItemDTO.setDefaultOption(goodsItems.get(0));
        }

        // 获取颜色与属性图片
        ArrayList<HashMap<String, String>> colorAndIcon = setColorAndIcon(itemFromDTO.getGid(), itemFromDTO.getColor());
        goodsItemDTO.setColorAndIcon(colorAndIcon);

        return ResultVo.ok(goodsItemDTO);

    }

    private GoodsItemDTO setItemDTO(GoodsItemFromDTO itemFromDTO) {
        GoodsItemDTO itemDTO = new GoodsItemDTO();
        List<GoodsItem> itemList;

        // 获取商品id
        Long gid = itemFromDTO.getGid();
        // 获取商品颜色
        String color = itemFromDTO.getColor();

        // 获取商品尺寸
        String size = itemFromDTO.getSize();
        ArrayList<HashMap<String, String>> sizeList = new ArrayList<>();

        // 根据名称选项，列举该商品属性的尺寸选项
        itemList = query().select("DISTINCT size").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getSize())) {
                // 判断商品属性尺寸是否全面售罄
                Integer sizeCount = query().eq("color", color).eq("gid", gid).eq("size", item.getSize()).gt("stock", 0).count();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("size", item.getSize());
                if (StrUtil.isNotBlank(size) && item.getSize().equals(size)) {
                    hashMap.put("isOption", "已选中");
                }
                if (sizeCount <= 0) {
                    hashMap.put("isSellOut", "已售罄");
                }
                sizeList.add(hashMap);
            }
        }
        // 设置尺寸可选项
        itemDTO.setSize(sizeList);

        // 获取商品套餐
        String combo = itemFromDTO.getCombo();
        ArrayList<HashMap<String, String>> comboList = new ArrayList<>();
        // 根据颜色选项，列举该商品属性的套餐选项
        itemList = query().select("DISTINCT combo").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getCombo())) {
                // 判断商品属性套餐是否全面售罄
                Integer comboCount = query().eq("color", color).eq("gid", gid).eq("combo", item.getCombo()).gt("stock", 0).count();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("combo", item.getCombo());
                if (StrUtil.isNotBlank(combo) && item.getCombo().equals(combo)) {
                    hashMap.put("isOption", "已选中");
                }
                if (comboCount <= 0) {
                    hashMap.put("isSellOut", "已售罄");
                }
                comboList.add(hashMap);
            }
        }
        // 设置套餐可选项
        itemDTO.setCombo(comboList);

        // 获取商品版本
        String edition = itemFromDTO.getEdition();
        ArrayList<HashMap<String, String>> editionList = new ArrayList<>();
        // 根据颜色选项，列举该商品属性的版本选项
        itemList = query().select("DISTINCT edition").eq("color", color).eq("gid", gid)
                .eq("status", 1).list();
        for (GoodsItem item : itemList) {
            if (item != null && StrUtil.isNotBlank(item.getEdition())) {
                // 判断商品属性套餐是否全面售罄
                Integer editionCount = query().eq("color", color).eq("gid", gid).eq("edition", item.getEdition()).gt("stock", 0).count();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("edition", item.getEdition());
                if (StrUtil.isNotBlank(edition) && item.getEdition().equals(edition)) {
                    hashMap.put("isOption", "已选中");
                }
                if (editionCount <= 0) {
                    hashMap.put("isSellOut", "已售罄");
                }
                editionList.add(hashMap);
            }
        }
        // 设置版本可选项
        itemDTO.setEdition(editionList);

        return itemDTO;
    }

    private ArrayList<HashMap<String, String>> setColorAndIcon(Long gid, String color) {
        // 获取颜色可选项
        List<GoodsItem> itemList = query().select("DISTINCT color,icon").eq("gid", gid)
                .eq("status", 1).list();
        ArrayList<HashMap<String, String>> colorAndIcon = new ArrayList<>();
        for (GoodsItem item : itemList) {
            if (item != null) {
                // 判断商品属性颜色是否全面售罄
                Integer colorCount = query().eq("color", item.getColor()).eq("gid", gid).gt("stock", 0).count();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("color", item.getColor());
                hashMap.put("icon", item.getIcon());
                if (StrUtil.isNotBlank(color) && color.equals(item.getColor())) {
                    hashMap.put("isOption", "已选中");
                }
                if (colorCount <= 0) {
                    hashMap.put("isSellOut", "已售罄");
                }
                colorAndIcon.add(hashMap);
            }
        }
        return colorAndIcon;
    }
}
