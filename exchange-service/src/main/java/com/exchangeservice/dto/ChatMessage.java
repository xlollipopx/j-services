package com.exchangeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String senderName;
    private String receiverName;
    private String message;
    private String date;
    private String status;
}
