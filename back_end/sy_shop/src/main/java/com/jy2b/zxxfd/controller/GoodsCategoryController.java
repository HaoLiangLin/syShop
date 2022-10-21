package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.service.IGoodsCategoryService;
import com.jy2b.zxxfd.utils.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/goodsCategory")
@CrossOrigin
@Api(tags = "商品分类相关接口")
public class GoodsCategoryController {
    @Resource
    private IGoodsCategoryService goodsCategoryService;

    @GetMapping("/list/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('goods:category:update')")
    @ApiOperation(value = "分页查询分类信息")
    public ResultVo queryCategoryList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return goodsCategoryService.queryCategoryList(page, size);
    }

    @GetMapping("/query/one")
    @ApiOperation(value = "查询所有一级分类")
    public ResultVo queryCategoryByOne() {
        return goodsCategoryService.queryCategoryByOne();
    }

    @GetMapping("/query/child/{id}")
    @ApiOperation(value = "查询商品分类的子分类")
    public ResultVo queryCategoryChild(@PathVariable("id") Long id) {
        return goodsCategoryService.queryCategoryChild(id);
    }

    @PostMapping("/uploadIcon")
    @ApiOperation(value = "上传分类图标", notes = "上传成功，返回存放路径")
    @PreAuthorize("hasAnyAuthority('goods:category:save')")
    public ResultVo uploadIcon(@RequestPart("file") MultipartFile file) {
        // 保存文件
        return UploadUtils.saveFile(file, "/goods/category/icon");
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:category:save')")
    @ApiOperation(value = "新增商品分类")
    public ResultVo saveCategory(@RequestBody GoodsCategory goodsCategory) {
        return goodsCategoryService.saveCategory(goodsCategory);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('goods:category:delete')")
    @ApiOperation(value = "删除商品分类")
    public ResultVo deleteCategory(@PathVariable("id") Long id) {
        return goodsCategoryService.deleteCategory(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('goods:category:update')")
    @ApiOperation(value = "修改商品分类")
    public ResultVo updateCategory(@RequestBody GoodsCategory goodsCategory) {
        return goodsCategoryService.updateCategory(goodsCategory);
    }
}
