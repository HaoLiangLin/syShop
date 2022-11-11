package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserCollection;
import com.jy2b.zxxfd.service.IUserCollectionService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/collection")
@CrossOrigin
@Api(tags = "商品收藏相关接口")
public class UserCollectionController {
    @Resource
    private IUserCollectionService collectionService;

    @PostMapping("/save/{gid}")
    @PreAuthorize("hasAnyAuthority('collection:save')")
    public ResultVo saveCollection(@PathVariable("gid") Long gid) {
        return collectionService.saveCollection(gid);
    }

    @DeleteMapping("/delete/{gid}")
    @PreAuthorize("hasAnyAuthority('collection:delete')")
    public ResultVo delCollection(@PathVariable("gid") Long gid) {
        return collectionService.delCollection(gid);
    }

    @GetMapping("/query")
    @PreAuthorize("hasAnyAuthority('collection:query')")
    public ResultVo queryCollection() {
        return collectionService.queryCollection();
    }

    @GetMapping("/query/{gid}")
    @PreAuthorize("hasAnyAuthority('collection:query')")
    public ResultVo queryCollectionByGid(@PathVariable("gid") Long gid) {
        return collectionService.queryCollectionByGid(gid);
    }
}
