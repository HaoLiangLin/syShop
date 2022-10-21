package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class GoodsCommentDTO {
    private String userIcon;

    private String nickname;

    private String time;

    private Integer stars;

    private String parameter;

    private String content;

    private ArrayList<String> images;

    private Integer liked;
}
