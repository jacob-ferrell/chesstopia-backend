package com.jacobferrell.chess.web;

import java.util.Set;

import com.jacobferrell.chess.chessboard.Position;

public interface MoveService {
    Set<Position> getPossibleMoves(long gameId, int x, int y);
}
