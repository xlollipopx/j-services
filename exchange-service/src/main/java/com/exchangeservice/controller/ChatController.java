//package com.exchangeservice.controller;
//
//import com.exchangeservice.dto.ChatMessage;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//@RequestMapping("/chat")
//public class ChatController {
//
//    private SimpMessagingTemplate simpMessagingTemplate;
//
//    @MessageMapping("/message")
//    @SendTo("/chatroom/public")
//    private ChatMessage receivePublicMessage(@Payload ChatMessage message) {
//        return message;
//    }
//
//    @MessageMapping("/private-message")
//    private ChatMessage receivePrivateMessage(@Payload ChatMessage message) {
//        // ex: /user/Davie/private
//        simpMessagingTemplate.convertAndSendToUser(message.getReceiverName(), "/private", message);
//        return message;
//    }
//
//}
