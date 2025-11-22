package com.jacobferrell.chess.dto;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.MoveEntity;

public record MoveResult(GameEntity gameData, MoveEntity moveData) {
}
