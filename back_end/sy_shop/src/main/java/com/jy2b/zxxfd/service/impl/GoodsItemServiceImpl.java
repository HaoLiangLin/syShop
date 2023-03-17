package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.domain.vo.StatusCode;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IGoodsItemService;
import com.jy2b.zxxfd.utils.UploadUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林武泰
 */
@Service
public class GoodsItemServiceImpl extends ServiceImpl<GoodsItemMapper, GoodsItem> implements IGoodsItemService {
    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Override
    public ResultVO saveItem(GoodsItemSaveFromDTO itemFromDTO) {
        // 判断商品是否存在
        Long gid = itemFromDTO.getGid();
        if (gid == null) {
            return ResultVO.fail("请指定添加属性的商品");
        }
        Goods goods = goodsMapper.selectById(gid);
        if (goods == null) {
            return ResultVO.fail("商品不存在");
        }

        // 获取属性颜色
        String color = itemFromDTO.getColor();
        // 判断属性颜色是否不为空
        if (StrUtil.isBlank(color)) {
            return ResultVO.fail("添加商品属性失败，颜色不能为空");
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
            return ResultVO.fail("商品属性已存在");
        }

        // 判断属性价格是否不为空，且大于零
        if (itemFromDTO.getPrice() == null || itemFromDTO.getPrice() <= 0) {
            return ResultVO.fail("添加商品属性价格不能为空或小于等于零");
        }
        // 判断属性折扣是否不为空，且大于零
        if (itemFromDTO.getDiscount() == null || itemFromDTO.getDiscount() <= 0) {
            itemFromDTO.setDiscount(1D);
        }
        // 判断属性库存是否不为空，且大于零
        if (itemFromDTO.getStock() == null || itemFromDTO.getStock() <= 0) {
            return ResultVO.fail("添加商品属性库存不能为空或小于等于零");
        }
        // 添加商品属性
        GoodsItem goodsItem = BeanUtil.copyProperties(itemFromDTO, GoodsItem.class);

        Integer count = query().eq("color", goodsItem.getColor()).eq("combo", goodsItem.getCombo()).eq("size", goodsItem.getSize()).eq("edition", goodsItem.getEdition()).count();
        if (count > 0) {
            return ResultVO.fail("商品属性已存在");
        }

        boolean result = save(goodsItem);
        if (!result) {
            String icon = goodsItem.getIcon();
            UploadUtils.deleteFile(icon);
        }
        return result ? ResultVO.ok(goodsItem,"添加商品属性成功") : ResultVO.fail("添加商品属性失败");
    }

