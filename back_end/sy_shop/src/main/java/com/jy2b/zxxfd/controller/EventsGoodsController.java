package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IEventsGoodsService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/eventsGoods")
@CrossOrigin
@Api(tags = "活动商品相关接口")
public class EventsGoodsController {
    @Resource
    private IEventsGoodsService eventsGoodsService;

    @PostMapping("/save/{id}")
    @PreAuthorize("hasAnyAuthority('events:goods:save')")
    public ResultVO saveEventsGoods(@PathVariable("id") Long eventsId, @RequestParam List<Long> ids) {
        return eventsGoodsService.saveEventsGoods(eventsId, ids);
    }

    @DeleteMapping("/delete/{eventsId}/{goodsId}")
    @PreAuthorize("hasAnyAuthority('events:goods:delete')")
    public ResultVO delEventsGoods(@PathVariable("eventsId") Long eventsId, @PathVariable("goodsId") Long goodsId) {
        return eventsGoodsService.delEventsGoods(eventsId, goodsId);
    }

    @GetMapping("/query/{page}/{size}/{id}")
    public ResultVO queryEventsGoods(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @PathVariable("id") Long id) {
        return eventsGoodsService.queryEventsGoods(page, size, id);
    }
}
