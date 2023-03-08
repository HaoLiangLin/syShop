package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IBillService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("/bill")
public class BillController {
    @Resource
    private IBillService billService;

    @GetMapping("/count/{startDate}/{endDate}")
    @PreAuthorize("hasAnyAuthority('end:query')")
    public ResultVO billCount(@PathVariable("startDate") Long startDate, @PathVariable("endDate") Long endDate) {
        return billService.billCount(startDate, endDate);
    }
}
