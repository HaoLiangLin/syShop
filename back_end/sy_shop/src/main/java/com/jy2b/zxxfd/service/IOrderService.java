package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.Order;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 订单业务接口
 */
@Transactional
public interface IOrderService extends IService<Order> {
    /**
     * 提交订单
     * @param orderSaveFromDTO 订单详情
     * @return ResultVo
     */
    ResultVO submitOrder(OrderSaveFromDTO orderSaveFromDTO);

    /**
     * 用户修改订单
     * @param orderSaveDTO 订单信息
     * @return ResultVo
     */
    ResultVO userUpdateOrder(Long id, OrderSaveFromDTO orderSaveDTO);

    /**
     * 取消订单
     * @param id 订单号
     * @param reason 取消原因
     * @return ResultVo
     */
    ResultVO cancelOrder(Long id, String reason);

    /**
     * 支付订单
     * @param id 订单号
     * @param points 抵扣积分
     * @return ResultVo
     */
    ResultVO paymentOrder(Long id, Long points);

    /**
     * 根据订单号查询订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVO queryOrderById(Long id);

    /**
     * 查询全部订单
     * @return ResultVo
     */
    ResultVO queryOrderAll();

    /**
     * 查询待付款订单
     * @return ResultVo
     */
    ResultVO queryUnpaidOrder();

    /**
     * 查询待发货订单
     * @return ResultVo
     */
    ResultVO queryBeShippedOrder();

    /**
     * 查询待收货订单
     * @return ResultVo
     */
    ResultVO queryUndeliveredOrder();

    /**
     * 查询已完成订单
     * @return ResultVo
     */
    ResultVO queryCompletedOrder();

    /**
     * 完成订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVO completeOrder(Long id);

    /**
     * 删除订单
     * @param id 订单号
     * @return ResultVo
     */
    ResultVO deleteOrder(Long id);

    /**
     * 查询订单
     * @param page 页数
     * @param size 每页显示条数
     * @param orderQueryDTO 查询订单条件
     * @return ResultVo
     */
    ResultVO queryOrder(Integer page, Integer size, OrderQueryDTO orderQueryDTO);

    /**
     * 修改订单
     * @param updateFromDTO 订单信息
     * @return ResultVo
     */
    ResultVO updateOrder(OrderUpdateFromDTO updateFromDTO);

    /**
     * 修改订单状态
     * @param orderId 订单号
     * @param orderStatusUpdateDTO 订单状态信息
     * @return ResultVO
     */
    ResultVO updateOrderStatus(Long orderId, OrderStatusUpdateDTO orderStatusUpdateDTO);

    /**
     * 订单统计
     * @param startDate 起始时间
     * @param endDate 返回时间
     * @return ResultVO
     */
    ResultVO orderCount(Long startDate, Long endDate);
}
