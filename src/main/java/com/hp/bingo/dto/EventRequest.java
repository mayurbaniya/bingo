package com.hp.bingo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventRequest {

    private String eventName;
    private String eventDate;
    private String location;
    private String status;
    private String entryFee;

}
