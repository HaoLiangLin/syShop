package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.GoodsCategory;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IGoodsCategoryService;
import com.jy2b.zxxfd.utils.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 林武泰
 * 商品分类接口
 */
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
    public ResultVO queryCategoryList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        return goodsCategoryService.queryCategoryList(page, size);
    }

    @GetMapping("/query/one")
    @ApiOperation(value = "查询所有一级分类")
    public ResultVO queryCategoryByOne() {
        return goodsCategoryService.queryCategoryByOne();
    }

    @GetMapping("/query/child/{id}")
    @ApiOperation(value = "查询商品分类的子分类")
    public ResultVO queryCategoryChild(@PathVariable("id") Long id) {
        return goodsCategoryService.queryCategoryChild(id);
    }

    @GetMapping("/select")
    public ResultVO findSelectCategory() {
        return goodsCategoryService.findSelectCategory();
    }

    @PostMapping("/uploadOrUpdateIcon/{id}")
    @ApiOperation(value = "上传或修改分类图标", notes = "上传成功，返回存放路径")
    @PreAuthorize("hasAnyAuthority('goods:category:upadte')")
    public ResultVO uploadIcon(@PathVariable("id") Long id, @RequestPart("file") MultipartFile file) {
        return goodsCategoryService.uploadOrUpdateCategoryIcon(id, file);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:category:save')")
    @ApiOperation(value = "新增商品分类")
    public ResultVO saveCategory(@RequestBody GoodsCategory goodsCategory) {
        return goodsCategoryService.saveCategory(goodsCategory);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('goods:category:delete')")
    @ApiOperation(value = "删除商品分类")
    public ResultVO deleteCategory(@PathVariable("id") Long id) {
        return goodsCategoryService.deleteCategory(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('goods:category:update')")
    @ApiOperation(value = "修改商品分类")
    public ResultVO updateCategory(@RequestBody GoodsCategory goodsCategory) {
        return goodsCategoryService.updateCategory(goodsCategory);
    }
}
