package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.GoodsItemFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemSaveFromDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IGoodsItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.jy2b.zxxfd.utils.UploadUtils.saveFile;

/**
 * @author 林武泰
 * 商品属性接口
 */
@RestController
@RequestMapping("/goodsItem")
@CrossOrigin
@Api(tags = "商品属性相关接口")
public class GoodsItemController {
    @Resource
    private IGoodsItemService itemService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('goods:item:save')")
    public ResultVO saveItem(@RequestBody GoodsItemSaveFromDTO itemFromDTO) {
        return itemService.saveItem(itemFromDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('goods:item:delete')")
    public ResultVO deleteItem(@PathVariable("id") Long id) {
        return itemService.deleteItem(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVO updateItem(@PathVariable("id") Long id, @RequestBody GoodsItemSaveFromDTO itemSaveFromDTO) {
        return itemService.updateItem(id, itemSaveFromDTO);
    }

    @PostMapping("/uploadOrUpdate/icon/{id}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVO updateItemIcon(@PathVariable("id") Long id, @RequestPart("file") MultipartFile file) {
        return itemService.uploadOrUpdateItemIcon(id, file);
    }

    @PostMapping("/all/{id}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVO queryItemList(@PathVariable("id") Long id, @RequestBody(required = false) GoodsItemQueryFromDTO itemFromDTO) {
        return itemService.queryItemList(id, itemFromDTO);
    }

    @PostMapping("/list/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('goods:item:update')")
    public ResultVO queryItemListPage(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody(required = false) GoodsItemQueryFromDTO itemFromDTO) {
        return itemService.queryItemListPage(page, size, itemFromDTO);
    }

    @GetMapping("/query/{gid}")
    public ResultVO queryItemByGid(@PathVariable("gid") Long gid) {
        return itemService.queryItemByGid(gid);
    }

    @PostMapping("/query")
    public ResultVO queryItemByColor(@RequestBody GoodsItemFromDTO itemFromDTO) {
        return itemService.queryItem(itemFromDTO);
    }
}
