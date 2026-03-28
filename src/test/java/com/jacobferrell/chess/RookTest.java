package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RookTest {

    @Test
    public void testRookMovesAlongEntireRankAndFileOnClearBoard() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);

        Set<Position> moves = rook.generatePossiblePositions();

        // Full rank (row 3)
        for (int x = 0; x < 8; x++) {
            if (x != 3) {
                assertTrue(moves.contains(new Position(x, 3)), "Rook should reach (" + x + ",3)");
            }
        }
        // Full file (col 3)
        for (int y = 0; y < 8; y++) {
            if (y != 3) {
                assertTrue(moves.contains(new Position(3, y)), "Rook should reach (3," + y + ")");
            }
        }
        assertEquals(14, moves.size(), "Rook in center should have 14 moves");
    }

    @Test
    public void testRookBlockedByFriendlyPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(3, 1), board)); // above
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(5, 3), board)); // right

        Set<Position> moves = rook.generatePossiblePositions();

        assertTrue(moves.contains(new Position(3, 2)), "Rook can reach square before ally");
        assertFalse(moves.contains(new Position(3, 1)), "Rook cannot land on friendly piece");
        assertFalse(moves.contains(new Position(3, 0)), "Rook cannot pass through friendly piece");
        assertTrue(moves.contains(new Position(4, 3)), "Rook can reach square before ally");
        assertFalse(moves.contains(new Position(5, 3)), "Rook cannot land on friendly piece");
        assertFalse(moves.contains(new Position(6, 3)), "Rook cannot pass through friendly piece");
    }

    @Test
    public void testRookCapturesEnemyButCannotPassThrough() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(3, 1), board)); // enemy above
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 3), board)); // enemy right

        Set<Position> moves = rook.generatePossiblePositions();

        assertTrue(moves.contains(new Position(3, 2)), "Rook can reach square before enemy");
        assertTrue(moves.contains(new Position(3, 1)), "Rook can capture enemy piece");
        assertFalse(moves.contains(new Position(3, 0)), "Rook cannot pass through enemy piece");
        assertTrue(moves.contains(new Position(4, 3)), "Rook can reach square before enemy");
        assertTrue(moves.contains(new Position(5, 3)), "Rook can capture enemy piece");
        assertFalse(moves.contains(new Position(6, 3)), "Rook cannot pass through enemy piece");
    }

    @Test
    public void testRookBlockedByPawnsInStartingPosition() {
        ChessBoard board = new ChessBoard();
        // White rook at (0,7) — blocked by pawn at (0,6) directly in front
        Rook rook = (Rook) board.getPieceAtPosition(0, 7).get();

        Set<Position> moves = rook.generatePossiblePositions();

        assertTrue(moves.isEmpty(), "Rook in starting position should be completely blocked");
    }

    @Test
    public void testRookWithEnemiesSurrounding() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        // Place enemies 2 squares away in all directions
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(1, 3), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(5, 3), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(3, 1), board));
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(3, 5), board));

        Set<Position> moves = rook.generatePossiblePositions();

        // Should reach the intermediate squares and capture each enemy
        assertTrue(moves.contains(new Position(2, 3)));
        assertTrue(moves.contains(new Position(1, 3))); // capture
        assertFalse(moves.contains(new Position(0, 3))); // cannot pass
        assertTrue(moves.contains(new Position(4, 3)));
        assertTrue(moves.contains(new Position(5, 3))); // capture
        assertFalse(moves.contains(new Position(6, 3))); // cannot pass
        assertTrue(moves.contains(new Position(3, 2)));
        assertTrue(moves.contains(new Position(3, 1))); // capture
        assertFalse(moves.contains(new Position(3, 0))); // cannot pass
        assertTrue(moves.contains(new Position(3, 4)));
        assertTrue(moves.contains(new Position(3, 5))); // capture
        assertFalse(moves.contains(new Position(3, 6))); // cannot pass
        assertEquals(8, moves.size());
    }
}
