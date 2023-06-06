package com.jacobferrell.chess.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private GameCreationService gameCreationService;

    public List<GameDTO> getUserGames(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        return gameRepository.findByPlayer(user);

    }

    public GameDTO getGame(long id, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        user.setInLobby(false);
        userRepository.save(user);
        Optional<GameDTO> game = gameRepository.findById(id);
        if (!game.isPresent()) {
            throw new NotFoundException("Game with id: " + id + " could not be found");
        }
        GameDTO foundGame = game.get();
        Set<UserDTO> players = foundGame.getPlayers();
        if (!players.contains(user)) {
            throw new AccessDeniedException("Access Denied");
        }
        showPlayerIsConnectedToGame(foundGame.getId(), request, true);
        notificationService.markAsReadForGame(foundGame, user);
        return foundGame;
    }

    public void showPlayerIsConnectedToGame(long gameId, HttpServletRequest request, boolean isConnected) {
        Map<String, Object> map = new HashMap<>();
        map.put("connected", isConnected);
        map.put("player", jwtService.getUserFromRequest(request).getId());
        messagingTemplate.convertAndSend("/topic/game/" + gameId, jsonService.toJSON(map));
    }

    public GameDTO createGame(long p2, HttpServletRequest request) {
        return gameCreationService.createGame(p2, request);
    }
}
