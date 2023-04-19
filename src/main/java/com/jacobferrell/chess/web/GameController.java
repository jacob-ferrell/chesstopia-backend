package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
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


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/games/user/{id}")
    Collection<GameModel> getUserGames(HttpServletRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(getEmailFromToken(request));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return gameRepository.findByPlayer(user);
        }
        /* token = token.substring(7);
        String userEmail = jwtService.extractUsername(token);
        Optional<User> user = userRepository.findByEmail(userEmail); */

        return gameRepository.findAll();
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/game/{id}")
    ResponseEntity<?> getGame(@PathVariable Long id) {
        Optional<GameModel> game = gameRepository.findById(id);
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
        User whitePlayer = rand >= .5 ? player1 : player2;
        GameModel newGame = GameModel.builder().players(players).whitePlayer(whitePlayer).build();
        gameRepository.save(newGame);
        return ResponseEntity.created(new URI("/api/game/" + newGame.getId()))
                .body(newGame);
    }

    /* @PostMapping("/employees")
    ResponseEntity<?> newGame(@RequestBody GameModel newGame) {

        EntityModel<GameModel> entityModel = assembler.toModel(repository.save(newEmployee));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    } */



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
