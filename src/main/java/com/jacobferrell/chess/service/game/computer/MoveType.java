package com.jacobferrell.chess.service.game.computer;

import com.jacobferrell.chess.game.pieces.Move;

import java.util.List;

public enum MoveType {
    CHECK_MATE,
    CHECK,
    TAKE{
        @Override
        public Move selectMove(List<Move> moves) {
            int maxRank = Integer.MIN_VALUE;
            Move highestRankedMove = null;
            for (var move : moves) {
                if (move.getTakenPiece() != null && move.getTakenPiece().getRank() > maxRank) {
                    maxRank = move.getTakenPiece().getRank();
                    highestRankedMove = move;
                }
            }

            return highestRankedMove;

        }
    },
    NORMAL;

    public Move selectMove(List<Move> moves) {
        return moves.getFirst();
    }
}
