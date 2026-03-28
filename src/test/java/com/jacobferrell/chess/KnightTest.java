package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class KnightTest {

    @Test
    public void testKnightHasEightMovesFromCenter() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(knight);

        Set<Position> moves = knight.generatePossiblePositions();

        assertEquals(8, moves.size(), "Knight in center should have 8 L-shape moves");
        assertTrue(moves.contains(new Position(1, 2)));
        assertTrue(moves.contains(new Position(1, 4)));
        assertTrue(moves.contains(new Position(2, 1)));
        assertTrue(moves.contains(new Position(2, 5)));
        assertTrue(moves.contains(new Position(4, 1)));
        assertTrue(moves.contains(new Position(4, 5)));
        assertTrue(moves.contains(new Position(5, 2)));
        assertTrue(moves.contains(new Position(5, 4)));
    }

    @Test
    public void testKnightAtCornerHasTwoMoves() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(5, 0), board));
        Knight knight = new Knight(PieceColor.WHITE, new Position(0, 0), board);
        board.addPiece(knight);

        Set<Position> moves = knight.generatePossiblePositions();

        assertEquals(2, moves.size(), "Knight in corner should have exactly 2 moves");
        assertTrue(moves.contains(new Position(1, 2)));
        assertTrue(moves.contains(new Position(2, 1)));
    }

    @Test
    public void testKnightAtEdgeHasLimitedMoves() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Knight knight = new Knight(PieceColor.WHITE, new Position(0, 3), board);
        board.addPiece(knight);

        Set<Position> moves = knight.generatePossiblePositions();

        assertEquals(4, moves.size(), "Knight on edge should have 4 moves");
        assertTrue(moves.contains(new Position(1, 1)));
        assertTrue(moves.contains(new Position(2, 2)));
        assertTrue(moves.contains(new Position(1, 5)));
        assertTrue(moves.contains(new Position(2, 4)));
    }

    @Test
    public void testKnightCanJumpOverPieces() {
        // Use the starting board — knight at (1,7) is surrounded by pieces but can still jump
        ChessBoard board = new ChessBoard();
        Knight knight = (Knight) board.getPieceAtPosition(new Position(1, 7)).get();

        Set<Position> moves = knight.generatePossiblePositions();

        // Starting knight can only jump to (0,5) or (2,5) — over pawns and rook
        assertTrue(moves.contains(new Position(0, 5)), "Knight should jump over pawn to (0,5)");
        assertTrue(moves.contains(new Position(2, 5)), "Knight should jump over pawn to (2,5)");
        assertEquals(2, moves.size(), "Knight on starting edge should have exactly 2 jump moves");
    }

    @Test
    public void testKnightCannotLandOnFriendlyPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(knight);
        // Place friendly pieces on some L-shape destinations
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(1, 2), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 4), board));

        Set<Position> moves = knight.generatePossiblePositions();

        assertFalse(moves.contains(new Position(1, 2)), "Knight cannot land on friendly piece");
        assertFalse(moves.contains(new Position(5, 4)), "Knight cannot land on friendly piece");
        assertEquals(6, moves.size(), "Knight should have 6 moves with 2 blocked by allies");
    }

    @Test
    public void testKnightCanCaptureEnemyPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Knight knight = new Knight(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(knight);
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(1, 2), board));

        Set<Position> moves = knight.generatePossiblePositions();

        assertTrue(moves.contains(new Position(1, 2)), "Knight can capture enemy at L-shape destination");
    }
}
