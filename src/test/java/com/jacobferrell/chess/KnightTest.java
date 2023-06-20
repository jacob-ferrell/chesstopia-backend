package com.jacobferrell.chess;

import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class KnightTest {
    @Test
    public void testKnight() {
        ChessBoard board = new ChessBoard();
        ChessPiece piece = board.getPieceAtPosition(new Position(1, 7));
        Knight knight = (Knight) piece;
        Set<Move> possibleMoves = knight.generatePossibleMoves();
        for (Move move : possibleMoves) {
            System.out.println(move.position.y + "," + move.position.x);
        }
    }
}
