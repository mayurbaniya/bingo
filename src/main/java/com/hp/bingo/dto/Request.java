package com.hp.bingo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Request {

    private String name;
    private String phone;
    private String email;
    private int tickets;
    private long amountPaid; // in paise
    private boolean payNow;

    private boolean paymentConfirmed = false;
}
