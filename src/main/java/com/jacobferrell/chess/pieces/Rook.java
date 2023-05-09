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
        System.out.println("!!!!!!!!!!!! " + this + canCastle());
        if (canCastle()) {
            King king = board.getPlayerKing(color);
            Position kingPosition = new Position(king.getXPosition(), king.getYPosition());
            possibleMoves.add(kingPosition);
        }
        return possibleMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Rook)) {
            return false;
        }
        Rook otherRook = (Rook) o;
        return xPosition == otherRook.getXPosition() && yPosition == otherRook.getYPosition()
                && color == otherRook.getColor() && hasMoved == otherRook.hasMoved;
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

}
