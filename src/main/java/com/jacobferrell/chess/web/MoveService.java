package com.jacobferrell.chess.web;

import java.util.Map;
import java.util.Set;

import com.jacobferrell.chess.chessboard.Position;
import com.jacobferrell.chess.model.GameModel;

import jakarta.servlet.http.HttpServletRequest;


public interface MoveService {
    Set<Position> getPossibleMoves(long gameId, int x, int y);
    Map<String, Object> makeMove(long gameId, int x0, int y0, int x1, int y1, HttpServletRequest request);
}
