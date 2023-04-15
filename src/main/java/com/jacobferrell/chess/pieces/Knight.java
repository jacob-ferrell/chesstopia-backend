package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public class Knight extends ChessPiece {
    private char SYMBOL;
    public String name = "KNIGHT";

    public Knight(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♘' : '♞';
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        Knight clone = new Knight(color, new Position(xPosition, yPosition), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Position> generatePossibleMoves() {
        int[] longValues = { 2, -2 };
        int[] shortValues = { 1, -1 };
        Set<Position> possibleMoves = new HashSet<>();

        possibleMoves.addAll(getKnightMoves(longValues, shortValues));
        possibleMoves.addAll(getKnightMoves(shortValues, longValues));

        return possibleMoves;
    }

    private Set<Position> getKnightMoves(int[] vertical, int[] horizontal) {
        Set<Position> possibleMoves = new HashSet<>();
        int x, y;
        for (int i = 0; i < 2; i++) {
            x = getXPosition() + horizontal[i];
            for (int j = 0; j < 2; j++) {
                y = getYPosition() + vertical[j];
                if (isValidMove(x, y)) {
                    possibleMoves.add(new Position(x, y));
                }
            }
        }
        return possibleMoves;

    }
}
