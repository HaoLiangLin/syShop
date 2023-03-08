package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IUserRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/userRoles")
public class UserRoleController {
    @Resource
    private IUserRoleService userRoleService;

    @GetMapping("/user/{roleId}")
    @PreAuthorize("hasAnyAuthority('user:role:query')")
    public ResultVO selectUserByRoleId(@PathVariable("roleId") Long roleId) {
        return userRoleService.selectUserByRoleId(roleId);
    }

    @GetMapping("/role/{userId}")
    @PreAuthorize("hasAnyAuthority('user:role:query')")
    public ResultVO selectRoleByUserId(@PathVariable("userId") Long userId) {
        return userRoleService.selectRoleByUserId(userId);
    }

    @DeleteMapping("/role/{userId}")
    @PreAuthorize("hasAnyAuthority('user:role:delete')")
    public ResultVO delUserByRoleId(@PathVariable("userId") Long userId) {
        return userRoleService.delUserByRoleId(userId);
    }

    @PutMapping("/update/{userId}/{roleId}")
    @PreAuthorize("hasAnyAuthority('user:role:update')")
    public ResultVO updateUserRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId) {
        return userRoleService.updateUserRole(userId, roleId);
    }

    @PostMapping("/save/{userId}/{roleId}")
    @PreAuthorize("hasAnyAuthority('user:role:save')")
    public ResultVO saveUserRole(@PathVariable("userId") Long userId, @PathVariable("roleId") Long roleId) {
        return userRoleService.saveUserRole(userId, roleId);
    }
}
