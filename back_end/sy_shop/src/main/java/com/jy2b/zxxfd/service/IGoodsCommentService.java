package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsCommentSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsComment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface IGoodsCommentService extends IService<GoodsComment> {
    /**
     * 评价图片上传
     * @param files 上传文件
     * @return ResultVo
     */
    ResultVo uploadImages(MultipartFile[] files, Long orderItemId);

    /**
     * 新增订单评价
     * @param commentSaveDTO 订单评价信息
     * @return ResultVo
     */
    ResultVo saveComment(GoodsCommentSaveDTO commentSaveDTO);

    /**
     * 删除订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVo deleteComment(Long id);

    /**
     * 根据id查询订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVo queryCommentById(Long id);

    /**
     * 根据商品id查询所有评价
     * @param goodsId 商品id
     * @param sort 排序方式 0：默认排序（按点赞量排序），1：最新排序
     * @return ResultVo
     */
    ResultVo queryComment(Long goodsId, Integer sort);
}
