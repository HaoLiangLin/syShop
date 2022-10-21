package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.RechargeComboSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.service.IRechargeComboService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/rechargeCombo")
@CrossOrigin
@Api(tags = "充值套餐相关接口")
public class RechargeComboController {
    @Resource
    private IRechargeComboService rechargeComboService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('recharge:combo:save')")
    public ResultVo saveCombo(@RequestBody RechargeComboSaveDTO saveDTO) {
        return rechargeComboService.saveCombo(saveDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:delete')")
    public ResultVo delCombo(@PathVariable("id") Long id) {
        return rechargeComboService.delCombo(id);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('recharge:combo:update')")
    public ResultVo updateCombo(@RequestBody RechargeCombo rechargeCombo) {
        return rechargeComboService.updateCombo(rechargeCombo);
    }

    @GetMapping("/query")
    @PreAuthorize("hasAnyAuthority('recharge:combo:query')")
    public ResultVo queryCombo() {
        return rechargeComboService.queryCombo();
    }
}
