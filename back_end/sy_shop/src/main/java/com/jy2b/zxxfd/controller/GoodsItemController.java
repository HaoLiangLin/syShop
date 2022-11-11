package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.GoodsItemFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.GoodsItemSaveFromDTO;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.service.IGoodsItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.jy2b.zxxfd.utils.UploadUtils.saveFile;

@RestController
@RequestMapping("/goodsItem")
@CrossOrigin
@Api(tags = "商品属性相关接口")
public class GoodsItemController {
    @Resource
    private IGoodsItemService itemService;

    @PostMapping("/uploadIcon")
    @ApiOperation(value = "上传商品属性图片", notes = "上传成功，返回存放路径")
    @PreAuthorize("hasAnyAuthority('goods:item:save')")
    public ResultVo uploadIcon(@RequestPart("file") MultipartFile file) {
        // 保存文件
        return saveFile(file, "/goods/item/icon");
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:item:save')")
    public ResultVo saveItem(@RequestBody GoodsItemSaveFromDTO itemFromDTO) {
        return itemService.saveItem(itemFromDTO);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVo updateItem(@PathVariable("id") Long id, @RequestBody GoodsItemSaveFromDTO itemSaveFromDTO) {
        return itemService.updateItem(id, itemSaveFromDTO);
    }

    @PostMapping("/list/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVo queryItemList(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody(required = false) GoodsItemQueryFromDTO itemFromDTO) {
        return itemService.queryItemList(page, size, itemFromDTO);
    }

    @GetMapping("/query/{gid}")
    public ResultVo queryItemByGid(@PathVariable("gid") Long gid) {
        return itemService.queryItemByGid(gid);
    }

    @PostMapping("/query")
    public ResultVo queryItemByColor(@RequestBody GoodsItemFromDTO itemFromDTO) {
        return itemService.queryItem(itemFromDTO);
    }
}
