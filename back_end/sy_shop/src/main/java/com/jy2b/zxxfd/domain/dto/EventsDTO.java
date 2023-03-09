package com.jy2b.zxxfd.domain.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author 林武泰
 */
@Data
public class EventsDTO {
    private String name; // 活动名称
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime; // 开始时间
    private Integer deadline; // 活动期限
    private String remark; // 活动备注
}
