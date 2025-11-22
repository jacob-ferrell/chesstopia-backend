package com.jacobferrell.chess.game.pieces;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;

public enum PieceColor {
    WHITE,
    BLACK;


    public static PieceColor fromGameAndUser(GameEntity gameEntity, User user) {
        return gameEntity.getWhitePlayer().equals(user) ? PieceColor.WHITE : PieceColor.BLACK;
    }


    public PieceColor enemy() {
        if (this == WHITE) return BLACK;
        return WHITE;
    }

}
