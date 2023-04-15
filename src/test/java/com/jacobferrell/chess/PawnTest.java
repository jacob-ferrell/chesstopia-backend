package com.jacobferrell.chess;
import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class PawnTest {
    @Test
    public void testPawn() {
        ChessBoard board = new ChessBoard();
        ChessPiece piece = board.getPieceAtPosition(3, 6);
        Pawn pawn = (Pawn) piece;
        Set<Position> possibleMoves = pawn.generatePossibleMoves();
        for (Position i : possibleMoves) {
            System.out.println(i);
        }
    }

}
