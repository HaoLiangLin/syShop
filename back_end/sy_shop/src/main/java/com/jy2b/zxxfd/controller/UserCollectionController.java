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

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('collection:save')")
    public ResultVo saveCollection(@RequestParam("gid") Long gid) {
        return collectionService.saveCollection(gid);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('collection:delete')")
    public ResultVo delCollection(@RequestBody UserCollection userCollection) {
        return collectionService.delCollection(userCollection);
    }

    @GetMapping("/query")
    @PreAuthorize("hasAnyAuthority('collection:query')")
    public ResultVo queryCollection() {
        return collectionService.queryCollection();
    }
}
