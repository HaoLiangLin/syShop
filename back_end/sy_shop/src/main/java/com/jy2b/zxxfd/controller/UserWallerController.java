package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.RechargeDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IUserWallerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 林武泰
 * 用户钱包接口
 */
@RestController
@CrossOrigin
@RequestMapping("/waller")
public class UserWallerController {
    @Resource
    private IUserWallerService userWallerService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('user:wallet:query')")
    public ResultVO selectWaller() {
        return userWallerService.selectWaller();
    }

    @PostMapping("/recharge")
    @PreAuthorize("hasAnyAuthority('user:wallet:recharge')")
    public ResultVO rechargeWaller(@RequestBody RechargeDTO rechargeDTO) {
        return userWallerService.rechargeWaller(rechargeDTO);
    }

    @GetMapping("/bill/{year}/{month}")
    @PreAuthorize("hasAnyAuthority('user:wallet:query')")
    public ResultVO selectUserBill(@PathVariable("year") int year, @PathVariable("month") int month) {
        return userWallerService.selectUserBill(year, month);
    }
}
