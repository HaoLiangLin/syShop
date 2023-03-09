package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Province;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.GoodsCategoryMapper;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IGoodsService;
import com.jy2b.zxxfd.service.IProvinceService;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UploadUtils;
import com.jy2b.zxxfd.contants.SystemConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Resource
    private IProvinceService provinceService;

    @Override
    public ResultVO uploadImage(MultipartFile[] files) {
        if (files.length > SystemConstants.GOODS_IMAGES_LENGTH) {
            return ResultVO.fail("上传图片不能超过" + SystemConstants.GOODS_IMAGES_LENGTH + "张");
        }

        MultipartFile[] multipartFiles = new MultipartFile[SystemConstants.GOODS_IMAGES_LENGTH];
        System.arraycopy(files, 0, multipartFiles, 0, files.length);

        return UploadUtils.saveFiles(multipartFiles, "/goods/image");
    }

    @Override
    public ResultVO saveGoods(GoodsSaveFromDTO goodsSaveFromDTO) {
        // 1. 判断商品名称是否为空
        if (StrUtil.isBlank(goodsSaveFromDTO.getName())) {
            return ResultVO.fail("商品名称不能为空");
        }

        // 2. 判断是否选择分类
        if (goodsSaveFromDTO.getCid() == null) {
            return ResultVO.fail("商品分类不能为空");
        }

        // 3 判断发货地址是否正确
        // 获取发货省份
        String province = goodsSaveFromDTO.getProvince();
        // 省份代码
        String provinceCode = null;
        // 判断省份是否存在
        if (StrUtil.isNotBlank(province)) {
            List<Province> provinceList = provinceService.selectAllProvince();
            // 过滤每一个省份，并收集到正确的
            List<Province> filterResult = provinceList.stream().filter(p -> p.getName().equals(province)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("省份不存在");
            }
            provinceCode = filterResult.get(0).getProvince();
        } else {
            return ResultVO.fail("发货省份不能为空");
        }
        // 获取发货城市
        String city = goodsSaveFromDTO.getCity();
        // 城市代码
        String cityCode = null;
        // 判断城市是否存在
        if (StrUtil.isNotBlank(city)) {
            List<Province> cityList = provinceService.selectAllCityByProvince(provinceCode);
            // 判断是否不为直辖市
            if (!cityList.isEmpty()) {
                String finalCity = city;
                // 过滤每一个城市，并收集到正确的
                List<Province> filterResult = cityList.stream().filter(c -> c.getName().equals(finalCity)).collect(Collectors.toList());
                if (filterResult.isEmpty()) {
                    return ResultVO.fail("城市不存在");
                }
                cityCode = filterResult.get(0).getCity();
            } else {
                city = province;
            }
        } else {
            return ResultVO.fail("发货城市不能为空");
        }
        // 获取发货区县
        String area = goodsSaveFromDTO.getDistrict();
        // 判断城区是否存在
        if (StrUtil.isNotBlank(area)) {
            List<Province> areaList = provinceService.selectAllAreaByProvinceAndCity(provinceCode, cityCode);
            // 过滤出正确城区
            List<Province> filterResult = areaList.stream().filter(a -> a.getName().equals(area)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("城区不存在");
            }
        } else {
            return ResultVO.fail("发货城区不能为空");
        }

        // 4. 判断商品分类是否存在
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", goodsSaveFromDTO.getCid());
        Integer categoryCount = goodsCategoryMapper.selectCount(queryWrapper);
        if (categoryCount <= 0) {
            return ResultVO.fail("商品分类不存在");
        }

        // 5. 类型转换
        Goods goods = BeanUtil.copyProperties(goodsSaveFromDTO, Goods.class);
        goods.setProvince(province);
        goods.setCity(city);
        goods.setDistrict(area);

        // 6. 新增商品
        boolean result = save(goods);

        if (!result) {
            String images = goods.getImages();
            UploadUtils.deleteFiles(images);
        }
        return result ? ResultVO.ok(goods,"新增商品成功") : ResultVO.fail("新增商品失败");
    }

    @Override
    public ResultVO deleteGoods(Long id) {
        Goods goods = getById(id);

        if (goods == null) {
            return ResultVO.fail("商品已删除");
        }

        if (goods.getStatus() == 1) {
            return ResultVO.fail("上架商品暂无法删除");
        }

        if (goods.getSale() > 0) {
            boolean result = update().set("status", 2).eq("id", id).update();
            return result ? ResultVO.ok(null, "商品下架成功") : ResultVO.fail("商品下架失败");
        }

        String images = goods.getImages();

        boolean result = removeById(id);
        if (result) {
            UploadUtils.deleteFiles(images);
        }
        return result ? ResultVO.ok(goods,"删除商品成功") : ResultVO.fail("删除商品失败");
    }

    @Override
    public ResultVO updateGoods(GoodsUpdateFromDTO updateFromDTO) {
        // 获取修改的商品id
        Long id = updateFromDTO.getId();
        // 获取将要修改的商品
        Goods beforeGoods = getById(id);

        // 判断商品是否存在
        if (beforeGoods == null) {
            return ResultVO.fail("商品不存在");
        }

        // 判断将要修改的商品分类是否存在
        if (updateFromDTO.getCid() != null) {
            GoodsCategory category = goodsCategoryMapper.selectById(updateFromDTO.getCid());
            if (category == null) {
                return ResultVO.fail("商品分类不存在");
            }
        }

        if (updateFromDTO.getName() != null) {
            if (StrUtil.isBlank(updateFromDTO.getName())) {
                return ResultVO.fail("商品名称不能为空");
            }
        }

        // 获取发货省份
        String province = updateFromDTO.getProvince();
        if (province == null) {
            province = beforeGoods.getProvince();
        }
        // 获取发货城市
        String city = updateFromDTO.getCity();
        if (city == null) {
            city = beforeGoods.getCity();
        }
        // 获取发货区县
        String area = updateFromDTO.getDistrict();
        if (area == null) {
            area = beforeGoods.getDistrict();
        }

        // 省份代码
        String provinceCode = null;
        // 判断省份是否存在
        if (StrUtil.isNotBlank(province)) {
            List<Province> provinceList = provinceService.selectAllProvince();
            // 过滤每一个省份，并收集到正确的
            String finalProvince = province;
            List<Province> filterResult = provinceList.stream().filter(p -> p.getName().equals(finalProvince)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("省份不存在");
            }
            provinceCode = filterResult.get(0).getProvince();
        }
        // 城市代码
        String cityCode = null;
        // 判断城市是否存在
        if (StrUtil.isNotBlank(city)) {
            List<Province> cityList = provinceService.selectAllCityByProvince(provinceCode);
            // 判断是否不为直辖市
            if (!cityList.isEmpty()) {
                String finalCity = city;
                // 过滤每一个城市，并收集到正确的
                List<Province> filterResult = cityList.stream().filter(c -> c.getName().equals(finalCity)).collect(Collectors.toList());
                if (filterResult.isEmpty()) {
                    return ResultVO.fail("城市不存在");
                }
                cityCode = filterResult.get(0).getCity();
            } else {
                updateFromDTO.setCity(province);
            }
        }
        // 判断城区是否存在
        if (StrUtil.isNotBlank(area)) {
            List<Province> areaList = provinceService.selectAllAreaByProvinceAndCity(provinceCode, cityCode);
            // 过滤出正确城区
            String finalArea = area;
            List<Province> filterResult = areaList.stream().filter(a -> a.getName().equals(finalArea)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("城区不存在");
            }
        }

        if (updateFromDTO.getAddress() != null) {
            if (StrUtil.isBlank(updateFromDTO.getAddress())) {
                return ResultVO.fail("发货详细地址不能为空");
            }
        }
        if (updateFromDTO.getRecommend() == null || updateFromDTO.getRecommend() > 1 || updateFromDTO.getRecommend() < 0) {
            updateFromDTO.setRecommend(beforeGoods.getRecommend());
        }
        if (StrUtil.isNotBlank(updateFromDTO.getImages())) {
            String images = beforeGoods.getImages();
            UploadUtils.deleteFiles(images);
        }

        // 获取商品状态
        Integer status = updateFromDTO.getStatus();
        if (status != null) {
            if (status == 1) {
                QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("gid", id).eq("status", 1);
                Integer itemCount = itemMapper.selectCount(queryWrapper);
                if (itemCount < 1) {
                    return ResultVO.fail("商品上架失败，商品属性需满足至少一个已上架");
                }
            }

            switch (status) {
                case 0: case 1: case 2:break;
                default:updateFromDTO.setStatus(beforeGoods.getStatus());
            }
        }

        Goods goods = BeanUtil.copyProperties(updateFromDTO, Goods.class);

        // 修改商品
        boolean result = updateById(goods);
        return result ? ResultVO.ok(null,"修改商品成功") : ResultVO.fail("修改商品失败");
    }

    @Override
    public ResultVO findGoodsById(Long id) {
        Goods goods = getById(id);
        if (goods != null) {
            GoodsFindDTO goodsFindDTO = BeanUtil.copyProperties(goods, GoodsFindDTO.class);

            Long cid = goods.getCid();
            String name = goodsCategoryMapper.selectById(cid).getName();
            goodsFindDTO.setCname(name);

            return ResultVO.ok(goodsFindDTO, "查询成功");
        }
        return ResultVO.fail("商品不存在");
    }

    @Override
    public ResultVO findGoodsList(Integer page, Integer size, Goods goods) {
        Page<Goods> goodsPage = new Page<>();
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (goods != null) {
            // 判断商品id是否不为空
            if (goods.getId() != null) {
                queryWrapper.eq("id", goods.getId());
            }
            // 判断商品分类是否不为空
            if (goods.getCid() != null) {
                List<Long> cidList = getSunCid(goods.getCid());
                if (!cidList.isEmpty()) {
                    queryWrapper.in("cid", cidList);
                } else {
                    queryWrapper.eq("cid", goods.getCid());
                }
            }
            // 判断商品发货省份是否不为空
            if (StrUtil.isNotBlank(goods.getProvince())) {
                queryWrapper.eq("province", goods.getProvince());
            }
            // 判断商品发货城市是否不为空
            if (StrUtil.isNotBlank(goods.getCity())) {
                queryWrapper.eq("city", goods.getCity());
            }
            // 判断商品发货地区是否不为空
            if (StrUtil.isNotBlank(goods.getDistrict())) {
                queryWrapper.eq("district", goods.getDistrict());
            }
            // 判断是否推荐
            if (goods.getRecommend() != null && goods.getRecommend() == 0 && goods.getRecommend() == 1) {
                queryWrapper.eq("recommend", goods.getRecommend());
            }

            if (goods.getWarrantyTime() != null) {
                queryWrapper.eq("warranty_time", goods.getWarrantyTime());
            }
            if (goods.getRefundTime() != null) {
                queryWrapper.eq("refund_time", goods.getRefundTime());
            }
            if (goods.getChangerTime() != null) {
                queryWrapper.eq("changer_time", goods.getChangerTime());
            }
            if (goods.getStatus() != null) {
                queryWrapper.eq("status", goods.getStatus());
            }
        }
        queryWrapper.orderByDesc("sale");

        goodsMapper.selectPage(goodsPage, queryWrapper);

        List<Goods> records = goodsPage.getRecords();
        List<GoodsFindDTO> goodsFindDTOS = new ArrayList<>();
        for (Goods record : records) {
            GoodsFindDTO goodsFindDTO = BeanUtil.copyProperties(record, GoodsFindDTO.class);

            Long cid = record.getCid();
            String name = goodsCategoryMapper.selectById(cid).getName();
            goodsFindDTO.setCname(name);

            goodsFindDTOS.add(goodsFindDTO);
        }


        Page<GoodsFindDTO> goodsFindDTOPage = new Page<>();
        goodsFindDTOPage.setRecords(goodsFindDTOS);
        goodsFindDTOPage.setSize(goodsPage.getSize());
        goodsFindDTOPage.setCountId(goodsPage.getCountId());
        goodsFindDTOPage.setCurrent(goodsPage.getCurrent());
        goodsFindDTOPage.setHitCount(goodsPage.isHitCount());
        goodsFindDTOPage.setMaxLimit(goodsPage.getMaxLimit());
        goodsFindDTOPage.setTotal(goodsPage.getTotal());
        goodsFindDTOPage.setSearchCount(goodsPage.isSearchCount());
        goodsFindDTOPage.setOrders(goodsPage.getOrders());

        return ResultVO.ok(goodsFindDTOPage, "查询成功");
    }

    @Override
    public ResultVO queryGoodsById(Long id) {
        Goods goods = query().eq("id", id).eq("status", 1).one();
        GoodsItem goodsItem = itemMapper.selectList(new QueryWrapper<GoodsItem>().eq("gid", id).eq("status", 1)).get(0);
        GoodsDTO goodsDTO = new GoodsDTO(goods, goodsItem.getPrice(), goodsItem.getDiscount());
        return goods != null ? ResultVO.ok(goodsDTO, "查询成功") : ResultVO.fail("商品不存在");
    }

    @Override
    public ResultVO queryGoodsList(Integer page, Integer size, GoodsQueryFromDTO goodsFromDTO) {
        Page<Goods> goodsPage = new Page<>(page, size);
        QueryWrapper<Goods> queryWrapper = new QueryWrapper<>();
        if (goodsFromDTO != null) {
            // 获取商品名称
            String name = goodsFromDTO.getName();
            // 判断商品名称是否不为空
            if (StrUtil.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            // 获取商品分类
            Long cid = goodsFromDTO.getCid();
            // 判断商品分类是否不为空
            if (cid != null) {
                List<Long> cidList = getSunCid(cid);
                if (!cidList.isEmpty()) {
                    queryWrapper.in("cid", cidList);
                } else {
                    queryWrapper.eq("cid", cid);
                }
            }
            // 获取商品推荐
            Integer recommend = goodsFromDTO.getRecommend();
            if (recommend != null) {
                switch (recommend) {
                    case 0: case 1:queryWrapper.eq("recommend", recommend);break;
                }
            }
            // 是否新品
            Integer isNew = goodsFromDTO.getIsNew();
            if (isNew != null && isNew == 1) {
                // 获取当前时间
                Date date = new Date();
                String now = TimeUtils.dateToStringTime(date);
                // 获取七天前的时间
                long before7 = date.getTime() - (7 * 24 * 60 * 60 * 1000);
                String before = TimeUtils.dateToStringTime(new Date(before7));

                queryWrapper.le("shelves_time", now).ge("shelves_time", before);
            }

            // 获取销量排序
            String saleSort = goodsFromDTO.getSaleSort();
            // 判断销量排序是否不为空
            if (StrUtil.isNotBlank(saleSort)) {
                switch (saleSort) {
                    case "Asc":
                        // 销量升序排序 1 2 3 4 5
                        queryWrapper.orderByAsc("sale");
                        break;
                    case "Des":
                        // 销量降序排序 5 4 3 2 1
                        queryWrapper.orderByDesc("sale");
                }
            }
        }
        queryWrapper.eq("status", 1);
        goodsMapper.selectPage(goodsPage, queryWrapper);

        List<Goods> records = goodsPage.getRecords();
        if (!records.isEmpty()) {
            ArrayList<GoodsDTO> goodsDTOS = new ArrayList<>();
            for (Goods goods : records) {
                // 获取商品id
                Long id = goods.getId();
                GoodsItem goodsItem = itemMapper.selectList(new QueryWrapper<GoodsItem>().eq("gid", id).eq("status", 1)).get(0);
                GoodsDTO goodsDTO = new GoodsDTO(goods, goodsItem.getPrice(), goodsItem.getDiscount());
                goodsDTOS.add(goodsDTO);
            }

            if (goodsFromDTO != null) {
                // 获取价格排序
                String priceSort = goodsFromDTO.getPriceSort();
                // 判断价格排序是否不为空
                if (StrUtil.isNotBlank(priceSort)) {
                    switch (priceSort) {
                        case "Asc":
                            // 价格升序排序 1 2 3 4 5
                            goodsDTOS.sort(Comparator.comparing(GoodsDTO::getPrice));
                            break;
                        case "Des":
                            // 价格降序排序 5 4 3 2 1
                            goodsDTOS.sort(Comparator.comparing(GoodsDTO::getPrice).reversed());
                    }
                }
            }

            Page<GoodsDTO> goodsDTOPage = new Page<>();
            goodsDTOPage.setRecords(goodsDTOS);
            goodsDTOPage.setSize(goodsPage.getSize());
            goodsDTOPage.setCountId(goodsPage.getCountId());
            goodsDTOPage.setCurrent(goodsPage.getCurrent());
            goodsDTOPage.setHitCount(goodsPage.isHitCount());
            goodsDTOPage.setMaxLimit(goodsPage.getMaxLimit());
            goodsDTOPage.setTotal(goodsPage.getTotal());
            goodsDTOPage.setSearchCount(goodsPage.isSearchCount());
            goodsDTOPage.setOrders(goodsPage.getOrders());

            return ResultVO.ok(goodsDTOPage, "查询成功");
        }

        return ResultVO.ok(goodsPage, "查询成功");
    }

    private List<Long> getSunCid(Long cid) {
        ArrayList<Long> categoryIds = new ArrayList<>();
        // 获取父类的子类
        List<GoodsCategory> categoryList = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("fid", cid));
        // 判断是不存在子类
        if (categoryList.isEmpty()) {
            categoryIds.add(cid);
            // 返回空集合
            return categoryIds;
        }
        // 存在子类
        for (GoodsCategory goodsCategory : categoryList) {
            // 循环每一个子类，并拿到id
            categoryIds.add(goodsCategory.getId());
            // 判断是否存在子类
            List<Long> sunCidList = getSunCid(goodsCategory.getId());
            // 将子类id存入
            categoryIds.addAll(sunCidList);
        }

        return categoryIds;
    }
}
