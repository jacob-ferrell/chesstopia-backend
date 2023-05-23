package com.jacobferrell.chess.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.GameRepository;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public List<GameDTO> getUserGames(long id, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("User could not be authenticated");
        }
        if (user.getId() != id) {
            throw new AccessDeniedException("Access Denied");
        }
        return gameRepository.findByPlayer(user);

    }

    public GameDTO getGame(long id, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("User could not be authenticated");
        }
        Optional<GameDTO> game = gameRepository.findById(id);
        if (!game.isPresent()) {
            throw new NotFoundException("Game with id: " + id + " could not be found");
        }
        GameDTO foundGame = game.get();
        Set<UserDTO> players = foundGame.getPlayers();
        if (!players.contains(user)) {
            throw new AccessDeniedException("Access Denied");
        }
        return foundGame;
    }

    public GameDTO createGame(long p1, long p2, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        if (user == null || user.getId() != p1) {
            throw new AccessDeniedException("Access Denied");
        }
        Optional<UserDTO> optionalPlayer1 = userRepository.findById(p1);
        Optional<UserDTO> optionalPlayer2 = userRepository.findById(p2);
        if (!optionalPlayer1.isPresent() || !optionalPlayer2.isPresent()) {
            throw new NotFoundException("One or both of the provided user ids do not exist");
        }
        //Randomly assign players to white/black
        Set<UserDTO> players = new HashSet<>();
        UserDTO player1 = optionalPlayer1.get();
        UserDTO player2 = optionalPlayer2.get();
        players.add(player1);
        players.add(player2);
        double rand = Math.random();
        UserDTO whitePlayer;
        UserDTO blackPlayer;
        if (rand >= .5) {
            whitePlayer = player1;
            blackPlayer = player2;
        } else {
            whitePlayer = player2;
            blackPlayer = player1;
        }
        GameDTO newGame = GameDTO.builder().players(players).whitePlayer(whitePlayer).blackPlayer(blackPlayer)
                .winner(null)
                .build();
        gameRepository.save(newGame);
        return newGame;
    }

}
