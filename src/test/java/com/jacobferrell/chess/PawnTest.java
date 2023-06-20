package com.jacobferrell.chess;
import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class PawnTest {
    @Test
    public void testPawn() {
        ChessBoard board = new ChessBoard();
        ChessPiece piece = board.getPieceAtPosition(new Position(3, 6));
        Pawn pawn = (Pawn) piece;
        board.setPieceAtPosition(new Position(3, 4), pawn);
        Set<Move> possibleMoves = pawn.generatePossibleMoves();
        for (Move move : possibleMoves) {
            System.out.println(move.position.y + "," + move.position.x);
        }
    }

}
