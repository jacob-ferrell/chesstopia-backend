package com.jacobferrell.chess.game;

import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;

public record Player(String name, PieceColor color) {

    public static Player getPlayerFromUser(User user, PieceColor color) {
        return new Player(user.getFirstName(), color);
    }

    public static void validate(GameEntity gameEntity, User user) {
        gameEntity.validatePlayer(user);
        validateIsPlayersTurn(gameEntity, user);
        validateGameIsNotOver(gameEntity);
    }

    private static void validateIsPlayersTurn(GameEntity gameEntity, User user) {
        if (!gameEntity.getCurrentTurn().equals(user)) {
            throw new IllegalArgumentException("User " + user.getEmail() + " is attempting to move out of turn");
        }
    }

    private static void validateGameIsNotOver(GameEntity gameEntity) {
        if (!gameEntity.getGameOver()) return;

        throw new IllegalArgumentException(
                "Game with id: " + gameEntity.getId() + " is over and additional moves cannot be made");
    }

}
