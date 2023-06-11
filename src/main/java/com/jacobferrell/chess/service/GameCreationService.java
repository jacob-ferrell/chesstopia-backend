package com.jacobferrell.chess.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.Role;
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

    /* @Autowired
    private ComputerMoveService computerMoveService; */

    public GameDTO createGame(long p2, HttpServletRequest request) {
        UserDTO player1 = jwtService.getUserFromRequest(request);
        Optional<UserDTO> optionalPlayer2;
        if(p2 == -1) {
            optionalPlayer2 = userRepository.findAIUser();
        } else {
            optionalPlayer2 = userRepository.findById(p2);
        }
        if (!optionalPlayer2.isPresent()) {
            throw new NotFoundException("The provided user id do not exist");
        }
        //Randomly assign players to white/black
        Set<UserDTO> players = new HashSet<>();
        UserDTO player2 = optionalPlayer2.get();
        players.add(player1);
        players.add(player2);
        int randomNumber = new Random().nextInt(2);
        UserDTO whitePlayer;
        UserDTO blackPlayer;
        if (randomNumber == 0) {
            whitePlayer = player1;
            blackPlayer = player2;
        } else {
            whitePlayer = player2;
            blackPlayer = player1;
        }
        GameDTO newGame = GameDTO.builder().players(players).whitePlayer(whitePlayer).blackPlayer(blackPlayer).currentTurn(whitePlayer)
                .winner(null)
                .build();
        gameRepository.save(newGame);
       /*  if (whitePlayer.getRole().equals(Role.AI)) {
            computerMoveService.makeComputerMove(newGame.getId());
        } */
        System.out.println(newGame);
        return newGame;
    }
}
