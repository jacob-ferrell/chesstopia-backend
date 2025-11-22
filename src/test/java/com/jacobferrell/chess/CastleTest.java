package com.jacobferrell.chess;

import org.junit.jupiter.api.Test;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;

public class CastleTest {
    // Test getCastleRooks for king side
    @Test
    public void testKingSide() {
        ChessBoard board = new ChessBoard();
        board.removePiece(new Position(5, 0));
        board.removePiece(new Position(6, 0));
        board.removePiece(new Position(5, 7));
        board.removePiece(new Position(6, 7));
        var rook = board.getPieceAtPosition(new Position(7, 0));

        /* System.out.println(board);
        Set<Rook> white = board.getCastleRooks(PieceColor.WHITE);
        Set<Rook> black = board.getCastleRooks(PieceColor.BLACK);
        for (Rook i : white) {
            System.out.println(i);
        }
        for (Rook i : black) {
            System.out.println(i);
        } */
    }
}
