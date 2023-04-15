package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public class Rook extends ChessPiece {
    private char SYMBOL;
    public String name = "ROOK";

    public Rook(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♖' : '♜';
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard clonedBoard) {
        Rook clone = new Rook(this.color, new Position(this.xPosition, this.yPosition), clonedBoard);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Position> generatePossibleMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        possibleMoves.addAll(getHorizontalMoves());
        possibleMoves.addAll(getVerticalMoves());
        return possibleMoves;
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

}
