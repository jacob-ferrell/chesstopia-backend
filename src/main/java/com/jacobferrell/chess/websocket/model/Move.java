package com.jacobferrell.chess.websocket.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Move {
    private String senderEmail;
    private String receiverEmail;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;
    private String date;
}
