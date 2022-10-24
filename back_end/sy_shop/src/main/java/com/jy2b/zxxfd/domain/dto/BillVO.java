package com.jy2b.zxxfd.domain.dto;

import lombok.Data;

import java.util.ArrayList;

@Data
public class BillVO {
    private String date;

    private ArrayList<BillDTO> bill;
}
