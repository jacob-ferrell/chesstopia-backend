package com.jacobferrell.chess;

import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;
import java.util.Set;

public class RookTest {
    @Test
    public void testRook() {
        ChessBoard board = new ChessBoard();
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, rook);
        board.setPieceAtPosition(1, 3, new Pawn(PieceColor.BLACK, new Position(1, 3), board));
        board.setPieceAtPosition(5, 3, new Pawn(PieceColor.BLACK, new Position(5, 3), board));
        board.setPieceAtPosition(3, 1, new Pawn(PieceColor.BLACK, new Position(3, 1), board));
        board.setPieceAtPosition(3, 5, new Pawn(PieceColor.BLACK, new Position(3, 5), board));
        Set<Move> possibleMoves = rook.generatePossibleMoves();
        System.out.println(board);
        for (Move move : possibleMoves) {
            System.out.println(move.position.y + "," + move.position.x);
        }
    }

    @Test
        public void testCloneRook() {
            ChessBoard board = new ChessBoard();
            ChessPiece rook = board.getPieceAtPosition(0, 0);
            ChessPiece rookClone = rook.getClone(board);
            board.setPieceAtPosition(0, 2, rookClone);
            System.out.println(board);
        }
}