package com.jacobferrell.chess.pieces;

import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public class Rook extends ChessPiece {
    public String name = "ROOK";
    public boolean canCastle;

    public Rook(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♖' : '♜';
        this.rank = 3;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard clonedBoard) {
        Rook clone = new Rook(this.color, new Position(position.x, position.y), clonedBoard);
        clone.hasMoved = hasMoved;
        return clone;
    }

    public Set<Position> getCastleTravelPositions(King king) {
        Set<Position> travelPositions = new HashSet<>();
        if (position.x == 0) {
            for (int x = 0; x <= king.position.x; x++) {
                travelPositions.add(new Position(x, position.y));
            }
            return travelPositions;
        }
        for (int x = 7; x >= king.position.x; x--) {
            travelPositions.add(new Position(x, position.y));
        }
        return travelPositions;
    }


    @Override
    public Set<Move> generatePossibleMoves() {
        Set<Move> possibleMoves = new HashSet<>();
        possibleMoves.addAll(getHorizontalMoves());
        possibleMoves.addAll(getVerticalMoves());
        return possibleMoves;
    }


}
