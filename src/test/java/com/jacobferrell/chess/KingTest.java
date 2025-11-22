package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

public class KingTest {
    private ChessBoard board = new ChessBoard();
/*
    @Test
    public void testKing() {
        King king = new King(PieceColor.WHITE, new Position(3, 3), board);
        board.clearBoard();
        board.placePieceAndCapture(new Position(3, 3), king);
        Set<Move> possibleMoves = king.generatePossiblePositions();
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
        board.placePieceAndCapture(new Position(3, 3), king);
        board.placePieceAndCapture(new Position(3, 5), rook);
        assertTrue(king.isInCheck());
    }

    @Test
    public void testKingsNotInCheckAtStart() {
        ChessBoard board = new ChessBoard();
        King blackKing = (King) board.getPieceAtPosition(new Position(4, 0));
        King whiteKing = (King) board.getPieceAtPosition(new Position(4, 7));
        assertFalse(
                blackKing.isInCheck() && whiteKing.isInCheck());
    }

    @Test
    public void testBlackCheckMate() {
        ChessBoard board = new ChessBoard();
        board.clearBoard();
        King blackKing = new King(PieceColor.BLACK, new Position(0, 0), board);
        King whiteKing = new King(PieceColor.WHITE, new Position(7, 7), board);
        board.placePieceAndCapture(new Position(7, 7), whiteKing);
        board.placePieceAndCapture(new Position(0, 2), new Rook(PieceColor.WHITE, new Position(1, 4), board));
        board.placePieceAndCapture(new Position(0, 1), new Queen(PieceColor.WHITE, new Position(5, 7), board));
        board.placePieceAndCapture(new Position(0, 0), blackKing);
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
        board.placePieceAndCapture(new Position(7, 7), blackKing);
        board.placePieceAndCapture(new Position(0, 2), new Rook(PieceColor.BLACK, new Position(1, 4), board));
        board.placePieceAndCapture(new Position(0, 1), new Queen(PieceColor.BLACK, new Position(5, 7), board));
        board.placePieceAndCapture(new Position(0, 0), whiteKing);
        System.out.println(board);
        assertTrue(whiteKing.isInCheckMate());
        assertFalse(blackKing.isInCheckMate());
        board = new ChessBoard();
        assertFalse(
                board.getPlayerKing(PieceColor.BLACK).isInCheckMate()
                        && board.getPlayerKing(PieceColor.WHITE).isInCheckMate());
    }*/
}
