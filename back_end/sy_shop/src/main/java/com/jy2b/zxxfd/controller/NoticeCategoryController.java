package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.NoticeCategory;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.service.INoticeCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/noticeCategory")
@CrossOrigin
@Api(tags = "公告类型相关接口")
public class NoticeCategoryController {
    @Resource
    private INoticeCategoryService noticeCategoryService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:category:save')")
    @ApiOperation(value = "新增公告类型")
    public ResultVo saveCategory(@RequestBody NoticeCategory noticeCategory) {
        return noticeCategoryService.saveCategory(noticeCategory);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('goods:category:delete')")
    @ApiOperation(value = "删除公告类型")
    public ResultVo deleteCategory(@PathVariable("id") Long id) {
        return noticeCategoryService.deleteCategory(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('goods:category:update')")
    @ApiOperation(value = "修改公告类型")
    public ResultVo updateCategory(@RequestBody NoticeCategory noticeCategory) {
        return noticeCategoryService.updateCategory(noticeCategory);
    }

    @GetMapping("/list/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('goods:category:update')")
    @ApiOperation(value = "分页查询公告类型信息")
    public ResultVo queryCategoryList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return noticeCategoryService.queryCategoryList(page, size);
    }

    @GetMapping("/query/one")
    @ApiOperation(value = "查询所有一级公告类型")
    public ResultVo queryCategoryByOne() {
        return noticeCategoryService.queryCategoryByOne();
    }

    @GetMapping("/query/child/{id}")
    @ApiOperation(value = "查询公告类型的子公告类型")
    public ResultVo queryCategoryChild(@PathVariable("id") Long id) {
        return noticeCategoryService.queryCategoryChild(id);
    }

    @GetMapping("/select")
    public ResultVo findSelectCategory() {
        return noticeCategoryService.findSelectCategory();
    }
}
