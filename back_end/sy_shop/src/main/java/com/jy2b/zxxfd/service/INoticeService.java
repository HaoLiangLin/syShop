package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Notice;
import com.jy2b.zxxfd.domain.dto.NoticeDTO;
import com.jy2b.zxxfd.domain.dto.NoticeQueryDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface INoticeService extends IService<Notice> {
    /**
     * 新增公告
     * @param noticeDTO 公告信息
     * @return ResultVo
     */
    ResultVO saveNotice(NoticeDTO noticeDTO);

    /**
     * 删除公告
     * @param id 公告id
     * @return ResultVo
     */
    ResultVO delNotice(Long id);

    /**
     * 修改公告
     * @param id 公告id
     * @param noticeDTO 公告信息
     * @return ResultVo
     */
    ResultVO updateNotice(Long id, NoticeDTO noticeDTO);

    /**
     * 查询公告
     * @param queryDTO 公告查询条件
     * @return ResultVo
     */
    ResultVO queryNotice(NoticeQueryDTO queryDTO);

    /**
     * 分页查询公告
     * @param page 页数
     * @param size 每页数量
     * @param queryDTO 查询条件
     * @return ResultVo
     */
    ResultVO queryNoticePage(Integer page, Integer size, NoticeQueryDTO queryDTO);

    /**
     * 首要公告
     * @return ResultVO
     */
    ResultVO indexNotice();

    /**
     * 设置首要公告Id
     * @param id 公告Id
     * @return ResultVO
     */
    ResultVO setIndexNoticeId(Long id);
}
