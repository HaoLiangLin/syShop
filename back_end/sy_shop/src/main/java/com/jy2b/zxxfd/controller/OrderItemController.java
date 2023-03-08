package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IOrderItemService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orderItem")
@CrossOrigin
@Api(tags = "订单属性相关接口")
public class OrderItemController {
    @Resource
    private IOrderItemService orderItemService;

    @GetMapping("/query/{id}")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryById(@PathVariable("id") Long id) {
        return orderItemService.queryById(id);
    }
}
