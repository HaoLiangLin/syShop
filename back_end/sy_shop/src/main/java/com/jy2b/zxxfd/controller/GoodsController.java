package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.GoodsQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsUpdateFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.service.IGoodsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/goods")
@CrossOrigin
@Api(tags = "商品相关接口")
public class GoodsController {
    @Resource
    private IGoodsService goodsService;

    @PostMapping("/uploadImages")
    @PreAuthorize("hasAnyAuthority('goods:save')")
    public ResultVo uploadImages(@RequestPart("files") MultipartFile[] files) {
        return goodsService.uploadImage(files);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:save')")
    @ApiOperation(value = "新增商品")
    public ResultVo saveGoods(@RequestBody GoodsSaveFromDTO goodsSaveFromDTO) {
        return goodsService.saveGoods(goodsSaveFromDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('goods:delete')")
    @ApiOperation(value = "删除商品")
    public ResultVo deleteGoods(@PathVariable("id") Long id) {
        return goodsService.deleteGoods(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('goods:update')")
    @ApiOperation(value = "修改商品")
    public ResultVo updateGoods(@RequestBody GoodsUpdateFromDTO updateFromDTO) {
        return goodsService.updateGoods(updateFromDTO);
    }

    @GetMapping("/find")
    @PreAuthorize("hasAnyAuthority('goods:update')")
    @ApiOperation(value = "管理员：根据id查询商品")
    public ResultVo findGoodsById(@RequestParam("id") Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/list/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('goods:update')")
    @ApiOperation(value = "管理员：分页查询商品")
    public ResultVo findGoodsList(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) Goods goods) {
        return goodsService.findGoodsList(page, size, goods);
    }

    @GetMapping("/query")
    @ApiOperation(value = "查询商品")
    public ResultVo queryGoodsById(@RequestParam("id") Long id) {
        return goodsService.queryGoodsById(id);
    }

    @PostMapping("/query/{page}/{size}")
    @ApiOperation(value = "分页查询商品")
    public ResultVo queryGoodsList(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) GoodsQueryFromDTO goodsFromDTO) {
        return goodsService.queryGoodsList(page, size, goodsFromDTO);
    }
}
