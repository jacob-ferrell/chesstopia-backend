package com.jacobferrell.chess.game.pieces;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pawn extends ChessPiece {

    private final int yDirection;

    public Pawn(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board, PieceType.PAWN, null);
        this.symbol = color == PieceColor.WHITE ? '♙' : '♟';
        yDirection = color.equals(PieceColor.WHITE) ? -1 : 1;
        this.rank = 6;
    }

    @Override
    public Set<Position> generatePossiblePositions() {
        return Stream.of(getDiagonalPositions(), getVerticalPositions())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Position> getDiagonalPositions() {
        Set<Position> result = new HashSet<>();
        int currentX = position.x();
        int currentY = position.y();
        for (int x = currentX - 1; x < currentX + 2 && x < 8; x++) {
            int y = currentY + yDirection;
            Position pos = new Position(x, y);
            if (Math.min(x, y) < 0 || y > 8 || x == currentX || !board.isPositionOccupied(pos)) {
                continue;
            }

            board.getPieceAtPosition(pos)
                    .filter(this::isEnemyPiece)
                    .ifPresent(ignored -> result.add(pos));

        }

        return result;
    }

    public static List<Integer[]> getAttackMoveset(ChessPiece piece) {
        int yDirection = piece.getEnemyColor().equals(PieceColor.WHITE) ? 1 : -1;
        return List.of(new Integer[]{yDirection, 1}, new Integer[]{yDirection, -1});
    }

    @Override
    public Set<Position> getVerticalPositions() {
        Set<Position> result = new HashSet<>();
        int currentX = position.x();
        int currentY = position.y();
        int maxSpaces = hasMoved ? 1 : 2;
        int spacesMoved = 0;
        for (int y = currentY + yDirection; y < 8 && y > -1 && spacesMoved < maxSpaces; y += yDirection) {
            spacesMoved++;
            Position pos = new Position(currentX, y);
            if (board.isPositionOccupied(pos)) {
                break;
            }
            result.add(pos);
        }

        return result;
    }

}
