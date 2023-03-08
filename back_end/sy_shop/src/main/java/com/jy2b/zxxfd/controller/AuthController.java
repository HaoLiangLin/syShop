package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.Auth;
import com.jy2b.zxxfd.domain.dto.AuthManageDTO;
import com.jy2b.zxxfd.domain.dto.AuthSaveDTO;
import com.jy2b.zxxfd.domain.dto.AuthUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IAuthService;
import com.jy2b.zxxfd.service.IRecycleBinService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/auths")
public class AuthController {
    @Resource
    private IAuthService authService;

    @Resource
    private IRecycleBinService recycleBinService;

    @GetMapping("/label")
    @PreAuthorize("hasAnyAuthority('auth:query')")
    public ResultVO labelList() {
        return authService.labelList();
    }

    @GetMapping("/name/{label}")
    @PreAuthorize("hasAnyAuthority('auth:query')")
    public ResultVO nameByLabelList(@PathVariable("label") String label) {
        return authService.nameByLabelList(label);
    }

    @PostMapping("/find/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('auth:query')")
    public ResultVO findAuth(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) AuthManageDTO authManageDTO) {
        return authService.findAuth(page, size, authManageDTO);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('auth:update')")
    public ResultVO updateAuth(@RequestBody AuthUpdateDTO authUpdateDTO) {
        return authService.updateAuth(authUpdateDTO);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('auth:save')")
    public ResultVO saveAuth(@RequestBody AuthSaveDTO authSaveDTO) {
        return authService.saveAuth(authSaveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('auth:delete')")
    public ResultVO delAuth(@PathVariable("id") Long id) {
        return authService.delAuth(id);
    }

    /*回收站接口*/
    @GetMapping("/recycleBin")
    @PreAuthorize("hasAnyAuthority('auth:query')")
    public ResultVO selectRecycleBin() {
        return recycleBinService.selectRecycleBin(Auth.class);
    }

    @GetMapping("/recycleBin/{name}")
    @PreAuthorize("hasAnyAuthority('auth:query')")
    public ResultVO findRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.findRecycleBinByName(Auth.class, name);
    }

    @DeleteMapping("/recycleBin/remove/{name}")
    @PreAuthorize("hasAnyAuthority('auth:delete')")
    public ResultVO removeRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.delRecycleBinByName(Auth.class, name);
    }

    @PostMapping("/recycleBin/regain/{name}")
    @PreAuthorize("hasAnyAuthority('auth:save')")
    public ResultVO regainRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.regainRecycleBinByName(Auth.class, name, val -> authService.save(val));
    }
}
