package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.EvaluationComment;
import com.jy2b.zxxfd.domain.dto.EvaluationCommentSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IEvaluationCommentService extends IService<EvaluationComment> {
    /**
     * 新增评论
     * @param commentSaveDTO 评论信息
     * @return ResultVo
     */
    ResultVo saveComment(EvaluationCommentSaveDTO commentSaveDTO);

    /**
     * 删除评论
     * @param id 评论id
     * @return ResultVo
     */
    ResultVo deleteComment(Long id);

    /**
     * 查询评论
     * @param evaluationId 评价id
     * @return ResultVo
     */
    ResultVo queryComment(Long evaluationId);

    /**
     * 点赞评论
     * @param id 评论id
     * @return ResultVo
     */
    ResultVo likedComment(Long id);
}
