package com.jacobferrell.chess;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class KingTest {
    private ChessBoard board = new ChessBoard();

    @Test
    public void testKing() {
        King king = new King(PieceColor.WHITE, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, king);
        Set<Move> possibleMoves = king.generatePossibleMoves();
        System.out.println(board);
        for (Move move : possibleMoves) {
            System.out.println(move.position.y + "," + move.position.x);
        }
    }

    @Test
    public void testWhiteKingCheck() {
        board.clearBoard();
        King king = new King(PieceColor.WHITE, new Position(3, 3), board);
        Rook rook = new Rook(PieceColor.BLACK, new Position(3, 5), board);
        board.setPieceAtPosition(3, 3, king);
        board.setPieceAtPosition(3, 5, rook);
        assertTrue(king.isInCheck());
    }

    @Test
    public void testKingsNotInCheckAtStart() {
        ChessBoard board = new ChessBoard();
        King blackKing = (King) board.getPieceAtPosition(4, 0);
        King whiteKing = (King) board.getPieceAtPosition(4, 7);
        assertFalse(
                blackKing.isInCheck() && whiteKing.isInCheck());
    }

    @Test
    public void testBlackCheckMate() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King blackKing = new King(PieceColor.BLACK, new Position(0, 0), board);
        King whiteKing = new King(PieceColor.WHITE, new Position(7, 7), board);
        board.setPieceAtPosition(7, 7, whiteKing);
        board.setPieceAtPosition(0, 2, new Rook(PieceColor.WHITE, new Position(1, 4), board));
        board.setPieceAtPosition(0, 1, new Queen(PieceColor.WHITE, new Position(5, 7), board));
        board.setPieceAtPosition(0, 0, blackKing);
        System.out.println(board);
        assertTrue(blackKing.isInCheckMate());
        assertFalse(whiteKing.isInCheckMate());
        board = new ChessBoard();
        assertFalse(
                board.getPlayerKing(PieceColor.BLACK).isInCheckMate()
                        && board.getPlayerKing(PieceColor.WHITE).isInCheckMate());
    }

    @Test
    public void testWhiteCheckMate() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King whiteKing = new King(PieceColor.WHITE, new Position(0, 0), board);
        King blackKing = new King(PieceColor.BLACK, new Position(7, 7), board);
        board.setPieceAtPosition(7, 7, blackKing);
        board.setPieceAtPosition(0, 2, new Rook(PieceColor.BLACK, new Position(1, 4), board));
        board.setPieceAtPosition(0, 1, new Queen(PieceColor.BLACK, new Position(5, 7), board));
        board.setPieceAtPosition(0, 0, whiteKing);
        System.out.println(board);
        assertTrue(whiteKing.isInCheckMate());
        assertFalse(blackKing.isInCheckMate());
        board = new ChessBoard();
        assertFalse(
                board.getPlayerKing(PieceColor.BLACK).isInCheckMate()
                        && board.getPlayerKing(PieceColor.WHITE).isInCheckMate());
    }
}
