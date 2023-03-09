package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.Role;
import com.jy2b.zxxfd.domain.dto.RoleManageDTO;
import com.jy2b.zxxfd.domain.dto.RoleSaveDTO;
import com.jy2b.zxxfd.domain.dto.RoleUpdateDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IRecycleBinService;
import com.jy2b.zxxfd.service.IRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 林武泰
 * 角色接口
 */
@RestController
@CrossOrigin
@RequestMapping("/roles")
public class RoleController {
    @Resource
    private IRoleService roleService;

    @Resource
    private IRecycleBinService recycleBinService;

    @GetMapping("/label")
    @PreAuthorize("hasAnyAuthority('role:query')")
    public ResultVO labelList() {
        return roleService.labelList();
    }

    @GetMapping("/name/{label}")
    @PreAuthorize("hasAnyAuthority('role:query')")
    public ResultVO nameByLabelList(@PathVariable("label") String label) {
        return roleService.nameByLabelList(label);
    }

    @PostMapping("/find/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('role:query')")
    public ResultVO findRole(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) RoleManageDTO roleManageDTO) {
        return roleService.findRole(page, size, roleManageDTO);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('role:update')")
    public ResultVO updateRole(@RequestBody RoleUpdateDTO roleUpdateDTO) {
        return roleService.updateRole(roleUpdateDTO);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('role:save')")
    public ResultVO saveRole(@RequestBody RoleSaveDTO roleSaveDTO) {
        return roleService.saveRole(roleSaveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('role:delete')")
    public ResultVO delRole(@PathVariable("id") Long id) {
        return roleService.delRole(id);
    }

    /*回收站接口*/
    @GetMapping("/recycleBin")
    @PreAuthorize("hasAnyAuthority('role:query')")
    public ResultVO selectRecycleBin() {
        return recycleBinService.selectRecycleBin(Role.class);
    }

    @GetMapping("/recycleBin/{name}")
    @PreAuthorize("hasAnyAuthority('role:query')")
    public ResultVO findRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.findRecycleBinByName(Role.class, name);
    }

    @DeleteMapping("/recycleBin/remove/{name}")
    @PreAuthorize("hasAnyAuthority('role:delete')")
    public ResultVO removeRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.delRecycleBinByName(Role.class, name);
    }

    @PostMapping("/recycleBin/regain/{name}")
    @PreAuthorize("hasAnyAuthority('role:save')")
    public ResultVO regainRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.regainRecycleBinByName(Role.class, name, val -> roleService.save(val));
    }
}
