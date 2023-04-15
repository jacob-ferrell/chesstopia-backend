package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;
import java.util.Set;


public class Bishop extends ChessPiece {
    private char SYMBOL;
    public String name = "BISHOP";

    public Bishop(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♗' : '♝';
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        Bishop clone = new Bishop(color, new Position(xPosition, yPosition), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

    @Override
    public Set<Position> generatePossibleMoves() {
        return getDiagonalMoves();
    }

}
