package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.NoticeDTO;
import com.jy2b.zxxfd.domain.dto.NoticeQueryDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
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
    public ResultVo saveNotice(@RequestBody NoticeDTO noticeDTO) {
        return noticeService.saveNotice(noticeDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('notice:delete')")
    public ResultVo delNotice(@PathVariable("id") Long id) {
        return noticeService.delNotice(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('notice:update')")
    public ResultVo updateNotice(@PathVariable("id") Long id, @RequestBody NoticeDTO noticeDTO) {
        return noticeService.updateNotice(id, noticeDTO);
    }

    @PostMapping("/query")
    public ResultVo queryNotice(@RequestBody NoticeQueryDTO queryDTO) {
        return noticeService.queryNotice(queryDTO);
    }

    @PostMapping("/query/{page}/{size}")
    public ResultVo queryNoticePage(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) NoticeQueryDTO queryDTO) {
        return noticeService.queryNoticePage(page, size, queryDTO);
    }
}
