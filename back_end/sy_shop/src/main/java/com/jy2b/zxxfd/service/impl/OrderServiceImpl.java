package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.domain.*;
import com.jy2b.zxxfd.domain.vo.BillStatusCode;
import com.jy2b.zxxfd.domain.vo.BillType;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.*;
import com.jy2b.zxxfd.service.IBillService;
import com.jy2b.zxxfd.service.IOrderService;
import com.jy2b.zxxfd.utils.RedisIdWorker;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UserBillUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.jy2b.zxxfd.contants.RedisConstants.ORDER_KEY;
import static com.jy2b.zxxfd.contants.RedisConstants.USER_WALLER_KEY;
import static com.jy2b.zxxfd.contants.SystemConstants.APPLICATION_NAME;

/**
 * @author 林武泰
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserAddressMapper addressMapper;

    @Resource
    private GoodsItemMapper goodsItemMapper;

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserWallerMapper userWallerMapper;

    @Resource
    private GoodsEvaluationMapper goodsEvaluationMapper;

    @Resource
    private UserBillUtils userBillUtils;

    @Override
    public ResultVO submitOrder(OrderSaveFromDTO orderSaveFromDTO) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取收货地址id
        Long addressId = orderSaveFromDTO.getAid();
        // 获取收货地址
        UserAddress address = addressMapper.selectById(addressId);
        // 判断收货地址是否为空
        if (address == null) {
            return ResultVO.fail("收货地址不能为空");
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
        long orderId = new RedisIdWorker(stringRedisTemplate).nextId(ORDER_KEY);
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
                throw new RuntimeException("商品属性不存在");
            }

            // 获取商品id
            Long goodsId = item.getGid();
            // 获取商品信息
            Goods goods = goodsMapper.selectById(goodsId);

            if (goods.getStatus() != 1) {
                return ResultVO.fail("商品：" + goods.getName() + "不存在");
            }
            // 判断商品属性是否上架
            if (item.getStatus() == 0) {
                return ResultVO.fail("商品：" + goods.getName() + "已下架");
            }
            // 获取商品数量
            Integer quantity = orderSaveDTO.getQuantity();
            // 判断数量是否小于大于零
            if (quantity <= 0) {
                return ResultVO.fail("购买数量不能等于小于零");
            }
            // 获取商品属性库存
            Long stock = item.getStock();
            // 判断库存是否充盈
            if (quantity > stock) {
                return ResultVO.fail("商品库存不足");
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
            item.setStock(stock - quantity);
            item.setSales(item.getSales() + quantity);
            goodsItemMapper.updateById(item);

            // 修改商品销量
            goods.setSale(goods.getSale() + quantity);
            goodsMapper.updateById(goods);

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

        return ResultVO.ok(String.valueOf(orderId), "提交订单成功");
    }

    @Override
    public ResultVO userUpdateOrder(Long id, OrderSaveFromDTO orderSaveDTO) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询将要修改订单
        Order beforeOrder = getById(id);

        // 判断订单是否存在
        if (beforeOrder == null) {
            return ResultVO.fail("订单不存在");
        }

        // 判断订单是否属于用户
        if (!beforeOrder.getUid().equals(userId)) {
            return ResultVO.fail("订单不存在");
        }

        // 判断订单是否已付款
        Integer isPay = beforeOrder.getIsPay();
        if (isPay > 0) {
            return ResultVO.fail("订单已交易");
        }

        Order order = new Order();
        order.setId(id);

        // 获取收货地址id
        Long aid = orderSaveDTO.getAid();
        // 判断是否修改收货地址
        if (aid != null) {
            // 获取用户收货地址
            UserAddress address = addressMapper.selectById(aid);

            // 判断收货地址是否存在
            if (address == null) {
                return ResultVO.fail("收货地址不存在");
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
        }

        // 获取订单备注
        String remarks = orderSaveDTO.getRemarks();
        // 判断是否修改订单备注
        if (remarks != null) {
            if (StrUtil.isNotBlank(remarks)) {
                order.setRemarks(remarks);
            }
        }

        // 修改订单
        boolean result = updateById(order);

        // 修改订单成功
        if (result) {
            // 获取订单
            Order afterOrder = getById(id);
            OrderDTO orderDTO = setQueryDTO(afterOrder);

            // 返回数据
            return ResultVO.ok(orderDTO, "修改成功");
        }

        return ResultVO.fail("订单修改失败");
    }

    @Override
    public ResultVO cancelOrder(Long id, String reason) {
        // 查询订单
        Order order = getById(id);

        // 判断订单是否存在
        if (order == null) {
            return ResultVO.fail("订单不存在");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        if (!order.getUid().equals(userId)) {
            return ResultVO.fail("订单不存在");
        }
        // 判断订单是否已完成
        if (order.getStatus() != 0) {
            return ResultVO.fail("订单已完成");
        }
        // 判断订单是否已发货
        if (order.getLogisticsStatus() > 0) {
            return ResultVO.fail("订单已发货");
        }
        // 判断订单是否已付款
        if (order.getIsPay() != 0) {
            // 退款
            returnMoney(order, "取消订单失败");
        }

        // 退货
        returnGoods(id, "取消订单失败");

        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", id);

        List<OrderItem> orderItemList = orderItemMapper.selectList(queryWrapper);
        // 删除订单属性
        orderItemMapper.delete(new QueryWrapper<OrderItem>().eq("order_id", id));

        // 取消订单
        boolean result = removeById(id);
        if (!result) {
            throw new RuntimeException("取消订单失败");
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("order", order);
        resultMap.put("orderItem", orderItemList);
        // 取消订单登记
        cancelOrder(ORDER_KEY, order, orderItemList, reason);
        return ResultVO.ok(null,"取消订单成功");
    }

    @Resource
    private IBillService billService;

    @Override
    public ResultVO paymentOrder(Long id, Long points) {
        // 查询订单
        Order order = getById(id);
        // 判断订单是否存在
        if (order == null) {
            return ResultVO.fail("订单不存在");
        }
        // 判断订单是否已支付
        if (order.getIsPay() > 0) {
            return ResultVO.fail("订单已支付");
        }

        // 获取订单金额
        Double price = order.getPrice();

        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 获取用户钱包
        UserWaller userWaller = userWallerMapper.selectById(userId);

        // 使用积分抵扣减少的金额
        double reducePrice = 0d;
        // 判断是否积分抵扣
        if (points != null) {
            if (points < 1) {
                return ResultVO.fail("抵扣积分不能低于1");
            }
            // 获取用户积分
            Long userPoints = userWaller.getPoints();
            if (userPoints < points) {
                return ResultVO.fail("对不起，你的积分不足");
            }
            reducePrice = points / 100.00;
            BigDecimal bigDecimal = new BigDecimal(reducePrice);
            reducePrice = bigDecimal.setScale(2, RoundingMode.UP).doubleValue();
        }

        // 获取钱包余额
        Double balance = userWaller.getBalance();
        // 判断用户钱包是否足够
        if (balance + reducePrice < price) {
            return ResultVO.fail("钱包余额不足");
        }

        // 获取钱包积分
        Long userPoints = userWaller.getPoints();

        // 获取钱包累计消费
        Double spending = userWaller.getSpending();

        // 支付订单
        userWaller.setBalance(balance - price + reducePrice);
        userWaller.setSpending(spending + price - reducePrice);
        if (points != null) {
            userWaller.setPoints(userPoints - points);
        }
        int update = userWallerMapper.updateById(userWaller);
        if (update < 1) {
            return ResultVO.fail("支付失败");
        }

        // 支付成功
        order.setIsPay(1);
        order.setPaymentMethods(0);
        Date payDate = new Date();
        order.setPaymentTime(payDate);

        boolean result = updateById(order);
        if (!result) {
            throw new RuntimeException("订单支付失败");
        }

        // 保存用户账单
        UserBill userBill = userBillUtils.saveUserBill(userId, price - reducePrice, null, "购物消费", APPLICATION_NAME, BillType.disburse, "钱包", BillStatusCode.rechargeSuccess, "订单号：" + id);
        if (points != null) {
            userBillUtils.saveUserBill(userId, Double.valueOf(points.toString()), "积分", "购物消费抵扣", APPLICATION_NAME, BillType.disburse, "钱包", BillStatusCode.rechargeSuccess, "订单号：" + id);
        }
        // 保存系统账单
        billService.saveBill(userId, userBill, BillType.income);

        // 更新缓存
        stringRedisTemplate.opsForValue().set(USER_WALLER_KEY + ":" + userId, JSONUtil.toJsonStr(userWaller));

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("price", price); // 实际金额
        resultMap.put("paymentPrice", price - reducePrice); // 支付金额
        resultMap.put("points", points); // 抵扣积分
        resultMap.put("orderId", order.getId()); // 订单号
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = simpleDateFormat.format(payDate);
        resultMap.put("payTime", format); // 支付时间

        return ResultVO.ok(resultMap,"支付成功");
    }

    @Override
    public ResultVO queryOrderById(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询订单
        Order order = getById(id);
        if (order == null) {
            return ResultVO.fail("订单不存在");
        }
        // 判断是否下单用户本人
        if (!userId.equals(order.getUid())) {
            return ResultVO.fail("订单不存在");
        }

        OrderDTO orderDTO = setQueryDTO(order);

        return ResultVO.ok(orderDTO, "查询成功");
    }

    @Override
    public ResultVO queryOrderAll() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询全部订单
        List<Order> orders = list(new QueryWrapper<Order>().eq("uid", userId).orderByDesc("time").eq("isDel", 0));
        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orders);
        return ResultVO.ok(orderDTOS, "查询成功");
    }

    @Override
    public ResultVO queryUnpaidOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query()
                .eq("uid", userId)
                .eq("isPay", 0)
                .orderByDesc("time")
                .eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVO.ok(orderDTOS, "查询成功");
    }

    @Override
    public ResultVO queryBeShippedOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query()
                .eq("uid", userId)
                .eq("isPay", 1)
                .eq("logistics_status", 0)
                .orderByDesc("time")
                .eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVO.ok(orderDTOS, "查询成功");
    }

    @Override
    public ResultVO queryUndeliveredOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query()
                .eq("uid", userId)
                .eq("logistics_status", 1)
                .orderByDesc("time")
                .eq("isDel", 0).list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVO.ok(orderDTOS, "查询成功");
    }

    @Override
    public ResultVO queryCompletedOrder() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        List<Order> orderList = query()
                .eq("uid", userId)
                .eq("status", 1)
                .eq("logistics_status", 2)
                .eq("isDel", 0)
                .orderByDesc("time").list();

        ArrayList<OrderDTO> orderDTOS = setQueryDTOS(orderList);
        return ResultVO.ok(orderDTOS, "查询成功");
    }

    @Override
    public ResultVO completeOrder(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 查询订单
        Order order = query()
                .eq("id", id)
                .eq("uid", userId)
                .eq("isPay", 1)
                .eq("logistics_status", 1).one();

        // 判断订单是否为空
        if (order == null) {
            return ResultVO.fail("订单尚未送出或订单不存在，暂无法完成订单");
        }

        // 完成订单
        order.setLogisticsStatus(2); // 设置已收货
        order.setStatus(1); // 设置已完成

        boolean result = updateById(order);

        return result ? ResultVO.ok(null, "订单完成，欢迎再次购买") : ResultVO.fail("订单暂无法完成，请重试");
    }

    @Override
    public ResultVO deleteOrder(Long id) {
        // 查询订单
        Order order = getById(id);
        if (order == null) {
            return ResultVO.fail("订单不存在");
        }
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断订单是否属于用户
        if (!order.getUid().equals(userId)) {
            return ResultVO.fail("订单不存在");
        }
        // 判断订单是否已完成
        if (order.getStatus() < 1) {
            return ResultVO.fail("订单未完成");
        }

        // 删除订单
        boolean result = update().set("isDel", 1).eq("id", id).update();

        return result ? ResultVO.ok("订单删除成功") : ResultVO.fail("订单删除失败");
    }

    @Override
    public ResultVO queryOrder(Integer page, Integer size, OrderQueryDTO orderQueryDTO) {
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
            // 获取联系电话
            String phone = orderQueryDTO.getPhone();
            if (StrUtil.isNotBlank(phone)) {
                queryWrapper.like("phone", phone);
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
                // 发货状态，0未发货，1：待收货，2：已收货，3：待换货，4：已换货，5：待退货，6：已退货，
                switch (logisticsStatus) {
                    case 0: case 1: case 2: case 3: case 4: case 5: case 6: queryWrapper.eq("logistics_status", logisticsStatus);break;
                }
            }
            // 获取订单状态
            Integer status = orderQueryDTO.getStatus();
            if (status != null) {
                switch (status) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        queryWrapper.eq("status", status);
                        break;
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

        return ResultVO.ok(orderPage, "查询成功");
    }

    @Override
    public ResultVO updateOrder(OrderUpdateFromDTO updateFromDTO) {
        // 获取订单号
        Long orderId = updateFromDTO.getId();
        // 查询订单
        Order order = getById(orderId);
        // 判断订单是否存在
        if (order == null) {
            return ResultVO.fail("订单不存在！");
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
                // 退货
                returnGoods(order.getId(), "订单退货失败");
            }
        }
        // 修改订单状态
        Integer status = updateFromDTO.getStatus();
        if (status != null) {
            // 修改订单状态
            order.setStatus(status);
            // 判断是否已退款
            if (status == 3) {
                // 获取订单物流状态
                Integer orderLogisticsStatus = order.getLogisticsStatus();
                if (orderLogisticsStatus != null) {
                    if (orderLogisticsStatus == 0 || orderLogisticsStatus == 6) {
                        // 退款
                        returnMoney(order, "订单退款失败");
                    } else {
                        return ResultVO.fail("订单未退货，暂无法退款");
                    }
                }
            }
        }

        // 修改订单
        boolean result = updateById(order);

        return result ? ResultVO.ok(null,"订单修改成功！") : ResultVO.fail("订单修改失败！");
    }

    @Override
    public ResultVO orderCount(Long startDate, Long endDate) {
        // 获取起始时间
        Date start = new Date(startDate);
        // 获取截止时间
        Date end = new Date(endDate);
        // 判断截止时间是否早于起始时间
        if (!start.equals(end)) {
            if (start.after(end)) {
                return ResultVO.fail("截止时间不得早于起始时间");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startDateTime = simpleDateFormat.format(start);
        String endDateTime = simpleDateFormat.format(end);

        // 查询指定时间内的全部已付款订单
        List<Order> orderList = query().ge("time", startDateTime).le("time", endDateTime).orderByAsc("time").eq("isPay", 1).list();

        // 获取指定时间段中全部日期
        List<Date> dumDateList = TimeUtils.getDumDateList(startDate, endDate);
        List<Map<String, Object>> resultMap = new ArrayList<>();

        for (Date date : dumDateList) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat simpleDate1 = new SimpleDateFormat("yyyy:MM:dd");
            Map<String, Object> listMap = new HashMap<>();
            Map<String, Object> childMap = new HashMap<>();

            String nowDate = simpleDate.format(date);
            // 获取当日新增订单
            String addedOrder = stringRedisTemplate.opsForValue().get("icr:" + ORDER_KEY + simpleDate1.format(date));
            if (StrUtil.isNotBlank(addedOrder) && addedOrder != null) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("quantity", Integer.valueOf(addedOrder));
                map1.put("data", Collections.emptyList());
                childMap.put("addedOrder", map1);
            }

            // 获取当日支付订单
            List<Order> paymentOrderList = orderList.stream().filter(order -> {
                simpleDate.setTimeZone(TimeZone.getTimeZone("GTM+8"));
                if (order.getPaymentTime() == null) {
                    return false;
                }
                String format = simpleDate.format(order.getPaymentTime());
                return nowDate.equals(format);
            }).filter(order -> order.getIsPay().equals(1)).collect(Collectors.toList());
            Map<String, Object> map2 = new HashMap<>();
            map2.put("quantity", paymentOrderList.size());
            map2.put("data", paymentOrderList);
            childMap.put("paymentOrder", map2);

            // 获取当日退款订单
            List<Order> returnOrderList = orderList.stream().filter(order -> {
                simpleDate.setTimeZone(TimeZone.getTimeZone("GTM+8"));
                if (order.getReturnTime() == null) {
                    return false;
                }
                String format = simpleDate.format(order.getReturnTime());
                return nowDate.equals(format);
            }).filter(order -> order.getStatus().equals(3)).collect(Collectors.toList());
            Map<String, Object> map3 = new HashMap<>();
            map3.put("quantity", returnOrderList.size());
            map3.put("data", returnOrderList);
            childMap.put("returnOrder", map3);

            // 获取当日已完成订单
            List<Order> completedOrderList = orderList.stream().filter(order -> {
                simpleDate.setTimeZone(TimeZone.getTimeZone("GTM+8"));
                if (order.getTime() == null) {
                    return false;
                }
                String format = simpleDate.format(order.getTime());
                return nowDate.equals(format);
            }).filter(order -> order.getStatus().equals(1)).collect(Collectors.toList());
            Map<String, Object> map4 = new HashMap<>();
            map4.put("quantity", completedOrderList.size());
            map4.put("data", completedOrderList);
            childMap.put("completedOrder", map4);

            // 获取当日取消订单
            Set<String> keys = stringRedisTemplate.keys("cancel:" + ORDER_KEY + simpleDate1.format(date) + ":*");
            Map<String, Object> map5 = new HashMap<>();
            map5.put("quantity", 0);
            if (keys != null && !keys.isEmpty()) {
                map5.put("quantity", keys.size());
                List<Map<Object, Object>> dataList = new ArrayList<>();
                for (String key : keys) {
                    Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(key);
                    Object order = entries.get("order");
                    Object orderItems = entries.get("orderItems");
                    entries.put("order", JSONUtil.toBean(order.toString(), Order.class));
                    entries.put("orderItems", JSONUtil.toList(orderItems.toString(), OrderItem.class));
                    dataList.add(entries);
                }
                map5.put("data", dataList);
            }
            childMap.put("cancelOrder", map5);

            // 获得当日成交额
            double turnover = 0d;
            for (Order order : paymentOrderList) {
                turnover += order.getPrice();
            }
            childMap.put("turnover", turnover);

            // 获得当日退款额
            double deficit = 0d;
            for (Order order : returnOrderList) {
                deficit += order.getPrice();
            }
            childMap.put("deficit", deficit);

            // 获取当日总盈利
            double income = turnover - deficit;
            BigDecimal bigDecimal = new BigDecimal(income);
            // 将保留后两位数，并进行四舍五入
            income = bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
            childMap.put("income", income);

            listMap.put("time", nowDate);
            listMap.put("data", childMap);

            resultMap.add(listMap);
        }

        return ResultVO.ok(resultMap, "查询成功");
    }

    /**
     * 取消订单
     * @param keyPrefix key前缀
     * @param reason 取消原因
     */
    public void cancelOrder(String keyPrefix, Order order, List<OrderItem> orderItems, String reason) {
        Date now = new Date();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd");
        String date = simpleDateFormat.format(now);
        Map<String, Object> hashMap = new HashMap<>();

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        hashMap.put("order", JSONUtil.toJsonStr(order)); // 订单信息
        hashMap.put("orderItems", JSONUtil.toJsonStr(orderItems));
        hashMap.put("reason", reason); // 取消原因
        hashMap.put("time", simpleDateFormat1.format(now)); // 取消时间

        // 2.2. 订单量自减少
        stringRedisTemplate.opsForHash().putAll("cancel:" + keyPrefix + date + ":" + order.getId(), hashMap);
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
        if (paymentMethods != null) {
            if (paymentMethods >= 0) {
                switch (paymentMethods) {
                    case 0: orderDTO.setPaymentMethods("钱包支付");break;
                    case 1: orderDTO.setPaymentMethods("微信支付");break;
                    case 2: orderDTO.setPaymentMethods("支付宝支付");break;
                }
            }
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

            // 设置发货时间
            orderDTO.setShippingTime(TimeUtils.dateToStringTime(shippingTime));
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
                // 获取订单属性id
                Long id = orderItem.getId();
                // 设置订单属性
                orderItemDTO.setOrderItemId(id);

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

                    if (integer > 0) {
                        // 设置是否评论
                        orderItemDTO.setIsComment(integer);
                    } else {
                        orderItemDTO.setIsComment(0);
                    }
                }

                orderItemDTOS.add(orderItemDTO);
            }
        }

        // 设置订单属性详情
        orderDTO.setOrderItems(orderItemDTOS);

        return orderDTO;
    }

    private void returnMoney(Order order, String errMsg) {
        // 获取用户id
        Long userId = order.getUid();
        // 获取用户钱包
        UserWaller userWaller = userWallerMapper.selectById(userId);

        // 获取用户余额
        Double balance = userWaller.getBalance();
        // 退还付款
        userWaller.setBalance(balance + order.getPrice());

        int update = userWallerMapper.updateById(userWaller);

        if (update < 1) {
            throw new RuntimeException(errMsg);
        }

        // 新增账单
        UserBill userBill = userBillUtils.saveUserBill(userId, order.getPrice(), null, "退款", APPLICATION_NAME, BillType.income, null, BillStatusCode.rechargeSuccess, "订单号：" + order.getId());
        // 保存系统账单
        billService.saveBill(userId, userBill, BillType.disburse);

    }

    private void returnGoods(Long orderId, String errMsg) {
        // 获取订单属性
        QueryWrapper<OrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
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
                item.setSales(item.getSales() - quantity);

                // 获取商品id
                Long gid = item.getGid();
                // 查询商品
                Goods goods = goodsMapper.selectById(gid);
                // 修改商品销量
                goods.setSale(goods.getSale() - quantity);

                int updateGoods = goodsMapper.updateById(goods);
                int updateItem = goodsItemMapper.updateById(item);
                int updateOrderItem = orderItemMapper.deleteById(orderItem.getId());

                if (updateGoods < 1 || updateItem < 1 || updateOrderItem < 1) {
                    throw new RuntimeException(errMsg);
                }
            }
        }
    }
}
