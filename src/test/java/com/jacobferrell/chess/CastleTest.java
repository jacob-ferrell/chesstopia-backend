package com.jacobferrell.chess;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.jacobferrell.chess.chessboard.ChessBoard;
import com.jacobferrell.chess.chessboard.Position;
import com.jacobferrell.chess.pieces.PieceColor;
import com.jacobferrell.chess.pieces.Rook;

public class CastleTest {
    // Test getCastleRooks for king side
    @Test
    public void testKingSide() {
        ChessBoard board = new ChessBoard();
        board.removePieceAtPosition(new Position(5, 0));
        board.removePieceAtPosition(new Position(6, 0));
        board.removePieceAtPosition(new Position(5, 7));
        board.removePieceAtPosition(new Position(6, 7));
        System.out.println(board);
        Set<Rook> white = board.getCastleRooks(PieceColor.WHITE);
        Set<Rook> black = board.getCastleRooks(PieceColor.BLACK);
        for (Rook i : white) {
            System.out.println(i);
        }
        for (Rook i : black) {
            System.out.println(i);
        }
    }
}
