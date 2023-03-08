package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Events;
import com.jy2b.zxxfd.domain.EventsGoods;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.dto.GoodsDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.EventsGoodsMapper;
import com.jy2b.zxxfd.mapper.EventsMapper;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.service.IEventsGoodsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventsGoodsServiceImpl extends ServiceImpl<EventsGoodsMapper, EventsGoods> implements IEventsGoodsService {
    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper itemMapper;

    @Resource
    private EventsMapper eventsMapper;

    @Resource
    private EventsGoodsMapper eventsGoodsMapper;

    @Override
    public ResultVO saveEventsGoods(Long eventsId, List<Long> ids) {
        // 查询活动
        Events events = eventsMapper.selectById(eventsId);
        if (events == null) {
            return ResultVO.fail("活动不存在");
        }

        if (ids.isEmpty()) {
            return ResultVO.fail("活动商品id不能为空");
        }

        int success = 0;
        List<Goods> goodsList = new ArrayList<>();
        for (Long id : ids) {
            Goods goods = goodsMapper.selectById(id);
            if (goods != null && goods.getStatus() > 0) {
                EventsGoods one = query().eq("events_id", eventsId).eq("goods_id", id).one();
                if (one == null) {
                    boolean result = save(new EventsGoods(eventsId, id));
                    if (result) {
                        goodsList.add(goods);
                        success++;
                    }
                }
            }
        }
        return success > 0 ? ResultVO.ok(goodsList,"新增活动商品：" + success) : ResultVO.fail("新增活动商品失败");
    }

    @Override
    public ResultVO delEventsGoods(Long eventsId, Long goodsId) {
        EventsGoods one = query().eq("events_id", eventsId).eq("goods_id", goodsId).one();
        if (one == null) {
            return ResultVO.fail("活动商品不存在");
        }
        // 删除活动商品
        boolean result = remove(new QueryWrapper<EventsGoods>().eq("events_id", eventsId).eq("goods_id", goodsId));
        return result ? ResultVO.ok(one,"删除活动商品成功") : ResultVO.fail("删除活动商品失败");
    }

    @Override
    public ResultVO queryEventsGoods(Integer page, Integer size, Long eventsId) {
        // 分页查询
        Page<EventsGoods> eventsGoodsPage = new Page<>(page, size);

        // 条件构造器
        QueryWrapper<EventsGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("events_id", eventsId);

        eventsGoodsMapper.selectPage(eventsGoodsPage, queryWrapper);

        // 分页后得到的活动商品数据
        List<EventsGoods> eventsGoods = eventsGoodsPage.getRecords();

        ArrayList<GoodsDTO> goodsList = new ArrayList<>();
        for (EventsGoods eventsGood : eventsGoods) {
            // 根据商品id查询商品
            Goods goods = goodsMapper.selectById(eventsGood.getGoodsId());
            // 判断商品是否存在并已上架
            if (goods != null && goods.getStatus() > 0) {
                // 获取当前商品的第一个商品属性
                GoodsItem goodsItem = itemMapper.selectList(new QueryWrapper<GoodsItem>().eq("gid", goods.getId()).eq("status", 1)).get(0);
                // 将商品数据，商品价格，商品则扣
                goodsList.add(new GoodsDTO(goods, goodsItem.getPrice(), goodsItem.getDiscount()));
            }
        }

        Page<GoodsDTO> goodsPage = new Page<>();
        goodsPage.setRecords(goodsList);
        goodsPage.setSize(eventsGoodsPage.getSize());
        goodsPage.setCountId(eventsGoodsPage.getCountId());
        goodsPage.setCurrent(eventsGoodsPage.getCurrent());
        goodsPage.setHitCount(eventsGoodsPage.isHitCount());
        goodsPage.setMaxLimit(eventsGoodsPage.getMaxLimit());
        goodsPage.setTotal(eventsGoodsPage.getTotal());
        goodsPage.setSearchCount(eventsGoodsPage.isSearchCount());
        goodsPage.setOrders(eventsGoodsPage.getOrders());

        return ResultVO.ok(goodsPage, "查询成功");
    }
}
