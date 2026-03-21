package com.jacobferrell.chess.game.pieces;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class King extends ChessPiece {

    private static final List<Integer[]> moveset = Stream.of(
            HORIZONTAL_MOVE_SET, VERTICAL_MOVE_SET, DIAGONAL_MOVE_SET
    ).flatMap(List::stream).toList();

    public King(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.KING, moveset, 1);
        this.symbol = color == PieceColor.WHITE ? '♔' : '♚';
        this.rank = 1;
    }

    public Set<Position> getCastleTravelPositions(Rook rook) {
        Set<Position> travelPositions = new HashSet<>();
        int rookX = rook.getPosition().x();
        int kingX = this.position.x();
        int y = this.position.y();
        // King moves 2 squares toward the rook
        int direction = rookX < kingX ? -1 : 1;
        for (int i = 1; i <= 2; i++) {
            travelPositions.add(new Position(kingX + direction * i, y));
        }

        return travelPositions;
    }

    // Instead of generating all possible moves for every enemy piece, generate all possible
    // moves from the King, and see if they contain enemy pieces
    public boolean isInCheck() {
        return canBeAttacked();
    }
/*

    // Overloaded with precomputed allPossibleMoves to avoid recalc
    public boolean isInCheck(Set<Move> allPossibleMoves) {
        return allPossibleMoves.stream()
                .map(Move::getTo)
                .collect(Collectors.toSet())
                .contains(this.position);
    }
*/

    public boolean isInCheckMate() {
        return board.getAllPossibleMoves(color).isEmpty();
    }
}