package com.jy2b.zxxfd.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;

/**
 * @author 林武泰
 */
@Data
public class GoodsEvaluationDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String userIcon;

    private String nickname;

    private String time;

    private Integer stars;

    private String parameter;

    private String content;

    private ArrayList<String> images;

    private Integer liked;

    private Integer isLiked;

    private Integer isMe;

    private Integer comment;
}
