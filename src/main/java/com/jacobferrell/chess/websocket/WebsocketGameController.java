package com.jacobferrell.chess.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.jacobferrell.chess.websocket.model.Move;

@Controller
public class WebsocketGameController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    
    @MessageMapping("/move")
    public Move receiveMove(@Payload Move move) {
        simpMessagingTemplate.convertAndSendToUser(move.getReceiverEmail(), "/game", move);
        return move;
    }
}
