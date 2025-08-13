package com.hp.bingo.dto;


import com.hp.bingo.constants.AppConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    private String msg;
    private Object data;
    private String err;
    private AppConstant status;

}