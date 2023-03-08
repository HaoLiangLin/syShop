package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Notice;
import com.jy2b.zxxfd.domain.dto.NoticeDTO;
import com.jy2b.zxxfd.domain.dto.NoticeQueryDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.NoticeCategoryMapper;
import com.jy2b.zxxfd.mapper.NoticeMapper;
import com.jy2b.zxxfd.service.INoticeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.NOTICE_KEY;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {
    @Resource
    private NoticeCategoryMapper categoryMapper;

    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO saveNotice(NoticeDTO noticeDTO) {
        // 获取公告标题
        String title = noticeDTO.getTitle();
        if (StrUtil.isBlank(title)) {
            return ResultVO.fail("公告标题不能为空");
        }
        // 获取公告类型
        Long cid = noticeDTO.getCid();
        if (cid == null) {
            return ResultVO.fail("公告类型不能为空");
        }
        if (categoryMapper.selectById(cid) == null) {
            return ResultVO.fail("公告类型不存在");
        }
        // 获取公告内容
        String content = noticeDTO.getContent();
        if (StrUtil.isBlank(content)) {
            return ResultVO.fail("公告内容不能为空");
        }

        // 新增公告
        Notice notice = BeanUtil.toBean(noticeDTO, Notice.class);
        boolean result = save(notice);
        if (result) {
            saveNoticeCache(notice);
        }
        return result ? ResultVO.ok(null, "公告发布成功") : ResultVO.fail("公告发布失败");
    }

    @Override
    public ResultVO delNotice(Long id) {
        // 获取公告
        Notice notice = getById(id);
        if (notice == null) {
            return ResultVO.fail("公告不存在");
        }

        // 删除公告
        boolean result = removeById(id);
        if (result) {
            // 删除公告缓存
            stringRedisTemplate.delete(NOTICE_KEY + id);
        }
        return result ? ResultVO.ok(null,"删除公告成功") : ResultVO.fail("删除公告失败");
    }

    @Override
    public ResultVO updateNotice(Long id, NoticeDTO noticeDTO) {
        // 获取公告
        Notice notice = getById(id);
        if (notice == null) {
            return ResultVO.fail("公告不存在");
        }

        // 获取公告类型
        Long cid = noticeDTO.getCid();
        if (cid != null) {
            if (categoryMapper.selectById(cid) == null) {
                return ResultVO.fail("公告类型不存在");
            }
        }

        // 修改公告
        if (noticeDTO.getTitle() != null) {
            notice.setTitle(noticeDTO.getTitle());
        }
        if (noticeDTO.getCid() != null) {
            notice.setCid(noticeDTO.getCid());
        }
        if (noticeDTO.getContent() != null) {
            notice.setContent(noticeDTO.getContent());
        }
        boolean result = updateById(notice);

        if (result) {
            saveNoticeCache(notice);
        }

        return result ? ResultVO.ok(null,"修改公告成功") : ResultVO.fail("修改公告失败");
    }

    @Override
    public ResultVO queryNotice(NoticeQueryDTO queryDTO) {
        if (queryDTO.getId() != null && queryDTO.getNoticeCategoryId() == null) {
            Long id = queryDTO.getId();
            String result = stringRedisTemplate.opsForValue().get(NOTICE_KEY + id);

            if (StrUtil.isNotBlank(result)) {
                Notice notice = JSONUtil.toBean(result, Notice.class);
                return ResultVO.ok(notice, "查询成功");
            }
        }

        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (queryDTO.getId() != null) {
            queryWrapper.eq("id", queryDTO.getId());
        }
        if (queryDTO.getNoticeCategoryId() != null) {
            queryWrapper.eq("cid", queryDTO.getNoticeCategoryId());
        }
        String timeSort = queryDTO.getTimeSort();
        if (StrUtil.isNotBlank(timeSort)) {
            switch (timeSort) {
                case "Asc":
                    queryWrapper.orderByAsc("update_time");break;
                case "Des":
                    queryWrapper.orderByDesc("update_time");break;
            }
        }
        List<Notice> result = list(queryWrapper);

        if (queryDTO.getId() != null && queryDTO.getNoticeCategoryId() == null) {
            Notice notice = result.get(0);
            if (notice != null) {
                return ResultVO.ok(saveNoticeCache(notice), "查询成功");
            }
        }

        return ResultVO.ok(result, "查询成功");
    }

    @Override
    public ResultVO queryNoticePage(Integer page, Integer size, NoticeQueryDTO queryDTO) {
        Page<Notice> noticePage = new Page<>(page, size);
        QueryWrapper<Notice> queryWrapper = null;

        if (queryDTO != null) {
            queryWrapper = new QueryWrapper<>();
            if (queryDTO.getId() != null) {
                queryWrapper.eq("id", queryDTO.getId());
            }
            if (queryDTO.getNoticeCategoryId() != null) {
                queryWrapper.eq("cid", queryDTO.getNoticeCategoryId());
            }
            String timeSort = queryDTO.getTimeSort();
            if (StrUtil.isNotBlank(timeSort)) {
                switch (timeSort) {
                    case "Asc":
                        queryWrapper.orderByAsc("update_time");break;
                    case "Des":
                        queryWrapper.orderByDesc("update_time");break;
                }
            }
        }

        noticeMapper.selectPage(noticePage, queryWrapper);
        return ResultVO.ok(noticePage, "查询成功");
    }

    private Notice saveNoticeCache(Notice notice) {
        // 获取公告id
        Long noticeId = notice.getId();
        // 拼接key
        String key = NOTICE_KEY + noticeId;

        String jsonStr = JSONUtil.toJsonStr(notice);
        stringRedisTemplate.opsForValue().set(key, jsonStr);

        return notice;
    }
}
