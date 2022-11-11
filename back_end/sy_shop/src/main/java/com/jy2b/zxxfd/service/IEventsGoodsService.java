package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.EventsGoods;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IEventsGoodsService extends IService<EventsGoods> {
    /**
     * 新增活动商品
     * @param eventsId 活动id
     * @param ids 活动商品ids
     * @return ResultVo
     */
    ResultVo saveEventsGoods(Long eventsId, List<Long> ids);

    /**
     * 删除活动商品
     * @param eventsId 活动id
     * @param goodsId 活动商品id
     * @return ResultVo
     */
    ResultVo delEventsGoods(Long eventsId, Long goodsId);

    /**
     * 查询活动商品
     * @param eventsId 活动id
     * @return ResultVo
     */
    ResultVo queryEventsGoods(Integer page, Integer size, Long eventsId);
}
