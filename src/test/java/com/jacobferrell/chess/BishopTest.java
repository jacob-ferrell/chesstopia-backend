package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BishopTest {

    @Test
    public void testBishopReachesDiagonalsOnClearBoard() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(bishop);

        Set<Position> moves = bishop.generatePossiblePositions();

        // NW diagonal
        assertTrue(moves.contains(new Position(2, 2)));
        assertTrue(moves.contains(new Position(1, 1)));
        assertTrue(moves.contains(new Position(0, 0)));
        // NE diagonal
        assertTrue(moves.contains(new Position(4, 2)));
        assertTrue(moves.contains(new Position(5, 1)));
        assertTrue(moves.contains(new Position(6, 0)));
        // SW diagonal
        assertTrue(moves.contains(new Position(2, 4)));
        assertTrue(moves.contains(new Position(1, 5)));
        assertTrue(moves.contains(new Position(0, 6)));
        // SE diagonal
        assertTrue(moves.contains(new Position(4, 4)));
        assertTrue(moves.contains(new Position(5, 5)));
        // (6,6) is not valid because white king-adjacent restrictions don't apply here
        // and (7,7) is white king — but bishop should stop at ally
    }

    @Test
    public void testBishopBlockedByFriendlyPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(bishop);
        // Place a friendly pawn on the NE diagonal
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 1), board));

        Set<Position> moves = bishop.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 2)), "Bishop can reach square before ally");
        assertFalse(moves.contains(new Position(5, 1)), "Bishop cannot land on friendly piece");
        assertFalse(moves.contains(new Position(6, 0)), "Bishop cannot pass through friendly piece");
    }

    @Test
    public void testBishopCapturesEnemyButCannotPassThrough() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(bishop);
        // Place an enemy pawn on the NE diagonal
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 1), board));

        Set<Position> moves = bishop.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 2)), "Bishop can reach square before enemy");
        assertTrue(moves.contains(new Position(5, 1)), "Bishop can capture enemy piece");
        assertFalse(moves.contains(new Position(6, 0)), "Bishop cannot move past enemy piece");
    }

    @Test
    public void testBishopInCornerHasLimitedMoves() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(5, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(5, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(0, 0), board);
        board.addPiece(bishop);

        Set<Position> moves = bishop.generatePossiblePositions();

        // From (0,0), only the SE diagonal is valid: (1,1), (2,2), ..., (7,7)
        assertEquals(7, moves.size());
        assertTrue(moves.contains(new Position(1, 1)));
        assertTrue(moves.contains(new Position(7, 7)));
    }

    @Test
    public void testBishopSurroundedByAlliesHasNoMoves() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(bishop);
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(2, 2), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(4, 2), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(2, 4), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(4, 4), board));

        Set<Position> moves = bishop.generatePossiblePositions();

        assertTrue(moves.isEmpty(), "Bishop surrounded by allies should have no moves");
    }

    @Test
    public void testBishopCanCaptureInAllFourDiagonalDirections() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(bishop);
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(1, 1), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 1), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(1, 5), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 5), board));

        Set<Position> moves = bishop.generatePossiblePositions();

        assertTrue(moves.contains(new Position(1, 1)), "Bishop captures NW enemy");
        assertTrue(moves.contains(new Position(5, 1)), "Bishop captures NE enemy");
        assertTrue(moves.contains(new Position(1, 5)), "Bishop captures SW enemy");
        assertTrue(moves.contains(new Position(5, 5)), "Bishop captures SE enemy");
        assertTrue(moves.contains(new Position(2, 2)));
        assertTrue(moves.contains(new Position(4, 2)));
        assertTrue(moves.contains(new Position(2, 4)));
        assertTrue(moves.contains(new Position(4, 4)));
        assertEquals(8, moves.size(), "Bishop should have exactly 8 moves (captures + intermediate squares)");
    }
}
