package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PawnTest {

    // Coordinate system: x=file (0=a..7=h), y=rank (0=rank8..7=rank1)
    // White pawns start at y=6, move UP (y decreases)
    // Black pawns start at y=1, move DOWN (y increases)

    @Test
    public void testWhitePawnInitialPositionCanMoveTwoSquares() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 5)), "White pawn should be able to move 1 square forward");
        assertTrue(moves.contains(new Position(4, 4)), "White pawn should be able to move 2 squares on first move");
        assertEquals(2, moves.size());
    }

    @Test
    public void testWhitePawnAfterMovingCanOnlyMoveOneSquare() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);

        // Simulate pawn having moved already
        pawn.setHasMoved(true);

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 5)));
        assertFalse(moves.contains(new Position(4, 4)), "Pawn that has moved cannot move 2 squares");
        assertEquals(1, moves.size());
    }

    @Test
    public void testWhitePawnBlockedByAllyCannotMoveForward() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);
        // Block with friendly piece directly ahead
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(4, 5), board));

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.isEmpty(), "Pawn blocked by ally should have no vertical moves");
    }

    @Test
    public void testWhitePawnBlockedByEnemyCannotMoveForward() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);
        // Block with enemy piece directly ahead
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(4, 5), board));

        Set<Position> moves = pawn.generatePossiblePositions();

        assertFalse(moves.contains(new Position(4, 5)), "Pawn cannot capture forward");
        assertFalse(moves.contains(new Position(4, 4)), "Pawn cannot skip over blocking piece");
    }

    @Test
    public void testWhitePawnCapturesDiagonally() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(3, 5), board)); // left diagonal
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 5), board)); // right diagonal

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.contains(new Position(3, 5)), "White pawn can capture left-diagonally");
        assertTrue(moves.contains(new Position(5, 5)), "White pawn can capture right-diagonally");
        assertTrue(moves.contains(new Position(4, 5)), "White pawn can still advance");
    }

    @Test
    public void testWhitePawnCannotCaptureAllyDiagonally() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(3, 5), board)); // friendly on diagonal
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 5), board)); // friendly on diagonal

        Set<Position> moves = pawn.generatePossiblePositions();

        assertFalse(moves.contains(new Position(3, 5)), "Pawn cannot capture friendly piece");
        assertFalse(moves.contains(new Position(5, 5)), "Pawn cannot capture friendly piece");
    }

    @Test
    public void testBlackPawnMovesDownTheBoard() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.BLACK, new Position(4, 1), board);
        board.addPiece(pawn);

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 2)), "Black pawn moves toward higher y (rank decrease)");
        assertTrue(moves.contains(new Position(4, 3)), "Black pawn can move 2 squares on first move");
    }

    @Test
    public void testBlackPawnCapturesDiagonally() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.BLACK, new Position(4, 3), board);
        board.addPiece(pawn);
        pawn.setHasMoved(true);
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(3, 4), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 4), board));

        Set<Position> moves = pawn.generatePossiblePositions();

        assertTrue(moves.contains(new Position(3, 4)), "Black pawn can capture diagonally left");
        assertTrue(moves.contains(new Position(5, 4)), "Black pawn can capture diagonally right");
    }

    @Test
    public void testWhitePawnNoDiagonalCaptureOnEmptySquare() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);

        Set<Position> moves = pawn.generatePossiblePositions();

        assertFalse(moves.contains(new Position(3, 5)), "Pawn cannot move diagonally to empty square");
        assertFalse(moves.contains(new Position(5, 5)), "Pawn cannot move diagonally to empty square");
    }
}
