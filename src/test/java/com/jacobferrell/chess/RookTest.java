package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class RookTest {
    @Test
    public void testRook() {
        ChessBoard board = new ChessBoard();
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.clear();
        board.placePieceAndCapture(new Position(3, 3), rook);
        board.placePieceAndCapture(new Position(1, 3), new Pawn(PieceColor.BLACK, new Position(1, 3), board));
        board.placePieceAndCapture(new Position(5, 3), new Pawn(PieceColor.BLACK, new Position(5, 3), board));
        board.placePieceAndCapture(new Position(3, 1), new Pawn(PieceColor.BLACK, new Position(3, 1), board));
        board.placePieceAndCapture(new Position(3, 5), new Pawn(PieceColor.BLACK, new Position(3, 5), board));
        Set<Position> possibleMoves = rook.generatePossiblePositions();
        System.out.println(board);
        for (Position move : possibleMoves) {
            System.out.println(move.y() + "," + move.x());
        }
    }
/*
    @Test
        public void testCloneRook() {
            ChessBoard board = new ChessBoard();
            ChessPiece rook = board.getPieceAtPosition(new Position(0, 0));
            ChessPiece rookClone = rook.getClone(board);
            board.placePieceAndCapture(new Position(0, 2), rookClone);
            System.out.println(board);
        }*/
}