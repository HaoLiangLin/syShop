package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.EvaluationCommentSaveDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IEvaluationCommentService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/comments")
@CrossOrigin
@Api(tags = "评价评论相关接口")
public class EvaluationCommentController {
    @Resource
    private IEvaluationCommentService evaluationCommentService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVO saveComment(@RequestBody EvaluationCommentSaveDTO commentSaveDTO) {
        return evaluationCommentService.saveComment(commentSaveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVO deleteComment(@PathVariable("id") Long id) {
        return evaluationCommentService.deleteComment(id);
    }

    @GetMapping("/query")
    public ResultVO queryComment(@RequestParam("id") Long evaluationId) {
        return evaluationCommentService.queryComment(evaluationId);
    }

    @PutMapping("/liked/{id}")
    public ResultVO likedComment(@PathVariable("id") Long id) {
        return evaluationCommentService.likedComment(id);
    }
}
