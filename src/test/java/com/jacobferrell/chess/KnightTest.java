package com.jacobferrell.chess;

import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class KnightTest {
    @Test
    public void testKnight() {
        ChessBoard board = new ChessBoard();
        ChessPiece piece = board.getPieceAtPosition(1, 7);
        Knight knight = (Knight) piece;
        Set<Position> possibleMoves = knight.generatePossibleMoves();
        for (Position i : possibleMoves) {
            System.out.println(i.getY() + "," + i.getX());
        }
    }
}
