package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.RechargeCombo;
import com.jy2b.zxxfd.domain.dto.RechargeComboManageDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.domain.vo.StatusCode;
import com.jy2b.zxxfd.service.IRechargeComboService;
import com.jy2b.zxxfd.service.IRecycleBinService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 林武泰
 * 充值套餐接口
 */
@RestController
@CrossOrigin
@RequestMapping("/rechargeCombo")
public class RechargeComboController {
    @Resource
    private IRechargeComboService rechargeComboService;

    @Resource
    private IRecycleBinService recycleBinService;

    @GetMapping("/all")
    public ResultVO selectAllRechargeCombo() {
        List<RechargeCombo> rechargeCombos = rechargeComboService.selectAllRechargeCombo();
        return ResultVO.ok(rechargeCombos, "查询成功");
    }

    @GetMapping("/query/{rechargeComboId}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:query')")
    public ResultVO selectRechargeComboById(@PathVariable("rechargeComboId") Long rechargeComboId) {
        RechargeCombo rechargeCombo = rechargeComboService.selectRechargeComboById(rechargeComboId);
        return ResultVO.ok(rechargeCombo, "查询成功");
    }

    @PutMapping("/update/{rechargeComboId}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:update')")
    public ResultVO updateRechargeCombo(@PathVariable("rechargeComboId") Long rechargeComboId, @RequestBody RechargeComboManageDTO rechargeComboManageDTO) {
        return rechargeComboService.updateRechargeCombo(rechargeComboId, rechargeComboManageDTO);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('recharge:combo:save')")
    public ResultVO saveRechargeCombo(@RequestBody RechargeComboManageDTO rechargeComboManageDTO) {
        return rechargeComboService.saveRechargeCombo(rechargeComboManageDTO);
    }

    @DeleteMapping("/delete/{rechargeComboId}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:delete')")
    public ResultVO delRechargeCombo(@PathVariable("rechargeComboId") Long rechargeComboId) {
        return rechargeComboService.delRechargeCombo(rechargeComboId);
    }

    /*回收站接口*/
    @GetMapping("/recycleBin")
    @PreAuthorize("hasAnyAuthority('recharge:combo:query')")
    public ResultVO selectRecycleBin() {
        return recycleBinService.selectRecycleBin(RechargeCombo.class);
    }

    @GetMapping("/recycleBin/{name}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:query')")
    public ResultVO findRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.findRecycleBinByName(RechargeCombo.class, name);
    }

    @DeleteMapping("/recycleBin/remove/{name}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:delete')")
    public ResultVO removeRecycleBinByName(@PathVariable("name") String name) {
        return recycleBinService.delRecycleBinByName(RechargeCombo.class, name);
    }

    @PostMapping("/recycleBin/regain/{name}")
    @PreAuthorize("hasAnyAuthority('recharge:combo:save')")
    public ResultVO regainRecycleBinByName(@PathVariable("name") String name) {
        ResultVO resultVO = recycleBinService.regainRecycleBinByName(RechargeCombo.class, name, val -> rechargeComboService.save(val));
        if (resultVO.getCode().equals(StatusCode.SUCCEED)) {
            // 获取资源
            RechargeCombo data = (RechargeCombo) resultVO.getData();
            // 更新缓存
            rechargeComboService.updateCache(data.getId(), data);
        }
        return resultVO;
    }
}
