package com.jy2b.zxxfd.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_events_goods")
public class EventsGoods {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("events_id")
    private Long eventsId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @TableField("goods_id")
    private Long goodsId;
}
