package com.jacobferrell.chess;

import static org.junit.jupiter.api.Assertions.*;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ChessBoardTest {

    @Test
    public void testHasBothKings() {
        // Standard board has both kings
        ChessBoard board = new ChessBoard();
        assertTrue(board.hasBothKings());

        // Cleared board has no kings
        board.clear();
        assertFalse(board.hasBothKings());

        // Add both kings manually
        King whiteKing = new King(PieceColor.WHITE, new Position(0, 4), board);
        King blackKing = new King(PieceColor.BLACK, new Position(7, 4), board);
        board.addPiece(whiteKing);
        board.addPiece(blackKing);
        assertTrue(board.hasBothKings());

        // Remove one king — should return false
        board.removePiece(blackKing);
        assertFalse(board.hasBothKings());
    }

    @Test
    public void testClearRemovesAllPieces() {
        ChessBoard board = new ChessBoard();
        board.clear();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                assertFalse(board.getPieceAtPosition(x, y).isPresent(),
                        "Board should be empty at (" + x + "," + y + ") after clear()");
            }
        }
        assertFalse(board.hasBothKings(), "No kings should exist after clear()");
    }

    @Test
    public void testIsStalemate_onlyTwoKingsRemain() {
        ChessBoard board = ChessBoardTestUtils.withKingsOnly();
        assertTrue(board.isStalemate(PieceColor.WHITE), "Two kings only = stalemate for white");
        assertTrue(board.isStalemate(PieceColor.BLACK), "Two kings only = stalemate for black");
    }

    @Test
    public void testIsNotStalemate_multiplePiecesRemain() {
        ChessBoard board = new ChessBoard();
        assertFalse(board.isStalemate(PieceColor.WHITE));
        assertFalse(board.isStalemate(PieceColor.BLACK));
    }

    @Test
    public void testGetAllPossibleMovesReturnsMovesAtStart() {
        ChessBoard board = new ChessBoard();
        Set<Move> whiteMoves = board.getAllPossibleMoves(PieceColor.WHITE);
        Set<Move> blackMoves = board.getAllPossibleMoves(PieceColor.BLACK);
        // At start only pawns (2 moves each x8) and knights (2 moves each x2) can move
        assertFalse(whiteMoves.isEmpty(), "White should have moves at start");
        assertFalse(blackMoves.isEmpty(), "Black should have moves at start");
        assertEquals(20, whiteMoves.size(), "White should have 20 pseudo-legal moves at start");
        assertEquals(20, blackMoves.size(), "Black should have 20 pseudo-legal moves at start");
    }

    @Test
    public void testAddAndGetPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(3, 5), board);
        board.addPiece(pawn);

        assertTrue(board.getPieceAtPosition(3, 5).isPresent());
        assertEquals(pawn, board.getPieceAtPosition(3, 5).get());
    }

    @Test
    public void testRemovePieceByPosition() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(3, 5), board);
        board.addPiece(pawn);

        board.removePiece(new Position(3, 5));

        assertFalse(board.getPieceAtPosition(3, 5).isPresent(), "Piece should be removed");
        assertFalse(board.getPiecesByColor(PieceColor.WHITE).contains(pawn));
    }

    @Test
    public void testPlacePieceAndCaptureMovesAndCapturesEnemy() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(0, 3), board);
        board.addPiece(rook);
        Pawn enemy = new Pawn(PieceColor.BLACK, new Position(5, 3), board);
        board.addPiece(enemy);

        var captured = board.placePieceAndCapture(new Position(5, 3), rook);

        assertTrue(captured.isPresent(), "Enemy should be captured");
        assertEquals(enemy, captured.get());
        assertEquals(new Position(5, 3), rook.getPosition());
        assertFalse(board.getPiecesByColor(PieceColor.BLACK).contains(enemy));
    }

    @Test
    public void testPlacePieceAndCaptureDoesNotCaptureAlly() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(0, 3), board);
        board.addPiece(rook);
        Pawn ally = new Pawn(PieceColor.WHITE, new Position(5, 3), board);
        board.addPiece(ally);

        var captured = board.placePieceAndCapture(new Position(5, 3), rook);

        assertTrue(captured.isEmpty(), "Ally should not be captured");
        // Rook should not have moved
        assertEquals(new Position(0, 3), rook.getPosition());
    }

    @Test
    public void testGetPlayerKingReturnsCorrectKing() {
        ChessBoard board = new ChessBoard();
        King whiteKing = board.getPlayerKing(PieceColor.WHITE);
        King blackKing = board.getPlayerKing(PieceColor.BLACK);

        assertNotNull(whiteKing);
        assertNotNull(blackKing);
        assertEquals(PieceColor.WHITE, whiteKing.getColor());
        assertEquals(PieceColor.BLACK, blackKing.getColor());
        assertEquals(new Position(4, 7), whiteKing.getPosition());
        assertEquals(new Position(4, 0), blackKing.getPosition());
    }

    @Test
    public void testIsPositionOccupied() {
        ChessBoard board = new ChessBoard();
        // White pawns occupy row 6
        assertTrue(board.isPositionOccupied(new Position(0, 6)));
        // Row 3 is empty at start
        assertFalse(board.isPositionOccupied(new Position(3, 3)));
    }

    @Test
    public void testCreateNewPieceFactory() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));

        ChessPiece rook = board.createNewPiece(PieceType.ROOK, new Position(0, 0), PieceColor.WHITE);
        ChessPiece queen = board.createNewPiece(PieceType.QUEEN, new Position(1, 0), PieceColor.BLACK);
        ChessPiece bishop = board.createNewPiece(PieceType.BISHOP, new Position(2, 0), PieceColor.WHITE);
        ChessPiece knight = board.createNewPiece(PieceType.KNIGHT, new Position(3, 0), PieceColor.BLACK);
        ChessPiece pawn = board.createNewPiece(PieceType.PAWN, new Position(4, 0), PieceColor.WHITE);

        assertInstanceOf(Rook.class, rook);
        assertInstanceOf(Queen.class, queen);
        assertInstanceOf(Bishop.class, bishop);
        assertInstanceOf(Knight.class, knight);
        assertInstanceOf(Pawn.class, pawn);
    }
}
