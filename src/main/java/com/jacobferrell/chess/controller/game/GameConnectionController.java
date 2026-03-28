package com.jacobferrell.chess.controller.game;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.GameEntityRepository;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameConnectionController {

    private final NotificationService notificationService;

    private final GameEntityRepository gameEntityRepository;

    @MessageMapping("/game/{gameId}/connected")
    public void playerConnected(@DestinationVariable long gameId, Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        log.info("Player {} connected to game {}", user.getId(), gameId);

        notificationService.showPlayerIsConnectedToGame(gameId, user);

        // If the opponent is already subscribed, broadcast their status back so
        // the newly-connected player immediately sees them as online too.
        Optional<GameEntity> game = gameEntityRepository.findById(gameId);
        game.ifPresent(g -> {
            User opponent = g.getOpponent(user);
            if (notificationService.isUserSubscribedToGame(opponent, gameId)) {
                notificationService.showPlayerIsConnectedToGame(gameId, opponent);
            }
        });
    }

}
