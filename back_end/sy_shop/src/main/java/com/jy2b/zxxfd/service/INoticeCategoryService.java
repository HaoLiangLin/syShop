package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.NoticeCategory;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface INoticeCategoryService extends IService<NoticeCategory> {
    /**
     * 新增公告类型
     * @param noticeCategory
     * @return ResultVo
     */
    ResultVO saveCategory(NoticeCategory noticeCategory);

    /**
     * 删除公告类型
     * @param id 将要删除的公告类型id
     * @return ResultVo
     */
    ResultVO deleteCategory(Long id);

    /**
     * 修改公告类型
     * @param noticeCategory 公告类型信息
     * @return ResultVo
     */
    ResultVO updateCategory(NoticeCategory noticeCategory);

    /**
     * 分页查询公告类型
     * @param page 页数
     * @param size 每页数量
     * @return ResultVo
     */
    ResultVO queryCategoryList(Integer page, Integer size);

    /**
     * 查询所有一级公告类型
     * @return ResultVo
     */
    ResultVO queryCategoryByOne();

    /**
     * 查询公告类型分类的子分类
     * @param id 商品分类id
     * @return ResultVo
     */
    ResultVO queryCategoryChild(Long id);

    /**
     * 查询全部公告类型
     * @return ResultVo
     */
    ResultVO findSelectCategory();
}
