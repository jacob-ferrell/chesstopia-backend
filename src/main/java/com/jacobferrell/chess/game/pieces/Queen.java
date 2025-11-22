package com.jacobferrell.chess.game.pieces;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Stream;

public class Queen extends ChessPiece {

    private static final List<Integer[]> moveset = Stream.of(
            VERTICAL_MOVE_SET, HORIZONTAL_MOVE_SET, DIAGONAL_MOVE_SET
    ).flatMap(List::stream).toList();

    public Queen(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.QUEEN, moveset);
        this.symbol = color == PieceColor.WHITE ? '♕' : '♛';
        this.rank = 2;
    }

}
