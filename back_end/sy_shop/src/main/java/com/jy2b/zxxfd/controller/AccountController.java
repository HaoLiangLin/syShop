package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.RechargeFromDTO;
import com.jy2b.zxxfd.domain.dto.BillDateDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.service.IAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/account")
@CrossOrigin
@Api(tags = "钱包相关接口")
public class AccountController {
    @Resource
    private IAccountService accountService;

    @GetMapping("/query")
    @ApiOperation(value = "查询钱包信息")
    @PreAuthorize("hasAnyAuthority('account:query')")
    public ResultVo queryAccount() {
        return accountService.queryAccount();
    }

    @PutMapping("/recharge")
    @ApiOperation(value = "钱包充值")
    @PreAuthorize("hasAnyAuthority('account:recharge')")
    public ResultVo recharge(@RequestBody RechargeFromDTO rechargeFromDTO) {
        return accountService.recharge(rechargeFromDTO);
    }

    @GetMapping("/bill")
    @PreAuthorize("hasAnyAuthority('account:query')")
    public ResultVo queryBill(@RequestBody(required = false) BillDateDTO dateDTO) {
        return accountService.queryBill(dateDTO);
    }
}
