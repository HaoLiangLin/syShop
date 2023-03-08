package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.EventsDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IEventsService;
import com.jy2b.zxxfd.utils.UploadUtils;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/events")
@CrossOrigin
@Api(tags = "活动相关接口")
public class EventsController {
    @Resource
    private IEventsService eventsService;

    @PostMapping("/uploadIcon")
    @PreAuthorize("hasAnyAuthority('events:save')")
    public ResultVO uploadIcon(@RequestPart("file") MultipartFile file) {
        return UploadUtils.saveFile(file, "/events/icon");
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('events:save')")
    public ResultVO saveEvents(@RequestBody EventsDTO eventsDTO) {
        return eventsService.saveEvents(eventsDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('events:delete')")
    public ResultVO delEvents(@PathVariable("id") Long id) {
        return eventsService.delEvents(id);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('events:update')")
    public ResultVO updateEvents(@PathVariable("id") Long id, @RequestBody EventsDTO eventsDTO) {
        return eventsService.updateEvents(id, eventsDTO);
    }

    @GetMapping("/query")
    public ResultVO queryEvents() {
        return eventsService.queryEvents();
    }
}
