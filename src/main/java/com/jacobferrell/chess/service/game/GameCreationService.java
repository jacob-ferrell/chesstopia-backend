package com.jacobferrell.chess.service.game;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.GameEntityRepository;
import com.jacobferrell.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class GameCreationService {

    private final GameEntityRepository gameEntityRepository;

    private final UserRepository userRepository;

    public GameEntity createGame(User player2) {

        User player1 = SecurityUtils.getCurrentUser();

        User[] playersArray = { player1, player2 };

        return buildGame(playersArray, false);

    }

    public GameEntity createGame(long player2Id) {

        User player2 = player2Id == -1
                ? userRepository.findAIUser().orElseThrow()
                : userRepository.findById(player2Id).orElseThrow();

        return createGame(player2);

    }

    public void createDemoGames(User demoUser) {
        User[] playerArrayForGameWithComputer = { demoUser, userRepository.findAIUser().orElseThrow() };
        User[] playerArrayForGameWithPlayer = { demoUser,
                userRepository.findByEmail("boomkablamo@gmail.com").orElseThrow() };
        buildGame(playerArrayForGameWithComputer, true);
        buildGame(playerArrayForGameWithPlayer, false);
    }

    public User[] randomlyAssignPlayers(User[] playersArray) {
        int randomNumber = new Random().nextInt(2);
        User whitePlayer = playersArray[randomNumber];
        User blackPlayer = playersArray[Math.abs(randomNumber - 1)];
        return new User[]{ whitePlayer, blackPlayer };
    }

    public GameEntity buildGame(User[] players, boolean isDemoComputerGame) {
        User[] assignedPlayers;
        if (isDemoComputerGame) {
            assignedPlayers = players;
        } else {
            assignedPlayers = randomlyAssignPlayers(players);
        }

        GameEntity newGameEntity = GameEntity.newGame(assignedPlayers[0], assignedPlayers[1]);

        gameEntityRepository.save(newGameEntity);

        return newGameEntity;
    }

}
