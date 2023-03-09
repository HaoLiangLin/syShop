package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.EvaluationComment;
import com.jy2b.zxxfd.domain.dto.EvaluationCommentSaveDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 评价评论业务接口
 */
@Transactional
public interface IEvaluationCommentService extends IService<EvaluationComment> {
    /**
     * 新增评论
     * @param commentSaveDTO 评论信息
     * @return ResultVo
     */
    ResultVO saveComment(EvaluationCommentSaveDTO commentSaveDTO);

    /**
     * 删除评论
     * @param id 评论id
     * @return ResultVo
     */
    ResultVO deleteComment(Long id);

    /**
     * 查询评论
     * @param evaluationId 评价id
     * @return ResultVo
     */
    ResultVO queryComment(Long evaluationId);

    /**
     * 点赞评论
     * @param id 评论id
     * @return ResultVo
     */
    ResultVO likedComment(Long id);
}
