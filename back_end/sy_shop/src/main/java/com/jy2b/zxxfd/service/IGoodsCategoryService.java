package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 林武泰
 * 商品分类业务接口
 */
@Transactional
public interface IGoodsCategoryService extends IService<GoodsCategory> {
    /**
     * 分页查询商品分类
     * @param page 页数
     * @param size 每页数量
     * @return ResultVo
     */
    ResultVO queryCategoryList(Integer page, Integer size);

    /**
     * 查询所有一级商品分类
     * @return ResultVo
     */
    ResultVO queryCategoryByOne();

    /**
     * 查询商品分类的子分类
     * @param id 商品分类id
     * @return ResultVo
     */
    ResultVO queryCategoryChild(Long id);

    /**
     * 查询全部分类
     * @return ResultVo
     */
    ResultVO findSelectCategory();

    /**
     * 新增商品分类
     * @param goodsCategory
     * @return ResultVo
     */
    ResultVO saveCategory(GoodsCategory goodsCategory);

    /**
     * 删除商品分类
     * @param id 将要删除的商品分类id
     * @return ResultVo
     */
    ResultVO deleteCategory(Long id);

    /**
     * 修改商品分类
     * @param goodsCategory 商品分类信息
     * @return ResultVo
     */
    ResultVO updateCategory(GoodsCategory goodsCategory);

    /**
     * 上传或修改商品分类图标
     * @param id 商品分类ID
     * @param file 图标文件
     * @return ResultVO
     */
    ResultVO uploadOrUpdateCategoryIcon(Long id, MultipartFile file);
}
