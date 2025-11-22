package com.jacobferrell.chess.dto;

import com.jacobferrell.chess.game.pieces.PieceType;

public record MoveParams(int x0, int y0, int x1, int y1, PieceType promotion) {
}
