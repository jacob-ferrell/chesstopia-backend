package com.jacobferrell.chess.service.game;

import java.util.List;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.service.JsonService;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.GameEntityRepository;
import com.jacobferrell.chess.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameEntityRepository gameEntityRepository;

    private final UserRepository userRepository;

    private final NotificationService notificationService;

    private final SimpMessagingTemplate messagingTemplate;

    private final JsonService jsonService;

    private final GameCreationService gameCreationService;

    public List<GameEntity> getUserGames() {
        User user = SecurityUtils.getCurrentUser();
        return gameEntityRepository.findAllByUser(user);

    }

    public GameEntity getById(Long id) {
        return gameEntityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Game not found with id: " + id) );
    }

    public GameEntity getGame(long id) {

        User user = SecurityUtils.getCurrentUser();

        user.setInLobby(false);

        userRepository.save(user);

        GameEntity game = gameEntityRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Game with id: " + id + " could not be found"));

        game.validatePlayer(user);

        notificationService.showPlayerIsConnectedToGame(game.getId(), true);

        notificationService.markAllAsReadForGame(game, user);

        return game;
    }

    public GameEntity createGame(long p2) {
        return gameCreationService.createGame(p2);
    }

}
