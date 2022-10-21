package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.GoodsQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsUpdateFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.mapper.GoodsCategoryMapper;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IGoodsService;
import com.jy2b.zxxfd.utils.UploadUtils;
import com.jy2b.zxxfd.contants.SystemConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Override
    public ResultVo uploadImage(MultipartFile[] files) {
        if (files.length > SystemConstants.GOODS_IMAGES_LENGTH) {
            return ResultVo.fail("上传图片不能超过" + SystemConstants.GOODS_IMAGES_LENGTH + "张！");
        }

        MultipartFile[] multipartFiles = new MultipartFile[SystemConstants.GOODS_IMAGES_LENGTH];
        System.arraycopy(files, 0, multipartFiles, 0, files.length);

        return UploadUtils.saveFiles(multipartFiles, "/goods/image");
    }

    @Override
    public ResultVo saveGoods(GoodsSaveFromDTO goodsSaveFromDTO) {
        // 1. 判断商品名称是否为空
        if (StrUtil.isBlank(goodsSaveFromDTO.getName())) {
            return ResultVo.fail("商品名称不能为空！");
        }

        // 2. 判断是否选择分类
        if (goodsSaveFromDTO.getCid() == null) {
            return ResultVo.fail("商品分类不能为空！");
        }

        // 3. 判断发货地址是否为空
        if (StrUtil.isBlank(goodsSaveFromDTO.getProvince()) && StrUtil.isBlank(goodsSaveFromDTO.getCity()) && StrUtil.isBlank(goodsSaveFromDTO.getDistrict()) && StrUtil.isBlank(goodsSaveFromDTO.getAddress())) {
            return ResultVo.fail("发货地址不能未空！");
        }

        // 4. 判断商品分类是否存在
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", goodsSaveFromDTO.getCid());
        Integer categoryCount = goodsCategoryMapper.selectCount(queryWrapper);
        if (categoryCount <= 0) {
            return ResultVo.fail("商品分类不存在！");
        }

        // 5. 类型转换
        Goods goods = BeanUtil.copyProperties(goodsSaveFromDTO, Goods.class);

        // 6. 新增商品
        boolean result = save(goods);
        return result ? ResultVo.ok("新增商品成功！") : ResultVo.fail("新增商品失败！");
    }

    @Override
    public ResultVo deleteGoods(Long id) {
        boolean result = removeById(id);
        return result ? ResultVo.ok("删除商品成功！") : ResultVo.fail("删除商品失败！");
    }

    @Override
    public ResultVo updateGoods(GoodsUpdateFromDTO updateFromDTO) {
        // 获取修改的商品id
        Long id = updateFromDTO.getId();
        // 获取将要修改的商品
        Goods beforeGoods = getById(id);

        // 判断商品是否存在
        if (beforeGoods == null) {
            return ResultVo.fail("商品不存在！");
        }

        // 判断将要修改的商品分类是否存在
        if (updateFromDTO.getCid() != null) {
            GoodsCategory category = goodsCategoryMapper.selectById(updateFromDTO.getCid());
            if (category == null) {
                return ResultVo.fail("商品分类不存在！");
            }
        }

        if (StrUtil.isBlank(updateFromDTO.getName())) {
            updateFromDTO.setName(beforeGoods.getName());
        }
        if (StrUtil.isBlank(updateFromDTO.getProvince())) {
            updateFromDTO.setProvince(beforeGoods.getProvince());
        }
        if (StrUtil.isBlank(updateFromDTO.getCity())) {
            updateFromDTO.setCity(beforeGoods.getCity());
        }
        if (StrUtil.isBlank(updateFromDTO.getDistrict())) {
            updateFromDTO.setDistrict(beforeGoods.getDistrict());
        }
        if (StrUtil.isBlank(updateFromDTO.getAddress())) {
            updateFromDTO.setAddress(beforeGoods.getAddress());
        }

        // 获取商品状态
        Integer status = updateFromDTO.getStatus();
        if (status != null) {
            if (status == 1) {
                QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("gid", id).eq("status", 1);
                Integer itemCount = itemMapper.selectCount(queryWrapper);
                if (itemCount < 1) {
                    return ResultVo.fail("商品上架失败，商品属性需满足至少一个已上架！");
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
        return result ? ResultVo.ok("修改商品成功！") : ResultVo.fail("修改商品失败！");
    }

    @Override
    public ResultVo findGoodsById(Long id) {
        Goods goods = getById(id);
        return goods != null ? ResultVo.ok(goods) : ResultVo.fail("商品不存在！");
    }

    @Override
    public ResultVo findGoodsList(Integer page, Integer size, Goods goods) {
        Page<Goods> goodsPage = new Page<>();
        QueryWrapper<Goods> queryWrapper;
        if (goods != null) {
            queryWrapper = new QueryWrapper<>();
            // 判断商品id是否不为空
            if (goods.getId() != null) {
                queryWrapper.eq("id", goods.getId());
            }
            // 判断商品分类是否不为空
            if (goods.getCid() != null) {
                queryWrapper.eq("cid", goods.getCid());
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
        } else {
            queryWrapper = null;
        }
        goodsMapper.selectPage(goodsPage, queryWrapper);
        return ResultVo.ok(goodsPage);
    }

    @Override
    public ResultVo queryGoodsById(Long id) {
        Goods goods = query().eq("id", id).eq("status", 1).one();
        return goods != null ? ResultVo.ok(goods) : ResultVo.fail("商品不存在！");
    }

    @Override
    public ResultVo queryGoodsList(Integer page, Integer size, GoodsQueryFromDTO goodsFromDTO) {
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
                queryWrapper.eq("cid", cid);
            }
            // 获取销量排序
            String saleSort = goodsFromDTO.getSaleSort();
            // 判断销量排序是否不为空
            if (StrUtil.isNotBlank(saleSort)) {
                switch (saleSort) {
                    case "Asc":
                        // 销量升序排序 1 2 3 4 5
                        queryWrapper.orderByAsc("monthSale");
                        break;
                    case "Des":
                        // 销量降序排序 5 4 3 2 1
                        queryWrapper.orderByDesc("monthSale");
                }
            }
        }
        queryWrapper.eq("status", 1);
        goodsMapper.selectPage(goodsPage, queryWrapper);
        return ResultVo.ok(goodsPage);
    }
}
