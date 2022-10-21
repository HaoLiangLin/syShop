package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsCategory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IGoodsCategoryService extends IService<GoodsCategory> {
    /**
     * 分页查询商品分类
     * @param page 页数
     * @param size 每页数量
     * @return ResultVo
     */
    ResultVo queryCategoryList(Integer page, Integer size);

    /**
     * 查询所有一级商品分类
     * @return ResultVo
     */
    ResultVo queryCategoryByOne();

    /**
     * 查询商品分类的子分类
     * @param id 商品分类id
     * @return ResultVo
     */
    ResultVo queryCategoryChild(Long id);

    /**
     * 新增商品分类
     * @param goodsCategory
     * @return ResultVo
     */
    ResultVo saveCategory(GoodsCategory goodsCategory);

    /**
     * 删除商品分类
     * @param id 将要删除的商品分类id
     * @return ResultVo
     */
    ResultVo deleteCategory(Long id);

    /**
     * 修改商品分类
     * @param goodsCategory 商品分类信息
     * @return ResultVo
     */
    ResultVo updateCategory(GoodsCategory goodsCategory);
}
