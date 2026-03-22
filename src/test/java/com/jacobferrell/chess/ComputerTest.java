package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.jacobferrell.chess.service.game.computer.ComputerService.selectMove;
import static org.junit.jupiter.api.Assertions.*;

public class ComputerTest {

    @Test
    public void testMoveMap() {
        ChessBoard board = ChessBoardTestUtils.withKingsOnly();
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(0, 1), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(1, 2), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(0, 2), board));

        Set<Move> allPossibleComputerMoves =
                board.getAllPossibleMoves(PieceColor.BLACK);

        Optional<Move> move = selectMove(allPossibleComputerMoves);
        assertTrue(move.isPresent());
    }

    @Test
    public void testSelectMoveReturnsEmptyWhenCheckmated() {
        // Black king in corner (7,0), both escape squares covered by white rooks
        // Rook at (0,0) delivers check along rank 0
        // Rook at (0,1) covers rank 1, blocking all king escapes
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 0), board));
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 1), board));

        Set<Move> allPossibleComputerMoves =
                board.getAllPossibleMoves(PieceColor.BLACK);

        // Pseudo-legal moves exist (king can geometrically move) but all are illegal
        assertFalse(allPossibleComputerMoves.isEmpty(), "Should have pseudo-legal moves");

        Optional<Move> move = selectMove(allPossibleComputerMoves);
        assertTrue(move.isEmpty(), "selectMove should return empty when all moves are illegal (checkmate)");
    }

    @Test
    public void testSelectMoveReturnsEmptyWhenStalemated() {
        // Black king at (7,0), all surrounding squares attacked but king not in check
        // Rook at (0,1) covers rank 1; rook at (1,7) covers file 1 from a safe distance
        // White king at (5,2) covers (6,1)
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(5, 2), board));
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 1), board)); // covers rank 1
        board.addPiece(new Rook(PieceColor.WHITE, new Position(6, 7), board)); // covers file 6

        Set<Move> allPossibleComputerMoves =
                board.getAllPossibleMoves(PieceColor.BLACK);

        Optional<Move> move = selectMove(allPossibleComputerMoves);
        assertTrue(move.isEmpty(), "selectMove should return empty when stalemated");
    }
}
