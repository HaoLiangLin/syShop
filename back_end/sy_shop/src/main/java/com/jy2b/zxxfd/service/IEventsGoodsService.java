package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.EventsGoods;
import com.jy2b.zxxfd.domain.vo.ResultVO;
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
    ResultVO saveEventsGoods(Long eventsId, List<Long> ids);

    /**
     * 删除活动商品
     * @param eventsId 活动id
     * @param goodsId 活动商品id
     * @return ResultVo
     */
    ResultVO delEventsGoods(Long eventsId, Long goodsId);

    /**
     * 查询活动商品
     * @param eventsId 活动id
     * @return ResultVo
     */
    ResultVO queryEventsGoods(Integer page, Integer size, Long eventsId);
}
