package com.jacobferrell.chess.game.pieces;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Knight extends ChessPiece {

    public static final List<Integer[]> MOVESET = List.of(
            new Integer[]{2, 1},
            new Integer[]{2, -1},
            new Integer[]{-2, 1},
            new Integer[]{-2, -1},
            new Integer[]{1, 2},
            new Integer[]{1, -2},
            new Integer[]{-1, 2},
            new Integer[]{-1, -2}
    );

    public Knight(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.KNIGHT, MOVESET, 1);
        this.symbol = color == PieceColor.WHITE ? '♘' : '♞';
        this.rank = 5;
    }

}
