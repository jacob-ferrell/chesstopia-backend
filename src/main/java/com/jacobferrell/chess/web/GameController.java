package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.game.*;
import com.jacobferrell.chess.pieces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {
    private final Logger log = LoggerFactory.getLogger(GameController.class);
    private GameRepository gameRepository;
    private UserRepository userRepository;
    private JwtService jwtService;

    public GameController(GameRepository gameRepository, UserRepository userRepository, JwtService jwtService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    private String getEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7);
            return jwtService.extractUsername(jwt);
        }
        return null;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(getEmailFromToken(request));
        if (!optionalUser.isPresent()) {
            return null;
        }
        User user = optionalUser.get();
        return user;
    }

    @GetMapping("/games/user/{id}")
    public Object getUserGames(@PathVariable Long id, HttpServletRequest request) {
        User user = jwtService.getUserFromRequest(request);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (user.getId() != id)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        return gameRepository.findByPlayer(user);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/game/{id}")
    ResponseEntity<?> getGame(@PathVariable Long id, HttpServletRequest request) {
        // TODO add auth
        User user = getUserFromRequest(request);
        if (user == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        Optional<GameModel> game = gameRepository.findById(id);
        if (!game.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GameModel foundGame = game.get();
        Set<User> players = foundGame.getPlayers();
        if (!players.contains(user)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return game.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/games")
    ResponseEntity<?> createGame(@RequestParam Long p1, @RequestParam Long p2) throws URISyntaxException {
        log.info("Request to create game");
        Optional<User> optionalPlayer1 = userRepository.findById(p1);
        Optional<User> optionalPlayer2 = userRepository.findById(p2);
        if (!optionalPlayer1.isPresent() || !optionalPlayer2.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Set<User> players = new HashSet<>();
        User player1 = optionalPlayer1.get();
        User player2 = optionalPlayer2.get();
        players.add(player1);
        players.add(player2);
        double rand = Math.random();
        User whitePlayer;
        User blackPlayer;
        if (rand >= .5) {
            whitePlayer = player1;
            blackPlayer = player2;
        } else {
            whitePlayer = player2;
            blackPlayer = player1;
        }
        GameModel newGame = GameModel.builder().players(players).whitePlayer(whitePlayer).blackPlayer(blackPlayer)
                .winner(null)
                .build();
        gameRepository.save(newGame);
        return ResponseEntity.created(new URI("/api/game/" + newGame.getId()))
                .body(newGame);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PutMapping("/game/{id}")
    ResponseEntity<GameModel> updateGame(@Valid @RequestBody GameModel game) {
        log.info("Request to update game: {}", game);
        GameModel result = gameRepository.save(game);
        return ResponseEntity.ok().body(result);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @DeleteMapping("/game/{id}")
    ResponseEntity<GameModel> deleteGame(@PathVariable Long id) {
        log.info("Request to delete game: {}", id);
        gameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
