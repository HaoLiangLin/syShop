package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserInfo;
import com.jy2b.zxxfd.service.IUserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/userInfo")
@Api(tags = "用户信息相关接口")
public class UserInfoController {
    @Resource
    private IUserInfoService userInfoService;

    @GetMapping("/me")
    @ApiOperation(value = "查询个人信息")
    @PreAuthorize("hasAnyAuthority('userinfo:query')")
    public ResultVo me() {
        return userInfoService.queryInfoMe();
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改个人信息")
    @PreAuthorize("hasAnyAuthority('userinfo:update')")
    public ResultVo updateInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.updateInfo(userInfo);
    }

    @PostMapping("/query")
    @ApiOperation(value = "查询用户信息")
    @PreAuthorize("hasAnyAuthority('userinfo:delete')")
    public ResultVo queryInfo(@RequestBody UserInfo userInfo) {
        return userInfoService.queryInfo(userInfo);
    }

    @PostMapping("/list/{page}/{size}")
    @ApiOperation(value = "分页查询用户信息")
    @PreAuthorize("hasAnyAuthority('userinfo:delete')")
    public ResultVo queryList(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) UserInfo userInfo) {
        return userInfoService.queryInfoAll(page, size, userInfo);
    }

    @PutMapping("/updates")
    @ApiOperation(value = "修改用户信息")
    @PreAuthorize("hasAnyAuthority('userinfo:delete')")
    public ResultVo updateInfoAdmin(@RequestBody UserInfo userInfo) {
        return userInfoService.updateInfoAdmin(userInfo);
    }
}
