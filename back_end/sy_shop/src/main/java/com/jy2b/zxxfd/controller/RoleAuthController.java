package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.RoleAuthDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IRoleAuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/roleAuths")
public class RoleAuthController {
    @Resource
    private IRoleAuthService roleAuthService;

    @GetMapping("/auth/{roleId}")
    @PreAuthorize("hasAnyAuthority('role:auth:query')")
    public ResultVO selectAuthByRoleId(@PathVariable("roleId") Long roleId) {
        return roleAuthService.selectAuthByRoleId(roleId);
    }

    @GetMapping("/role/{authId}")
    @PreAuthorize("hasAnyAuthority('role:auth:query')")
    public ResultVO selectRoleByAuthId(@PathVariable("authId") Long authId) {
        return roleAuthService.selectRoleByAuthId(authId);
    }

    @DeleteMapping("/delete/{roleId}/{authId}")
    @PreAuthorize("hasAnyAuthority('role:auth:delete')")
    public ResultVO delAuthByRoleId(@PathVariable("roleId") Long roleId,@PathVariable("authId")  Long authId) {
        return roleAuthService.delAuthByRoleId(roleId, authId);
    }

    @DeleteMapping("/auth/{roleId}")
    @PreAuthorize("hasAnyAuthority('role:auth:delete')")
    public ResultVO delAllAuthByRoleId(@PathVariable("roleId") Long roleId) {
        return roleAuthService.delAllAuthByRoleId(roleId);
    }

    @DeleteMapping("/role/{authId}")
    @PreAuthorize("hasAnyAuthority('role:auth:delete')")
    public ResultVO delAllRoleByAuthId(@PathVariable("authId") Long authId) {
        return roleAuthService.delAllRoleByAuthId(authId);
    }

    @PostMapping("/save/{roleId}/{authId}")
    @PreAuthorize("hasAnyAuthority('role:auth:save')")
    public ResultVO saveRoleAuth(@PathVariable("roleId") Long roleId,@PathVariable("authId")  Long authId) {
        return roleAuthService.saveRoleAuth(roleId, authId);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('role:auth:save')")
    public ResultVO batchSaveRoleAuth(@RequestBody RoleAuthDTO roleAuthDTO) {
        return roleAuthService.batchSaveRoleAuth(roleAuthDTO);
    }
}
