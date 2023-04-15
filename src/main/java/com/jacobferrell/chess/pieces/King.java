package com.jacobferrell.chess.pieces;

import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

public class King extends ChessPiece {
    protected char SYMBOL;
    public String name = "KING";

    public King(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♔' : '♚';
    }

    public char getSymbol() {
        return SYMBOL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        King clone = new King(color, new Position(xPosition, yPosition), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Position> generatePossibleMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        possibleMoves.addAll(getHorizontalMoves());
        possibleMoves.addAll(getVerticalMoves());
        possibleMoves.addAll(getDiagonalMoves());
        return possibleMoves;
    }

    public boolean isInCheck() {
        Position currentPosition = new Position(xPosition, yPosition);
        Set<Move> allPossibleMoves = board.getAllPossibleMoves();
        return !allPossibleMoves
                .stream()
                .filter(move -> move.piece.color != color && move.to.equals(currentPosition))
                .collect(Collectors.toSet())
                .isEmpty();
    }

    public boolean isInCheckMate() {
        Set<Move> possiblePlayerMoves = board.getAllPossibleMoves()
                                             .stream()
                                             .filter(move -> move.piece.color != this.color)
                                             .collect(Collectors.toSet());
        for (Move move : possiblePlayerMoves) {
            ChessBoard simulatedBoard = move.simulateMove(board);
            if (simulatedBoard.hasBothKings() && !simulatedBoard.getPlayerKing(color).isInCheck()) {
                return false;
            }
        }
        return true;
    }
}