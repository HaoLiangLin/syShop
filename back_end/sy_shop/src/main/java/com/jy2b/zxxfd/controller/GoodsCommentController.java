package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.GoodsCommentSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.service.IGoodsCommentService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/comments")
@Api(tags = "商品评价相关接口")
public class GoodsCommentController {
    @Resource
    private IGoodsCommentService commentService;

    @PostMapping("/uploadImages")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo uploadImages(@RequestParam("files") MultipartFile[] files, @RequestParam("orderItemId") Long orderItemId) {
        return commentService.uploadImages(files, orderItemId);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo saveComment(@RequestBody GoodsCommentSaveDTO commentSaveDTO) {
        return commentService.saveComment(commentSaveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('comment:save')")
    public ResultVo deleteComment(@PathVariable("id") Long id) {
        return commentService.deleteComment(id);
    }

    @GetMapping("/query/{id}")
    public ResultVo queryCommentById(@PathVariable("id") Long id) {
        return commentService.queryCommentById(id);
    }
}
