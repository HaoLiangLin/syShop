package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Events;
import com.jy2b.zxxfd.domain.dto.EventsDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.EventsMapper;
import com.jy2b.zxxfd.service.IEventsService;
import com.jy2b.zxxfd.utils.UploadUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.EVENTS_KEY;

@Service
public class EventsServiceImpl extends ServiceImpl<EventsMapper, Events> implements IEventsService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO saveEvents(EventsDTO saveDTO) {
        // 获取活动名称
        String name = saveDTO.getName();
        if (StrUtil.isBlank(name)) {
            return ResultVO.fail("活动名称不能为空");
        }
        // 获取活动图片
        String icon = saveDTO.getIcon();
        if (StrUtil.isBlank(icon)) {
            return ResultVO.fail("活动图片不能为空");
        }

        // 获取活动期限
        Integer deadline = saveDTO.getDeadline();
        if (deadline == null) {
            deadline = 1;
        }
        if (deadline < -1 || deadline == 0) {
            return ResultVO.fail("活动期限不得小于-1或等于0");
        }

        // 获取活动期限
        if (deadline <= 1) {
            if (endTimeIsBeforeNow(saveDTO.getStartTime(), deadline)) {
                return ResultVO.fail("活动结束时间不得早于当前时间");
            }
        }

        Events events = BeanUtil.toBean(saveDTO, Events.class);
        // 新增活动
        boolean result = save(events);
        if (result) {
            saveEventsCache();
        } else {
            String image = events.getIcon();
            UploadUtils.deleteFile(image);
        }
        return result ? ResultVO.ok(events,"新增活动成功") : ResultVO.fail("新增活动失败");
    }

    @Override
    public ResultVO delEvents(Long id) {
        // 查询活动
        Events events = getById(id);
        if (events == null) {
            return ResultVO.fail("活动不存在");
        }

        // 获取图片
        String icon = events.getIcon();

        // 删除活动
        boolean result = removeById(id);
        if (result) {
            if (StrUtil.isNotBlank(icon)) {
                UploadUtils.deleteFile(icon);
            }
            saveEventsCache();
        }
        return result ? ResultVO.ok(events,"删除活动成功") : ResultVO.fail("删除活动失败");
    }

    @Override
    public ResultVO updateEvents(Long id, EventsDTO updateDTO) {
        // 查询活动
        Events events = getById(id);
        if (events == null) {
            return ResultVO.fail("活动不存在");
        }

        Integer deadline = updateDTO.getDeadline();
        if (deadline != null) {
            if (deadline < -1 || deadline == 0) {
                return ResultVO.fail("活动期限不得小于-1或等于0");
            }
        }

        if (updateDTO.getStartTime() != null) {
            if (deadline == null) {
                if (endTimeIsBeforeNow(updateDTO.getStartTime(), events.getDeadline())) {
                    return ResultVO.fail("活动结束时间不得早于当前时间");
                }
            } else {
                if (endTimeIsBeforeNow(updateDTO.getStartTime(), deadline)) {
                    return ResultVO.fail("活动结束时间不得早于当前时间");
                }
            }
        }

        // 修改活动
        Events update = BeanUtil.toBean(updateDTO, Events.class);
        update.setId(id);
        boolean result = updateById(update);
        if (result) {
            saveEventsCache();
        }
        return result ? ResultVO.ok(null,"修改活动成功") : ResultVO.fail("修改活动失败");
    }

    @Override
    public ResultVO queryEvents() {
        // 查询Redis
        String result = stringRedisTemplate.opsForValue().get(EVENTS_KEY);
        if (StrUtil.isNotBlank(result)) {
            List<Events> events = JSONUtil.toList(result, Events.class);
            events = delEndTimeEvents(events);
            return ResultVO.ok(events, "查询成功");
        }

        List<Events> events = list();
        // 判断是否不为空
        if (!events.isEmpty()) {
            String jsonStr = JSONUtil.toJsonStr(events);
            // 存入redis
            stringRedisTemplate.opsForValue().set(EVENTS_KEY, jsonStr);
        }
        return ResultVO.ok(events, "查询成功");
    }

    /**
     * 比较结束时间是否在当前时间之前
     * @param startTime 开始时间
     * @param deadline 活动期限
     * @return boolean
     */
    private boolean endTimeIsBeforeNow(Date startTime, long deadline) {
        if (deadline == -1) {
            return false;
        }

        // 获取开始时间
        long start = startTime.getTime();
        // 获取截止期限
        long deadlineTime = deadline * 24 * 60 * 60 * 1000;
        // 获取结束时间
        Date endTime = new Date(start + deadlineTime);

        // 获取当前时间
        Date date = new Date();

        // 判断结束日期是否在当前时间之前
        return endTime.before(date);
    }

    private List<Events> delEndTimeEvents(List<Events> events) {
        boolean isDel = false;
        for (Events event : events) {
            // 获取开始时间
            Date startTime = event.getStartTime();
            // 获取活动期限
            Integer deadline = event.getDeadline();

            // 判断是否到期
            if (endTimeIsBeforeNow(startTime, deadline)) {
                // 获取活动图片
                String icon = event.getIcon();
                boolean result = removeById(event.getId());
                if (result) {
                    if (StrUtil.isNotBlank(icon)) {
                        UploadUtils.deleteFile(icon);
                    }
                }
                isDel = true;
            }
        }
        if (isDel) {
            return saveEventsCache();
        }
        return events;
    }

    /**
     * 保存活动事件到缓存
     */
    private List<Events> saveEventsCache() {
        List<Events> events = list();
        // 判断是否不为空
        if (!events.isEmpty()) {
            String jsonStr = JSONUtil.toJsonStr(events);
            // 存入redis
            stringRedisTemplate.opsForValue().set(EVENTS_KEY, jsonStr);
        }

        return events;
    }
}
