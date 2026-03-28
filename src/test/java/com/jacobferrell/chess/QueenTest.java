package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class QueenTest {

    @Test
    public void testQueenMovesInAllEightDirections() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Queen queen = new Queen(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(queen);

        Set<Position> moves = queen.generatePossiblePositions();

        // Horizontal right
        assertTrue(moves.contains(new Position(4, 3)));
        assertTrue(moves.contains(new Position(5, 3)));
        // Horizontal left
        assertTrue(moves.contains(new Position(2, 3)));
        assertTrue(moves.contains(new Position(0, 3)));
        // Vertical up
        assertTrue(moves.contains(new Position(3, 2)));
        assertTrue(moves.contains(new Position(3, 0)));
        // Vertical down
        assertTrue(moves.contains(new Position(3, 4)));
        // Diagonal NW
        assertTrue(moves.contains(new Position(2, 2)));
        assertTrue(moves.contains(new Position(0, 0)));
        // Diagonal NE
        assertTrue(moves.contains(new Position(4, 2)));
        assertTrue(moves.contains(new Position(5, 1)));
        // Diagonal SW
        assertTrue(moves.contains(new Position(2, 4)));
        assertTrue(moves.contains(new Position(1, 5)));
        // Diagonal SE
        assertTrue(moves.contains(new Position(4, 4)));
        assertTrue(moves.contains(new Position(5, 5)));
    }

    @Test
    public void testQueenBlockedByFriendlyPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Queen queen = new Queen(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(queen);
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 3), board)); // horizontal right
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 1), board)); // NE diagonal

        Set<Position> moves = queen.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 3)));
        assertFalse(moves.contains(new Position(5, 3)), "Queen cannot land on friendly piece");
        assertFalse(moves.contains(new Position(6, 3)), "Queen cannot pass through friendly piece");
        assertTrue(moves.contains(new Position(4, 2)));
        assertFalse(moves.contains(new Position(5, 1)), "Queen cannot land on friendly piece");
        assertFalse(moves.contains(new Position(6, 0)), "Queen cannot pass through friendly piece");
    }

    @Test
    public void testQueenCapturesEnemyButCannotPassThrough() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Queen queen = new Queen(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(queen);
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 3), board)); // enemy horizontal right
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(1, 1), board)); // enemy NW diagonal

        Set<Position> moves = queen.generatePossiblePositions();

        assertTrue(moves.contains(new Position(4, 3)));
        assertTrue(moves.contains(new Position(5, 3)), "Queen can capture enemy");
        assertFalse(moves.contains(new Position(6, 3)), "Queen cannot pass through enemy");
        assertTrue(moves.contains(new Position(2, 2)));
        assertTrue(moves.contains(new Position(1, 1)), "Queen can capture enemy on diagonal");
        assertFalse(moves.contains(new Position(0, 0)), "Queen cannot pass through enemy");
    }

    @Test
    public void testQueenInStartingPositionIsCompletelyBlocked() {
        ChessBoard board = new ChessBoard();
        // White queen starts at (3,7), surrounded by pawns and pieces
        Queen queen = (Queen) board.getPieceAtPosition(3, 7).get();

        Set<Position> moves = queen.generatePossiblePositions();

        assertTrue(moves.isEmpty(), "Queen in starting position should be completely blocked");
    }

    @Test
    public void testQueenOnEmptyBoardFromCorner() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 6), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(5, 0), board));
        Queen queen = new Queen(PieceColor.WHITE, new Position(0, 7), board);
        board.addPiece(queen);

        Set<Position> moves = queen.generatePossiblePositions();

        // File (column 0): 7 moves up
        for (int y = 0; y < 7; y++) {
            assertTrue(moves.contains(new Position(0, y)), "Queen should reach (0," + y + ")");
        }
        // Rank (row 7): 6 moves right (excluding king at (7,6) area)
        assertTrue(moves.contains(new Position(1, 7)));
        assertTrue(moves.contains(new Position(2, 7)));
        // Diagonal NE from (0,7): (1,6), (2,5), ..., (6,1), (7,0)
        assertTrue(moves.contains(new Position(1, 6)));
        assertTrue(moves.contains(new Position(7, 0)));
    }
}
