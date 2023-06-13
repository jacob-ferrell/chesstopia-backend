package com.jacobferrell.chess.pieces;

import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;

public class King extends ChessPiece {
    public String name = "KING";

    public King(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♔' : '♚';
        this.rank = 1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        King clone = new King(color, new Position(position.x, position.y), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Move> generatePossibleMoves() {
        Set<Move> possibleMoves = new HashSet<>();
        possibleMoves.addAll(getHorizontalMoves());
        possibleMoves.addAll(getVerticalMoves());
        possibleMoves.addAll(getDiagonalMoves());
        possibleMoves = possibleMoves.stream()
                .filter(move -> Math.max(Math.abs(position.x - move.position.x),
                        Math.abs(position.y - move.position.y)) < 2)
                .collect(Collectors.toSet());
        return possibleMoves;
    }

    public boolean isInCheck() {
        Set<Move> allPossibleMoves = board.getAllPossibleMoves().get(getEnemyColor());
        return allPossibleMoves
                .stream()
                .anyMatch(move -> move.position.equals(position));
    }

    public boolean isInCheckMate() {
        return Move.removeMovesIntoCheck(board.getAllPossibleMoves().get(color)).isEmpty();
    }
}