package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsEvaluationSaveDTO;
import com.jy2b.zxxfd.domain.GoodsEvaluation;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 林武泰
 * 商品评价业务接口
 */
@Transactional
public interface IGoodsEvaluationService extends IService<GoodsEvaluation> {
    /**
     * 评价图片上传
     * @param files 上传文件
     * @return ResultVo
     */
    ResultVO uploadImages(MultipartFile[] files, Long orderItemId);

    /**
     * 新增订单评价
     * @param commentSaveDTO 订单评价信息
     * @return ResultVo
     */
    ResultVO saveEvaluation(GoodsEvaluationSaveDTO commentSaveDTO);

    /**
     * 删除订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVO deleteEvaluation(Long id);

    /**
     * 根据id查询订单评价
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVO queryEvaluationById(Long id);

    /**
     * 根据商品id查询所有评价
     * @param goodsId 商品id
     * @param sort 排序方式 0：默认排序（按点赞量排序），1：最新排序
     * @return ResultVo
     */
    ResultVO queryEvaluation(Long goodsId, Integer sort);

    /**
     * 订单评价点赞
     * @param id 订单评价id
     * @return ResultVo
     */
    ResultVO likedEvaluation(Long id);
}
