package com.jacobferrell.chess.game;

import com.jacobferrell.chess.pieces.PieceColor;

public class Player {
    private String name;
    private PieceColor color;

    public Player(String name, PieceColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setColor(PieceColor newColor) {
        color = newColor;
    }
    
}
