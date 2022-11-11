package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.OrderQueryDTO;
import com.jy2b.zxxfd.domain.dto.OrderSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.OrderUpdateFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.service.IOrderService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@Api(tags = "订单相关接口")
public class OrderController {
    @Resource
    private IOrderService orderService;

    @PostMapping("/submit")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVo submitOrder(@RequestBody OrderSaveFromDTO orderSaveFromDTO) {
        return orderService.submitOrder(orderSaveFromDTO);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVo userUpdateOrder(@PathVariable("id") Long id, @RequestBody OrderSaveFromDTO orderSaveFromDTO) {
        return orderService.userUpdateOrder(id, orderSaveFromDTO);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasAnyAuthority('order:cancel')")
    public ResultVo cancelOrder(@PathVariable("id") Long id) {
        return orderService.cancelOrder(id);
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVo completeOrder(@PathVariable("id") Long id) {
        return orderService.completeOrder(id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo deleteOrder(@PathVariable("id") Long id) {
        return orderService.deleteOrder(id);
    }

    @PutMapping("/payment/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVo paymentOrder(@PathVariable("id") Long id) {
        return orderService.paymentOrder(id);
    }

    @GetMapping("/query/{id}")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryOrderById(@PathVariable("id") Long id) {
        return orderService.queryOrderById(id);
    }

    @GetMapping("/query/all")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryOrderAll() {
        return orderService.queryOrderAll();
    }

    @GetMapping("/query/unpaid")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryUnpaidOrder() {
        return orderService.queryUnpaidOrder();
    }

    @GetMapping("/query/beShipped")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryBeShippedOrder() {
        return orderService.queryBeShippedOrder();
    }

    @GetMapping("/query/undelivered")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryUndeliveredOrder() {
        return orderService.queryUndeliveredOrder();
    }

    @GetMapping("/query/completed")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVo queryCompletedOrder() {
        return orderService.queryCompletedOrder();
    }

    @PostMapping("/query/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('order:update')")
    public ResultVo queryOrder(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody(required = false) OrderQueryDTO orderQueryDTO) {
        return orderService.queryOrder(page, size, orderQueryDTO);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('order:update')")
    public ResultVo updateOrder(@RequestBody OrderUpdateFromDTO updateFromDTO) {
        return orderService.updateOrder(updateFromDTO);
    }
}
