package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.mapper.GoodsCategoryMapper;
import com.jy2b.zxxfd.service.IGoodsCategoryService;
import com.jy2b.zxxfd.service.IGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class GoodsCategoryServiceImpl extends ServiceImpl<GoodsCategoryMapper, GoodsCategory> implements IGoodsCategoryService {
    @Resource
    private GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    private IGoodsService goodsService;

    @Override
    public ResultVo queryCategoryList(Integer page, Integer size) {
        Page<GoodsCategory> goodsCategoryPage = new Page<>(page, size);
        goodsCategoryMapper.selectPage(goodsCategoryPage, null);
        return ResultVo.ok(goodsCategoryPage);
    }

    @Override
    public ResultVo queryCategoryByOne() {
        List<GoodsCategory> list = query().isNull("fid").list();
        return ResultVo.ok(list);
    }

    @Override
    public ResultVo queryCategoryChild(Long id) {
        List<GoodsCategory> categories = query().eq("fid", id).list();
        return ResultVo.ok(categories, "查询商品子分类成功！");
    }

    @Override
    public ResultVo saveCategory(GoodsCategory goodsCategory) {
        // 判断分类名称是否存在
        if (StrUtil.isBlank(goodsCategory.getName())) {
            return ResultVo.fail("分类名称不能为空！");
        }

        // 判断分类是否存在
        Integer count = query().eq("name", goodsCategory.getName()).count();
        if (count > 0) {
            return ResultVo.fail("该分类已存在！");
        }

        // 判断是否新增子分类
        if (goodsCategory.getFid() != null) {
            // 查询父分类是否存在
            GoodsCategory category = getById(goodsCategory.getFid());
            if (category == null) {
                return ResultVo.fail("父分类不存在，无法新增子分类！");
            }
        }

        // 新增分类
        boolean result = save(goodsCategory);
        return result ? ResultVo.ok("新增分类成功！") : ResultVo.fail("新增分类失败！");
    }

    @Override
    public ResultVo deleteCategory(Long id) {
        // 查询商品分类
        GoodsCategory goodsCategory = getById(id);

        // 判断商品分类是否存在
        if (goodsCategory == null) {
            return ResultVo.fail("商品分类不存在！");
        }

        // 判断商品分类是否已经被使用
        Integer goodsCount = goodsService.query().eq("cid", id).count();
        if (goodsCount > 0) {
            return ResultVo.fail("商品分类已经被使用，暂无法删除！");
        }

        // 删除分类
        boolean result = delCategory(id);

        return result ? ResultVo.ok("删除商品分类成功！") : ResultVo.fail("删除商品分类失败！");
    }

    @Override
    public ResultVo updateCategory(GoodsCategory goodsCategory) {
        // 获取修改的商品分类id
        Long categoryId = goodsCategory.getId();

        // 获取将要修改的商品分类
        GoodsCategory category = getById(categoryId);

        // 判断商品分类是否存在
        if (category == null) {
            return ResultVo.fail("商品分类不存在！");
        }

        // 判断分类名称是否存在
        Integer count = query().eq("name", goodsCategory.getName()).ne("id", categoryId).count();
        if (count > 0) {
            return ResultVo.fail("商品分类已存在！");
        }

        // 判断是否为子分类
        if (goodsCategory.getFid() != null) {
            // 查询父分类是否存在
            GoodsCategory fCategory = getById(goodsCategory.getFid());
            if (fCategory == null) {
                return ResultVo.fail("父分类不存在，修改商品分类失败！");
            }
        }

        // 修改商品分类
        boolean result = updateById(goodsCategory);

        return result ? ResultVo.ok("修改商品分类成功！") : ResultVo.fail("修改商品分类失败！");
    }

    /**
     * 删除商品分类
     * @param id 分类id
     * @return boolean
     */
    private boolean delCategory(Long id) {
        // 查询商品分类
        GoodsCategory goodsCategory = getById(id);

        // 判断商品分类是否存在
        if (goodsCategory == null) {
            // 不存在，无法删除
            return false;
        }

        // 判断商品分类是否已经被使用
        Integer goodsCount = goodsService.query().eq("cid", id).count();
        if (goodsCount > 0) {
            // 已经被使用，暂时无法删除
            return false;
        }

        // 判断商品分类是否存在子分类
        List<GoodsCategory> categories = query().eq("fid", id).list();
        if (!categories.isEmpty()) {
            // 存在子分类
            for (GoodsCategory category : categories) {
                // 获取分类id
                Long categoryId = category.getId();
                // 删除分类
                boolean result = delCategory(categoryId);
                // 遇到删除失败，直接返回 false
                if (!result) {
                    return false;
                }
            }
        }

        // 不存在子分类, 删除分类
        return removeById(id);
    }


}
