package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.OrderItem;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IOrderItemService extends IService<OrderItem> {
    /**
     * 根据订单属性id获取订单属性信息
     * @param id 订单属性id
     * @return ResultVo
     */
    ResultVO queryById(Long id);
}
