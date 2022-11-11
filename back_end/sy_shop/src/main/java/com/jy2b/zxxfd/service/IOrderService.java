package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.Order;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface IOrderService extends IService<Order> {
    /**
     * 提交订单
     * @param orderSaveFromDTO 订单详情
     * @return ResultVo
     */
    ResultVo submitOrder(OrderSaveFromDTO orderSaveFromDTO);

    /**
     * 用户修改订单
     * @param orderSaveDTO 订单信息
     * @return ResultVo
     */
    ResultVo userUpdateOrder(Long id, OrderSaveFromDTO orderSaveDTO);

    /**
     * 取消订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVo cancelOrder(Long id);

    /**
     * 支付订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVo paymentOrder(Long id);

    /**
     * 根据订单号查询订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVo queryOrderById(Long id);

    /**
     * 查询全部订单
     * @return ResultVo
     */
    ResultVo queryOrderAll();

    /**
     * 查询待付款订单
     * @return ResultVo
     */
    ResultVo queryUnpaidOrder();

    /**
     * 查询待发货订单
     * @return ResultVo
     */
    ResultVo queryBeShippedOrder();

    /**
     * 查询待收货订单
     * @return ResultVo
     */
    ResultVo queryUndeliveredOrder();

    /**
     * 查询已完成订单
     * @return ResultVo
     */
    ResultVo queryCompletedOrder();

    /**
     * 完成订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVo completeOrder(Long id);

    /**
     * 删除订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVo deleteOrder(Long id);

    /**
     * 查询订单
     * @param page 页数
     * @param size 每页显示条数
     * @param orderQueryDTO 查询订单条件
     * @return ResultVo
     */
    ResultVo queryOrder(Integer page, Integer size, OrderQueryDTO orderQueryDTO);

    /**
     * 修改订单
     * @param updateFromDTO 订单信息
     * @return ResultVo
     */
    ResultVo updateOrder(OrderUpdateFromDTO updateFromDTO);

}
