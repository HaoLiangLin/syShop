package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.GoodsEvaluationSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.service.IGoodsEvaluationService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/evaluation")
@Api(tags = "商品评价相关接口")
public class GoodsEvaluationController {
    @Resource
    private IGoodsEvaluationService commentService;

    @PostMapping("/uploadImages/{orderItemId}")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo uploadImages(@RequestPart("files") MultipartFile[] files, @PathVariable("orderItemId") Long orderItemId) {
        return commentService.uploadImages(files, orderItemId);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo saveEvaluation(@RequestBody GoodsEvaluationSaveDTO commentSaveDTO) {
        return commentService.saveEvaluation(commentSaveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo deleteEvaluation(@PathVariable("id") Long id) {
        return commentService.deleteEvaluation(id);
    }

    @GetMapping("/query/{id}")
    public ResultVo queryEvaluationById(@PathVariable("id") Long id) {
        return commentService.queryEvaluationById(id);
    }

    @GetMapping("/query")
    public ResultVo queryEvaluation(@RequestParam("goodsId") Long goodsId, @RequestParam(value = "sort",required = false) Integer sort) {
        return commentService.queryEvaluation(goodsId, sort);
    }

    @PutMapping("/liked/{id}")
    public ResultVo likedEvaluation(@PathVariable("id") Long id) {
        return commentService.likedEvaluation(id);
    }

}
