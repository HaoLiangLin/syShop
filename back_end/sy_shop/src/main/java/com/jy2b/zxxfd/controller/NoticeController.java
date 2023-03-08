package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.NoticeDTO;
import com.jy2b.zxxfd.domain.dto.NoticeQueryDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.INoticeService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/notices")
@CrossOrigin
@Api(tags = "公告相关接口")
public class NoticeController {
    @Resource
    private INoticeService noticeService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('notice:save')")
    public ResultVO saveNotice(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.saveNotice(noticeDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('notice:delete')")
    public ResultVO delNotice(@PathVariable("id") Long id) {
        return noticeService.delNotice(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('notice:update')")
    public ResultVO updateNotice(@PathVariable("id") Long id, @RequestBody NoticeDTO noticeDTO) {
        return noticeService.updateNotice(id, noticeDTO);
    }

    @PostMapping("/query")
    public ResultVO queryNotice(@RequestBody NoticeQueryDTO queryDTO) {
        return noticeService.queryNotice(queryDTO);
    }

    @PostMapping("/query/{page}/{size}")
    public ResultVO queryNoticePage(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) NoticeQueryDTO queryDTO) {
        return noticeService.queryNoticePage(page, size, queryDTO);
    }

    @GetMapping("/index")
    public ResultVO indexNotice() {
        return noticeService.indexNotice();
    }

    @PutMapping("/index/{noticeId}")
    @PreAuthorize("hasAnyAuthority('notice:update')")
    public ResultVO setIndexNoticeId(@PathVariable("noticeId") Long id) {
        return noticeService.setIndexNoticeId(id);
    }
}
