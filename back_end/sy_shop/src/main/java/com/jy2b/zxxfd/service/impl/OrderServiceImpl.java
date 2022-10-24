package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.*;
import com.jy2b.zxxfd.mapper.*;
import com.jy2b.zxxfd.service.IOrderService;
import com.jy2b.zxxfd.utils.BillUtils;
import com.jy2b.zxxfd.utils.RedisIdWorker;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import com.jy2b.zxxfd.contants.RedisConstants;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AddressMapper addressMapper;

    @Resource
    private GoodsItemMapper goodsItemMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private AccountMapper accountMapper;

    @Resource
    private GoodsEvaluationMapper goodsEvaluationMapper;

    @Override
    public ResultVo submitOrder(OrderSaveFromDTO orderSaveFromDTO) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取收货地址id
        Long addressId = orderSaveFromDTO.getAid();
        // 获取收货地址
        UserAddress address = addressMapper.selectById(addressId);
        // 判断收货地址是否为空
        if (address == null) {
            return ResultVo.fail("收货地址不能为空");
        }

        // 获取收货人姓名
        String name = address.getName();
        // 获取收货联系电话
        String phone = address.getPhone();
        // 获取收货省份
        String province = address.getProvince();
        // 获取收货城市
        String city = address.getCity();
        // 获取收货区/县
        String district = address.getDistrict();
        // 获取详细收货地址
        String addressInfo = address.getAddress();

        // 获取订单备注
        String remarks = orderSaveFromDTO.getRemarks();

        // 生成订单号
        long orderId = new RedisIdWorker(stringRedisTemplate).nextId(RedisConstants.ORDER_KEY);
        // 订单金额
        double amount = 0D;

        // 新增订单
        Order order = new Order();
        // 设置订单号
        order.setId(orderId);
        // 设置下单用户
        order.setUid(userId);
        // 设置订单金额
        order.setPrice(amount);
        // 设置订单备注
        order.setRemarks(remarks);
        // 设置订单收货人
        order.setName(name);
        // 设置订单联系电话
        order.setPhone(phone);
        // 设置订单收货省份
        order.setProvince(province);
        // 设置订单收货城市
        order.setCity(city);
        // 设置订单收货区/县
        order.setDistrict(district);
        // 设置订单详细收货地址
        order.setAddress(addressInfo);

        // 保存订单
        save(order);

        // 订单运费
        Double postage = 0d;

        // 获取每一个商品属性
        for (OrderSaveDTO orderSaveDTO : orderSaveFromDTO.getItems()) {
            // 获取商品属性id
            Long gid = orderSaveDTO.getGid();
            // 获取商品属性
            GoodsItem item = goodsItemMapper.selectById(gid);

            if (item == null) {
                throw new RuntimeException("商品不存在");
            }

            // 获取商品id
            Long goodsId = item.getGid();
            // 获取商品信息
            Goods goods = goodsMapper.selectById(goodsId);

            if (goods.getStatus() != 1) {
                return ResultVo.fail("商品：" + goods.getName() + "不存在");
            }
            // 判断商品属性是否上架
            if (item.getStatus() == 0) {
                return ResultVo.fail("商品：" + goods.getName() + "已下架");
            }
            // 获取商品数量
            Integer quantity = orderSaveDTO.getQuantity();
            // 判断数量是否小于大于零
            if (quantity <= 0) {
                return ResultVo.fail("购买数量不能等于小于零");
            }
            // 获取商品属性库存
            Long stock = item.getStock();
            // 判断库存是否充盈
            if (quantity > stock) {
                return ResultVo.fail("商品库存不足");
            }

            // 获取商品单价
            Double unitPrice = item.getPrice() * item.getDiscount();

            // 获取商品总价
            double price = unitPrice * quantity;

            OrderItem orderItem = new OrderItem();
            // 设置订单号
            orderItem.setOrderId(orderId);
            // 设置商品属性id
            orderItem.setGid(gid);
            // 设置单价
            orderItem.setUnitPrice(unitPrice);
            // 设置数量
            orderItem.setQuantity(quantity);
            // 设置总价
            orderItem.setPrice(price);

            // 保存订单属性
            orderItemMapper.insert(orderItem);

            // 修改商品属性库存
            item.setStock(stock -quantity);
            goodsItemMapper.updateById(item);

            // 获取商品邮费
            Double goodsPostage = goods.getPostage();
            postage += goodsPostage;

            amount += price;
        }

        // 修改订单价格
        boolean result = update().set("postage", postage).set("price", amount + postage).eq("id", orderId).update();

        if (!result) {
            throw new RuntimeException("提交订单失败");
        }

        // TODO 用户提交订单后，若在限定时间中没有进行支付，则删除该订单

        return ResultVo.ok(orderId, "提交订单成功");
    }

    @Override
    public ResultVo cancelOrder(Long id) {
        // 查询订单
        Order order = getById(id);

        // 判断订单是否存在
        if (order == null) {
            return ResultVo.fail("订单不存在");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        if (!order.getUid().equals(userId)) {
            return ResultVo.fail("订单不存在");
        }
        // 判断订单是否已完成
        if (order.getStatus() != 0) {
            return ResultVo.fail("订单已完成");
        }
        // 判断订单是否已发货
        if (order.getLogisticsStatus() > 0) {
            return ResultVo.fail("订单已发货");
        }
        // 判断订单是否已付款
        if (order.getIsPay() != 0) {
            // 获取用户钱包
            UserAccount userAccount = accountMapper.selectById(userId);

            // 获取用户余额
            Double balance = userAccount.getBalance();
            // 退还付款
            userAccount.setBalance(balance + order.getPrice());

            int update = accountMapper.updateById(userAccount);

            if (update < 1) {
                throw new RuntimeException("取消订单失败");
            }

            String price = String.valueOf(order.getPrice());

            // 新增账单
            BillUtils billUtils = new BillUtils(stringRedisTemplate);
            billUtils.saveBill(userId, "退款", price);
        }

        // 获取订单属性
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", id);
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        for (OrderItem orderItem : orderItems) {
            if (orderItem != null) {
                Long itemId = orderItem.getGid();
                // 获取商品属性
                GoodsItem item = goodsItemMapper.selectById(itemId);
                // 获取购买数量
                Integer quantity = orderItem.getQuantity();
                // 获取商品库存
                Long stock = item.getStock();

                // 修改商品库存
                item.setStock(stock + quantity);
                int updateItem = goodsItemMapper.updateById(item);
                int updateOrderItem = orderItemMapper.deleteById(orderItem.getId());

                if (updateItem < 1 || updateOrderItem < 1) {
                    throw new RuntimeException("取消订单失败");
                }
            }
        }

        // 取消订单
        boolean result = removeById(id);
        if (!result) {
            throw new RuntimeException("取消订单失败");
        }
        return ResultVo.ok("取消订单成功");
    }

    @Override
    public ResultVo paymentOrder(Long id) {
        // 查询订单
        Order order = getById(id);
        // 判断订单是否存在
        if (order == null) {
            return ResultVo.fail("订单不存在");
        }
        // 判断订单是否已支付
        if (order.getIsPay() > 0) {
            return ResultVo.fail("订单已支付");
        }

        // 获取订单金额
        Double price = order.getPrice();

        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 获取用户钱包
        UserAccount userAccount = accountMapper.selectById(userId);

        // 获取钱包余额
        Double balance = userAccount.getBalance();
        // 判断用户钱包是否足够
        if (balance < price) {
            return ResultVo.fail("钱包余额不足");
        }

        // 获取钱包累计消费
        Double spending = userAccount.getSpending();

        // 支付订单
        userAccount.setBalance(balance - price);
        userAccount.setSpending(spending + price);
        int update = accountMapper.updateById(userAccount);
        if (update < 1) {
            return ResultVo.fail("支付失败");
        }

        // 支付成功
        order.setIsPay(1);
        order.setPaymentMethods(0);

        boolean result = updateById(order);
        if (!result) {
            throw new RuntimeException("订单支付失败");
        }

        // 生成账单
        String amount = String.valueOf(price);
        BillUtils billUtils = new BillUtils(stringRedisTemplate);
        billUtils.saveBill(userId, "购物消费", amount);

        return ResultVo.ok("支付成功");
    }

    @Override
    public ResultVo queryOrderById(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询订单
        Order order = getById(id);
        if (order == null) {
            return ResultVo.fail("订单不存在");
        }
        // 判断是否下单用户本人
        if (!userId.equals(order.getUid())) {
            return ResultVo.fail("订单不存在");
        }

        OrderDTO orderDTO = setQueryDTO(order);

        return ResultVo.ok(orderDTO);
    }

    @Override
    public ResultVo queryUnpaidOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query().eq("uid", userId).eq("isPay", 0).eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVo.ok(orderDTOS);
    }

    @Override
    public ResultVo queryUndeliveredOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query().eq("uid", userId).in("logistics_status", 0, 1).eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVo.ok(orderDTOS);
    }

    @Override
    public ResultVo queryCompletedOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query().eq("uid", userId).eq("status", 1).eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVo.ok(orderDTOS);
    }

    @Override
    public ResultVo deleteOrder(Long id) {
        // 查询订单
        Order order = getById(id);
        if (order == null) {
            return ResultVo.fail("订单不存在");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断订单是否属于用户
        if (!order.getUid().equals(userId)) {
            return ResultVo.fail("订单不存在");
        }
        // 判断订单是否已完成
        if (order.getStatus() < 1) {
            return ResultVo.fail("订单未完成");
        }

        // 删除订单
        boolean result = update().set("isDel", 1).eq("id", id).update();

        return result ? ResultVo.ok("订单删除成功") : ResultVo.fail("订单删除失败");
    }

    @Override
    public ResultVo queryOrder(Integer page, Integer size, OrderQueryDTO orderQueryDTO) {
        Page<Order> orderPage = new Page<>(page, size);

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();

        if (orderQueryDTO != null) {
            // 获取订单号
            Long orderId = orderQueryDTO.getId();
            if (orderId != null) {
                queryWrapper.eq("id", orderId);
            }
            // 获取下单用户id
            Long userId = orderQueryDTO.getUid();
            if (orderId != null) {
                queryWrapper.eq("uid", userId);
            }
            // 获取支付状态
            Integer isPay = orderQueryDTO.getIsPay();
            if (isPay != null) {
                switch (isPay) {
                    case 0: case 1: queryWrapper.eq("isPay", isPay);break;
                }
            }
            // 获取物流状态
            Integer logisticsStatus = orderQueryDTO.getLogisticsStatus();
            if (logisticsStatus != null) {
                queryWrapper.eq("logistics_status", logisticsStatus);
            }
            // 获取订单状态
            Integer status = orderQueryDTO.getStatus();
            if (status != null) {
                switch (status) {
                    case 0: case 1: queryWrapper.eq("status", status);break;
                }
            }
            // 获取排序时间
            String timeSort = orderQueryDTO.getTimeSort();
            if (StrUtil.isNotBlank(timeSort)) {
                switch (timeSort) {
                    case "Asc": queryWrapper.orderByAsc("time");break;
                    case "Des": queryWrapper.orderByDesc("time");break;
                }
            }

            // 获取排序价格
            String priceSort = orderQueryDTO.getPriceSort();
            if (StrUtil.isNotBlank(priceSort)) {
                switch (priceSort) {
                    case "Asc": queryWrapper.orderByAsc("price");break;
                    case "Des": queryWrapper.orderByDesc("price");break;
                }
            }
        } else {
            queryWrapper.orderByDesc("time");
        }

        orderMapper.selectPage(orderPage, queryWrapper);

        return ResultVo.ok(orderPage);
    }

    @Override
    public ResultVo updateOrder(OrderUpdateFromDTO updateFromDTO) {
        // 获取订单号
        Long orderId = updateFromDTO.getId();
        // 查询订单
        Order order = getById(orderId);
        // 判断订单是否存在
        if (order == null) {
            return ResultVo.fail("订单不存在！");
        }

        // 获取修改收货人
        String name = updateFromDTO.getName();
        if (StrUtil.isNotBlank(name)) {
            // 修改收货人
            order.setName(name);
        }
        // 获取修改联系电话
        String phone = updateFromDTO.getPhone();
        if (StrUtil.isNotBlank(phone)) {
            // 修改联系电话
            order.setPhone(phone);
        }
        // 获取修改收货省份
        String province = updateFromDTO.getProvince();
        // 获取修改收货城市
        String city = updateFromDTO.getCity();
        if (StrUtil.isNotBlank(province) && StrUtil.isNotBlank(city)) {
            // 修改省份
            order.setProvince(province);
            // 修改城市
            order.setCity(city);
        }
        // 获取修改区/县
        String district = updateFromDTO.getDistrict();
        if (StrUtil.isNotBlank(district)) {
            // 修改区/县
            order.setDistrict(district);
        }
        // 获取修改收货地址
        String address = updateFromDTO.getAddress();
        if (StrUtil.isNotBlank(address)) {
            // 修改收货地址
            order.setAddress(address);
        }

        // 获取修改备注
        String remarks = updateFromDTO.getRemarks();
        if (StrUtil.isNotBlank(remarks)) {
            // 修改订单备注
            order.setRemarks(remarks);
        }

        // 修改物流状态
        Integer logisticsStatus = updateFromDTO.getLogisticsStatus();
        if (logisticsStatus != null) {
            // 修改物流状态
            order.setLogisticsStatus(logisticsStatus);

            if (logisticsStatus == 1) {
                Date date = new Date();
                // 设置发货时间
                order.setShippingTime(date);
            }

            if (logisticsStatus == 6) {
                Date date = new Date();
                // 设置退货时间
                order.setReturnTime(date);
            }
        }
        // 修改订单状态
        Integer status = updateFromDTO.getStatus();
        if (status != null) {
            // 修改订单状态
            order.setStatus(status);
        }

        // 修改订单
        boolean result = updateById(order);

        return result ? ResultVo.ok("订单修改成功！") : ResultVo.fail("订单修改失败！");
    }

    private ArrayList<OrderDTO> setQueryDTOS(List<Order> orderList) {
        ArrayList<OrderDTO> orderDTOS = new ArrayList<>();

        if (!orderList.isEmpty()) {
            for (Order order : orderList) {
                if (order != null) {
                    OrderDTO orderDTO = setQueryDTO(order);
                    orderDTOS.add(orderDTO);
                }
            }
        }

        return orderDTOS;
    }

    private OrderDTO setQueryDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();

        // 设置订单号
        orderDTO.setOrderId(order.getId());
        // 设置订单收件人
        orderDTO.setName(order.getName());
        // 设置订单联系电话
        orderDTO.setPhone(order.getPhone());

        // 获取收货省份
        String province = order.getProvince();
        // 获取收货城市
        String city = order.getCity();
        // 获取收货区/县
        String district = order.getDistrict();
        // 获取详细收货地址
        String addressInfo = order.getAddress();
        String address = province + city + district + addressInfo;


        // 设置订单收货地址
        orderDTO.setAddress(address);
        // 设置订单运费
        orderDTO.setPostage(order.getPostage());
        // 设置订单总额
        orderDTO.setPrice(order.getPrice());
        // 设置订单是否付款
        orderDTO.setIsPay(order.getIsPay() == 0 ? "未付款" : "已付款");

        // 设置订单支付方式
        Integer paymentMethods = order.getPaymentMethods();
        switch (paymentMethods) {
            case 0: orderDTO.setPaymentMethods("钱包支付");break;
            case 1: orderDTO.setPaymentMethods("微信支付");break;
            case 2: orderDTO.setPaymentMethods("支付宝支付");break;
        }

        // 设置物流状态
        Integer logisticsStatus = order.getLogisticsStatus();
        switch (logisticsStatus) {
            case 0: orderDTO.setLogisticsStatus("未发货");break;
            case 1: orderDTO.setLogisticsStatus("待收货");break;
            case 2: orderDTO.setLogisticsStatus("已收货");break;
            case 3: orderDTO.setLogisticsStatus("待换货");break;
            case 4: orderDTO.setLogisticsStatus("已换货");break;
        }

        if (logisticsStatus > 0) {
            Date shippingTime = order.getShippingTime();

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            // 设置发货时间
            orderDTO.setShippingTime(simpleDateFormat.format(shippingTime));
        }

        // 获取订单状态
        Integer status = order.getStatus();
        String statusName;
        switch (status) {
            case 0: statusName = "未完成";break;
            case 1: statusName = "已完成";break;
            case 2: statusName = "待退款";break;
            case 3: statusName = "已退款";break;
            default: statusName = "";
        }
        // 设置订单状态
        orderDTO.setStatus(statusName);

        // 获取下单时间
        Date time = order.getTime();

        // 设置订单时间
        orderDTO.setTime(TimeUtils.dateToStringTime(time));

        // 设置订单备注
        orderDTO.setRemarks(order.getRemarks());

        // 获取订单属性
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", order.getId());
        List<OrderItem> orderItems = orderItemMapper.selectList(queryWrapper);
        ArrayList<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        // 获取每一个订单信息
        for (OrderItem orderItem : orderItems) {
            if (orderItem != null) {
                OrderItemDTO orderItemDTO = new OrderItemDTO();
                // 获取商品属性id
                Long itemId = orderItem.getGid();
                // 获取商品属性信息
                GoodsItem item = goodsItemMapper.selectById(itemId);

                // 获取商品id
                Long gid = item.getGid();
                // 获取商品信息
                Goods goods = goodsMapper.selectById(gid);

                // 设置商品名称
                orderItemDTO.setGoodsName(goods.getName());

                // 设置商品属性id
                orderItemDTO.setGid(itemId);
                // 设置商品属性颜色
                orderItemDTO.setColor(item.getColor());
                // 设置商品属性图片
                orderItemDTO.setImage(item.getIcon());
                // 设置商品属性套餐
                if (item.getCombo() != null) {
                    orderItemDTO.setCombo(item.getCombo());
                }
                // 设置商品属性尺寸
                if (item.getSize() != null) {
                    orderItemDTO.setSize(item.getSize());
                }
                // 设置商品属性版本
                if (item.getEdition() != null) {
                    orderItemDTO.setEdition(item.getEdition());
                }

                // 设置商品属性单价
                orderItemDTO.setUnitPrice(orderItem.getUnitPrice());
                // 设置商品属性总价
                orderItemDTO.setPrice(orderItem.getPrice());
                // 设置商品属性数量
                orderItemDTO.setQuantity(orderItem.getQuantity());

                if (order.getStatus() == 1) {
                    // 查询是否进行评价
                    QueryWrapper<GoodsEvaluation> commentQueryWrapper = new QueryWrapper<>();
                    commentQueryWrapper.eq("order_id", order.getId()).eq("goodsItem_id", itemId);
                    Integer integer = goodsEvaluationMapper.selectCount(commentQueryWrapper);

                    if (integer < 1) {
                        // 设置未评论
                        orderItemDTO.setIsComment(integer);
                    }
                }

                orderItemDTOS.add(orderItemDTO);
            }
        }

        // 设置订单属性详情
        orderDTO.setOrderItems(orderItemDTOS);

        return orderDTO;
    }

}
