package com.jacobferrell.chess.game.pieces;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Rook extends ChessPiece {

    private static final List<Integer[]> moveset = Stream.of(
            VERTICAL_MOVE_SET, HORIZONTAL_MOVE_SET
    ).flatMap(List::stream).toList();

    public Rook(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.ROOK, moveset);
        this.symbol = color == PieceColor.WHITE ? '♖' : '♜';
        this.rank = 3;
    }
/*

    public boolean canCastle(Set<Move> opponentsPossibleMoves) {
        King playerKing = board.getPlayerKing(color);
        if (playerKing.isHasMoved() || this.isHasMoved() || playerKing.isInCheck(opponentsPossibleMoves)) return false;

        Set<Position> kingsCastlePath = playerKing.getCastleTravelPositions(this);

        Set<Position> opponentsPossiblePositions = opponentsPossibleMoves.stream().map(Move::getTo).collect(Collectors.toSet());

        // King cannot be in, move through, or land in check
        boolean kingCanTravel = kingsCastlePath.stream()
                .allMatch(position ->
                    !opponentsPossiblePositions.contains(position) && !board.isPositionOccupied(position)
                );

        // If the rook is at x position 0, the space next to it can't be occupied for castling.
        // King's path has already been checked
        boolean rookCanTravel = position.x() != 0 || !board.isPositionOccupied(new Position(1, position.y()));

        return kingCanTravel && rookCanTravel;

    }
*/

}
