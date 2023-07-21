package com.jacobferrell.chess.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.GameRepository;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GameCreationService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public GameDTO createGame(long p2, HttpServletRequest request) {
        UserDTO player1;
        UserDTO player2;

        player1 = jwtService.getUserFromRequest(request);

        player2 = p2 == -1
                ? userRepository.findAIUser().orElseThrow()
                : userRepository.findById(p2).orElseThrow();

        UserDTO[] playersArray = { player1, player2 };

        return buildGame(playersArray);

    }

    public void createDemoGames(UserDTO demoUser) {
        UserDTO[] playerArrayForGameWithComputer = { demoUser, userRepository.findAIUser().orElseThrow() };
        UserDTO[] playerArrayForGameWithPlayer = { demoUser,
                userRepository.findByEmail("boomkablamo@gmail.com").orElseThrow() };
        buildGame(playerArrayForGameWithComputer);
        buildGame(playerArrayForGameWithPlayer);
    }

    public UserDTO[] randomlyAssignPlayers(UserDTO[] playersArray) {
        int randomNumber = new Random().nextInt(2);
        UserDTO whitePlayer = playersArray[randomNumber];
        UserDTO blackPlayer = playersArray[Math.abs(randomNumber - 1)];
        UserDTO[] assignedPlayersArray = { whitePlayer, blackPlayer };
        return assignedPlayersArray;
    }

    public GameDTO buildGame(UserDTO[] players) {
        UserDTO[] assignedPlayers = randomlyAssignPlayers(players);
        GameDTO newGame = GameDTO
                .builder()
                .players(new HashSet<>(Arrays.asList(assignedPlayers)))
                .whitePlayer(assignedPlayers[0])
                .blackPlayer(assignedPlayers[1])
                .currentTurn(assignedPlayers[0])
                .winner(null)
                .build();
        gameRepository.save(newGame);
        return newGame;
    }
}
