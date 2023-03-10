package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Events;
import com.jy2b.zxxfd.domain.dto.EventsDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 林武泰
 * 活动业务接口
 */
@Transactional
public interface IEventsService extends IService<Events> {
    /**
     * 新增活动
     * @param saveDTO 活动信息
     * @return ResultVo
     */
    ResultVO saveEvents(EventsDTO saveDTO);

    /**
     * 删除活动
     * @param id 活动id
     * @return ResultVo
     */
    ResultVO delEvents(Long id);

    /**
     * 修改活动
     * @param id 活动id
     * @param updateDTO 活动修改信息
     * @return ResultVo
     */
    ResultVO updateEvents(Long id, EventsDTO updateDTO);

    /**
     * 上传或修改活动图标
     * @param id 活动ID
     * @param file 活动图片文件
     * @return ResultVO
     */
    ResultVO uploadOrUpdateEventsIcon(Long id, MultipartFile file);

    /**
     * 查询活动
     * @return ResultVo
     */
    ResultVO queryEvents();
}
