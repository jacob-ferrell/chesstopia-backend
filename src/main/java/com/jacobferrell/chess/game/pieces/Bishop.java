package com.jacobferrell.chess.game.pieces;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;


public class Bishop extends ChessPiece {

    public Bishop(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.BISHOP, DIAGONAL_MOVE_SET);
        this.symbol = color == PieceColor.WHITE ? '♗' : '♝';
        this.rank = 4;
    }

}
