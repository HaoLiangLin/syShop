package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Events;
import com.jy2b.zxxfd.domain.dto.EventsDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IEventsService extends IService<Events> {
    /**
     * 新增活动
     * @param saveDTO 活动信息
     * @return ResultVo
     */
    ResultVo saveEvents(EventsDTO saveDTO);

    /**
     * 删除活动
     * @param id 活动id
     * @return ResultVo
     */
    ResultVo delEvents(Long id);

    /**
     * 修改活动
     * @param id 活动id
     * @param updateDTO 活动修改信息
     * @return ResultVo
     */
    ResultVo updateEvents(Long id, EventsDTO updateDTO);

    /**
     * 查询活动
     * @return ResultVo
     */
    ResultVo queryEvents();
}
