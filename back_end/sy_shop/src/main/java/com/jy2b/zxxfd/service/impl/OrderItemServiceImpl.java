package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.OrderItem;
import com.jy2b.zxxfd.domain.dto.OrderItemDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.mapper.OrderItemMapper;
import com.jy2b.zxxfd.service.IOrderItemService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements IOrderItemService {
    @Resource
    private GoodsItemMapper goodsItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public ResultVo queryById(Long id) {
        // 根据id查询订单属性
        OrderItem orderItem = getById(id);
        // 判断订单属性是否存在
        if (orderItem == null) {
            return ResultVo.fail("订单不存在");
        }

        OrderItemDTO orderItemDTO = new OrderItemDTO();
        // 设置订单属性id
        orderItemDTO.setOrderItemId(orderItem.getId());

        // 获取商品属性id
        Long goodsItemId = orderItem.getGid();
        // 根据商品属性id查询商品属性
        GoodsItem goodsItem = goodsItemMapper.selectById(goodsItemId);

        // 获取商品id
        Long gid = goodsItem.getGid();
        // 获取商品信息
        Goods goods = goodsMapper.selectById(gid);

        // 设置商品名称
        orderItemDTO.setGoodsName(goods.getName());

        // 设置商品属性id
        orderItemDTO.setGid(goodsItemId);
        // 设置商品属性颜色
        orderItemDTO.setColor(goodsItem.getColor());
        // 设置商品属性图片
        orderItemDTO.setImage(goodsItem.getIcon());
        // 设置商品属性套餐
        if (goodsItem.getCombo() != null) {
            orderItemDTO.setCombo(goodsItem.getCombo());
        }
        // 设置商品属性尺寸
        if (goodsItem.getSize() != null) {
            orderItemDTO.setSize(goodsItem.getSize());
        }
        // 设置商品属性版本
        if (goodsItem.getEdition() != null) {
            orderItemDTO.setEdition(goodsItem.getEdition());
        }

        // 设置商品属性单价
        orderItemDTO.setUnitPrice(orderItem.getUnitPrice());
        // 设置商品属性总价
        orderItemDTO.setPrice(orderItem.getPrice());
        // 设置商品属性数量
        orderItemDTO.setQuantity(orderItem.getQuantity());

        return ResultVo.ok(orderItemDTO);
    }
}
