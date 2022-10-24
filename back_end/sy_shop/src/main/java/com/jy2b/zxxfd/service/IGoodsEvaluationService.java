package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsEvaluationSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsEvaluation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface IGoodsEvaluationService extends IService<GoodsEvaluation> {
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
    ResultVo saveEvaluation(GoodsEvaluationSaveDTO commentSaveDTO);

    /**
     * 删除订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVo deleteEvaluation(Long id);

    /**
     * 根据id查询订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVo queryEvaluationById(Long id);

    /**
     * 根据商品id查询所有评价
     * @param goodsId 商品id
     * @param sort 排序方式 0：默认排序（按点赞量排序），1：最新排序
     * @return ResultVo
     */
    ResultVo queryEvaluation(Long goodsId, Integer sort);

    /**
     * 订单评价点赞
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVo likedEvaluation(Long id);
}
