package com.jacobferrell.chess;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;

import java.util.*;

public class BishopTest {
    @Test
    public void testEnemiesForWhite() {
        ChessBoard board = new ChessBoard();
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, bishop);
        board.setPieceAtPosition(1, 1, new Pawn(PieceColor.BLACK, new Position(1, 1), board));
        board.setPieceAtPosition(1, 5, new Pawn(PieceColor.BLACK, new Position(5, 1), board));
        board.setPieceAtPosition(5, 1, new Pawn(PieceColor.BLACK, new Position(1, 5), board));
        board.setPieceAtPosition(5, 5, new Pawn(PieceColor.BLACK, new Position(5, 5), board));
        Set<Position> possibleMoves = bishop.generatePossibleMoves();
        for (Position pos : possibleMoves) {
            System.out.println("new Position(" + pos.getY() + ", " + pos.getX() + "),");
        }
        Set<Position> expectedPositions = new HashSet<>();

        expectedPositions.addAll(Arrays.asList(
                new Position(2, 2),
                new Position(1, 1),
                new Position(2, 4),
                new Position(1, 5),
                new Position(4, 2),
                new Position(5, 1),
                new Position(4, 4),
                new Position(5, 5)));
        assertTrue(expectedPositions.containsAll(possibleMoves) && possibleMoves.containsAll(expectedPositions));

    }

    @Test
    public void testAlliesForWhite() {
        ChessBoard board = new ChessBoard();
        Bishop bishop = new Bishop(PieceColor.WHITE, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, bishop);
        board.setPieceAtPosition(1, 1, new Pawn(PieceColor.WHITE, new Position(1, 1), board));
        board.setPieceAtPosition(1, 5, new Pawn(PieceColor.WHITE, new Position(5, 1), board));
        board.setPieceAtPosition(5, 1, new Pawn(PieceColor.WHITE, new Position(1, 5), board));
        board.setPieceAtPosition(5, 5, new Pawn(PieceColor.WHITE, new Position(5, 5), board));
        Set<Position> possibleMoves = bishop.generatePossibleMoves();
        Set<Position> expectedPositions = new HashSet<>();
        expectedPositions.addAll(Arrays.asList(
                new Position(2, 2),
                new Position(2, 4),
                new Position(4, 2),
                new Position(4, 4)));
        assertTrue(expectedPositions.size() == possibleMoves.size() && expectedPositions.containsAll(possibleMoves)
                && possibleMoves.containsAll(expectedPositions));

    }

    @Test
    public void testAlliesForBlack() {
        ChessBoard board = new ChessBoard();
        Bishop bishop = new Bishop(PieceColor.BLACK, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, bishop);
        board.setPieceAtPosition(1, 1, new Pawn(PieceColor.BLACK, new Position(1, 1), board));
        board.setPieceAtPosition(1, 5, new Pawn(PieceColor.BLACK, new Position(5, 1), board));
        board.setPieceAtPosition(5, 1, new Pawn(PieceColor.BLACK, new Position(1, 5), board));
        board.setPieceAtPosition(5, 5, new Pawn(PieceColor.BLACK, new Position(5, 5), board));
        Set<Position> possibleMoves = bishop.generatePossibleMoves();
        System.out.println(board);
        for (Position pos : possibleMoves) {
            System.out.println(pos.getY() + "," + pos.getX());
        }
    }

    @Test
    public void testEnemiesForBlack() {
        ChessBoard board = new ChessBoard();
        Bishop bishop = new Bishop(PieceColor.BLACK, new Position(3, 3), board);
        board.clearBoard();
        board.setPieceAtPosition(3, 3, bishop);
        board.setPieceAtPosition(1, 1, new Pawn(PieceColor.WHITE, new Position(1, 1), board));
        board.setPieceAtPosition(1, 5, new Pawn(PieceColor.WHITE, new Position(5, 1), board));
        board.setPieceAtPosition(5, 1, new Pawn(PieceColor.WHITE, new Position(1, 5), board));
        board.setPieceAtPosition(5, 5, new Pawn(PieceColor.WHITE, new Position(5, 5), board));
        Set<Position> possibleMoves = bishop.generatePossibleMoves();
        System.out.println(board);
        for (Position pos : possibleMoves) {
            System.out.println(pos.getY() + "," + pos.getX());
        }
    }
}