    @Override
    public ResultVO deleteItem(Long id) {
        // 查询商品属性
        GoodsItem goodsItem = getById(id);

        if (goodsItem == null) {
            return ResultVO.fail("商品属性不存在");
        }

        // 获取商品属性销量
        Long sales = goodsItem.getSales();
        if (sales > 0) {
            boolean result = update().set("status", 0).eq("id", id).update();
            return result ? ResultVO.ok(goodsItem, "下架商品属性成功") : ResultVO.fail("下架商品属性失败");
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
        return result ? ResultVO.ok(goodsItem, "删除商品属性成功") : ResultVO.fail("删除商品属性失败");
    }

    @Override
    public ResultVO updateItem(Long id, GoodsItemSaveFromDTO itemFromDTO) {
        // 判断商品属性是否存在
        GoodsItem item = getById(id);
        if (item == null) {
            return ResultVO.fail("商品属性不存在");
        }
        // 获取商品属性归属商品id
        Long gid = itemFromDTO.getGid();
        if (gid != null) {
            // 判断商品是否存在
            Goods goods = goodsMapper.selectById(gid);
            if (goods == null) {
                return ResultVO.fail("商品不存在");
            }
        } else {
            gid = item.getGid();
            itemFromDTO.setGid(gid);
        }

        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();

        // 判断属性颜色是否不为空
        String color = itemFromDTO.getColor();
        queryWrapper.eq("color", color);

        // 判断属性大小是否不为空
        String size = itemFromDTO.getSize();
        queryWrapper.eq("size", size);

        // 判断属性套餐是否不为空
        String combo = itemFromDTO.getCombo();
        queryWrapper.eq("combo", combo);

        // 判断属性版本是否不为空
        String edition = itemFromDTO.getEdition();
        queryWrapper.eq("edition", edition);

        // 判断属性价格是否不为空，且大于零
        if (itemFromDTO.getPrice() != null) {
            if (itemFromDTO.getPrice() <= 0) {
                return ResultVO.fail("修改商品属性价格不能为空或小于等于零");
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
            if (itemFromDTO.getStock() < 0) {
                return ResultVO.fail("修改商品属性库存不能为空或小于零");
            }
        }
        // 判断属性状态是否不为空
        if (itemFromDTO.getStatus() != null) {
            if (itemFromDTO.getStatus() < 0 || itemFromDTO.getStatus() > 1) {
                return ResultVO.fail("修改商品属性状态不存在");
            }
            if (itemFromDTO.getStatus() == 1) {
                String icon = item.getIcon();
                if (StrUtil.isBlank(icon)) {
                    return ResultVO.fail("商品属性图标未上传");
                }
                if (StrUtil.isBlank(color)) {
                    return ResultVO.fail("商品属性颜色不能为空");
                }
            }
        }

        if (StrUtil.isNotBlank(color) || StrUtil.isNotBlank(size) || StrUtil.isNotBlank(combo) || StrUtil.isNotBlank(edition)) {
            // 查询是否有重复项
            int colorCount = count(queryWrapper);
            if (colorCount > 1) {
                return ResultVO.fail("商品属性已存在");
            }
        }

        // 修改商品属性
        GoodsItem goodsItem = BeanUtil.toBean(itemFromDTO, GoodsItem.class);
        goodsItem.setId(id);

        boolean result = updateById(goodsItem);

        return result ? ResultVO.ok(null,"修改商品属性成功") : ResultVO.fail("修改商品属性失败");
    }

    @Override
    public ResultVO uploadOrUpdateItemIcon(Long id, MultipartFile file) {
        // 查询商品属性
        GoodsItem goodsItem = getById(id);
        // 判断商品属性是否存在
        if (goodsItem == null) {
            return ResultVO.fail("商品属性不存在");
        }

        // 获取旧图标
        String icon = goodsItem.getIcon();

        // 保存图标
        ResultVO resultVO = UploadUtils.saveFile(file, "/goods/item/icon");
        if (resultVO.getCode().equals(StatusCode.FAIL)) {
            return resultVO;
        }
        String fileName = resultVO.getData().toString();

        // 修改图标
        boolean updateResult = update().set("icon", fileName).eq("gid", goodsItem.getGid()).eq("color", goodsItem.getColor()).update();

        if (updateResult) {
            // 删除旧图标
            Integer iconCount = query().eq("icon", icon).count();
            if (iconCount < 1) {
                if (StrUtil.isNotBlank(icon)) {
                    UploadUtils.deleteFile(icon);
                }
            }
        } else {
            // 删除新图标
            UploadUtils.deleteFile(fileName);
        }

        return ResultVO.ok(fileName, "修改商品属性图标成功");
    }

    @Override
    public ResultVO queryItemList(Long id, GoodsItemQueryFromDTO itemFromDTO) {
        // 查询商品
        Goods goods = goodsMapper.selectById(id);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVO.fail("商品不存在");
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

        List<String> colorList = new ArrayList<>();
        List<String> sizeList = new ArrayList<>();
        List<String> comboList = new ArrayList<>();
        List<String> editionList = new ArrayList<>();

        for (GoodsItem goodsItem : itemList) {
            String color = goodsItem.getColor();
            String size1 = goodsItem.getSize();
            String combo = goodsItem.getCombo();
            String edition = goodsItem.getEdition();

            // 判断颜色是否不为空
            if (StrUtil.isNotBlank(color)) {
                // 判断选项集合是否为空
                if (colorList.isEmpty()) {
                    // 将选项直接存入
                    colorList.add(color);
                }
                // 过滤统计是否有相同值
                long count = colorList.stream().filter(c -> c.equals(color)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    colorList.add(color);
                }
            }

            // 判断大小是否不为空
            if (StrUtil.isNotBlank(size1)) {
                // 判断选项集合是否为空
                if (sizeList.isEmpty()) {
                    // 将选项直接存入
                    sizeList.add(size1);
                }
                // 过滤统计是否有相同值
                long count = sizeList.stream().filter(s -> s.equals(size1)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    sizeList.add(size1);
                }
            }

            // 判断套餐是否不为空
            if (StrUtil.isNotBlank(combo)) {
                // 判断选项集合是否为空
                if (comboList.isEmpty()) {
                    // 将选项直接存入
                    comboList.add(combo);
                }
                // 过滤统计是否有相同值
                long count = comboList.stream().filter(c -> c.equals(combo)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    comboList.add(combo);
                }
            }

            // 判断版本是否不为空
            if (StrUtil.isNotBlank(edition)) {
                // 判断选项集合是否为空
                if (editionList.isEmpty()) {
                    // 将选项直接存入
                    editionList.add(edition);
                }
                // 过滤统计是否有相同值
                long count = editionList.stream().filter(e -> e.equals(edition)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    editionList.add(edition);
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", itemList);
        if (!colorList.isEmpty()) {
            resultMap.put("colorOption", colorList);
        }
        if (!sizeList.isEmpty()) {
            resultMap.put("sizeOption", sizeList);
        }
        if (!comboList.isEmpty()) {
            resultMap.put("comboOption", comboList);
        }
        if (!editionList.isEmpty()) {
            resultMap.put("editionOption", editionList);
        }

        return ResultVO.ok(resultMap, "查询成功");
    }

    @Override
    public ResultVO queryItemListPage(Integer page, Integer size, GoodsItemQueryFromDTO itemFromDTO) {
        if (itemFromDTO == null) {
            return ResultVO.fail("商品不存在");
        }
        // 获取商品id
        Long gid = itemFromDTO.getGid();
        // 查询商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否存在
        if (goods == null) {
            return ResultVO.fail("商品不存在");
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

        List<GoodsItem> records = goodsItemPage.getRecords();

        List<String> colorList = new ArrayList<>();
        List<String> sizeList = new ArrayList<>();
        List<String> comboList = new ArrayList<>();
        List<String> editionList = new ArrayList<>();

        for (GoodsItem goodsItem : records) {
            String color = goodsItem.getColor();
            String size1 = goodsItem.getSize();
            String combo = goodsItem.getCombo();
            String edition = goodsItem.getEdition();

            // 判断颜色是否不为空
            if (StrUtil.isNotBlank(color)) {
                // 判断选项集合是否为空
                if (colorList.isEmpty()) {
                    // 将选项直接存入
                    colorList.add(color);
                }
                // 过滤统计是否有相同值
                long count = colorList.stream().filter(c -> c.equals(color)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    colorList.add(color);
                }
            }

            // 判断大小是否不为空
            if (StrUtil.isNotBlank(size1)) {
                // 判断选项集合是否为空
                if (sizeList.isEmpty()) {
                    // 将选项直接存入
                    sizeList.add(size1);
                }
                // 过滤统计是否有相同值
                long count = sizeList.stream().filter(s -> s.equals(size1)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    sizeList.add(size1);
                }
            }

            // 判断套餐是否不为空
            if (StrUtil.isNotBlank(combo)) {
                // 判断选项集合是否为空
                if (comboList.isEmpty()) {
                    // 将选项直接存入
                    comboList.add(combo);
                }
                // 过滤统计是否有相同值
                long count = comboList.stream().filter(c -> c.equals(combo)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    comboList.add(combo);
                }
            }

            // 判断版本是否不为空
            if (StrUtil.isNotBlank(edition)) {
                // 判断选项集合是否为空
                if (editionList.isEmpty()) {
                    // 将选项直接存入
                    editionList.add(edition);
                }
                // 过滤统计是否有相同值
                long count = editionList.stream().filter(e -> e.equals(edition)).count();
                // 每页相同值
                if (count == 0) {
                    // 将选项存入
                    editionList.add(edition);
                }
            }
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", goodsItemPage);
        if (!colorList.isEmpty()) {
            resultMap.put("colorOption", colorList);
        }
        if (!sizeList.isEmpty()) {
            resultMap.put("sizeOption", sizeList);
        }
        if (!comboList.isEmpty()) {
            resultMap.put("comboOption", comboList);
        }
        if (!editionList.isEmpty()) {
            resultMap.put("editionOption", editionList);
        }

        return ResultVO.ok(resultMap, "查询成功");
    }

    @Override
    public ResultVO queryItemByGid(Long gid) {
        // 查询商品
        Goods goods = goodsMapper.selectById(gid);
        // 判断商品是否为空
        if (goods == null) {
            return ResultVO.fail("商品不存在");
        }

        // 根据商品id查询商品属性
        List<GoodsItem> goodsItems = query().eq("gid", gid).eq("status", 1).list();

        if (goodsItems.isEmpty()) {
            return ResultVO.fail("该商品暂无商品属性");
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
                return ResultVO.fail("商品已售罄");
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

        return ResultVO.ok(itemDTO, "查询成功");
    }

    @Override
    public ResultVO queryItem(GoodsItemFromDTO itemFromDTO) {
        // 获取商品属性颜色
        String color = itemFromDTO.getColor();
        // 判断颜色是否为空
        if (StrUtil.isBlank(color)) {
            return ResultVO.fail("属性颜色不能为空");
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

        return ResultVO.ok(goodsItemDTO, "查询成功");

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
