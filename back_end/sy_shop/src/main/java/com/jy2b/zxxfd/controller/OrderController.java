package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.OrderQueryDTO;
import com.jy2b.zxxfd.domain.dto.OrderSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.OrderStatusUpdateDTO;
import com.jy2b.zxxfd.domain.dto.OrderUpdateFromDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IOrderService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author 林武泰
 * 订单接口
 */
@RestController
@RequestMapping("/orders")
@CrossOrigin
@Api(tags = "订单相关接口")
public class OrderController {
    @Resource
    private IOrderService orderService;

    @PostMapping("/submit")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVO submitOrder(@RequestBody OrderSaveFromDTO orderSaveFromDTO) {
        return orderService.submitOrder(orderSaveFromDTO);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVO userUpdateOrder(@PathVariable("id") Long id, @RequestBody OrderSaveFromDTO orderSaveFromDTO) {
        return orderService.userUpdateOrder(id, orderSaveFromDTO);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasAnyAuthority('order:cancel')")
    public ResultVO cancelOrder(@PathVariable("id") Long id, @RequestParam("reason") String reason) {
        return orderService.cancelOrder(id, reason);
    }

    @PutMapping("/complete/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVO completeOrder(@PathVariable("id") Long id) {
        return orderService.completeOrder(id);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO deleteOrder(@PathVariable("id") Long id) {
        return orderService.deleteOrder(id);
    }

    @PostMapping("/payment/{id}")
    @PreAuthorize("hasAnyAuthority('order:submit')")
    public ResultVO paymentOrder(@PathVariable("id") Long id, @RequestParam(value = "points", required = false) Long points) {
        return orderService.paymentOrder(id, points);
    }

    @GetMapping("/query/{id}")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryOrderById(@PathVariable("id") Long id) {
        return orderService.queryOrderById(id);
    }

    @GetMapping("/query/all")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryOrderAll() {
        return orderService.queryOrderAll();
    }

    @GetMapping("/query/unpaid")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryUnpaidOrder() {
        return orderService.queryUnpaidOrder();
    }

    @GetMapping("/query/beShipped")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryBeShippedOrder() {
        return orderService.queryBeShippedOrder();
    }

    @GetMapping("/query/undelivered")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryUndeliveredOrder() {
        return orderService.queryUndeliveredOrder();
    }

    @GetMapping("/query/completed")
    @PreAuthorize("hasAnyAuthority('order:query')")
    public ResultVO queryCompletedOrder() {
        return orderService.queryCompletedOrder();
    }

    @PostMapping("/query/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('order:update')")
    public ResultVO queryOrder(
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size,
            @RequestBody(required = false) OrderQueryDTO orderQueryDTO) {
        return orderService.queryOrder(page, size, orderQueryDTO);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('order:update')")
    public ResultVO updateOrder(@RequestBody OrderUpdateFromDTO updateFromDTO) {
        return orderService.updateOrder(updateFromDTO);
    }

    @PutMapping("/update/status/{orderId}")
    public ResultVO updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {
        return orderService.updateOrderStatus(orderId, orderStatusUpdateDTO);
    }

    @GetMapping("/count/{startDate}/{endDate}")
    @PreAuthorize("hasAnyAuthority('end:query')")
    public ResultVO orderCount(@PathVariable("startDate") Long startDate, @PathVariable("endDate") Long endDate) {
        return orderService.orderCount(startDate, endDate);
    }
}
