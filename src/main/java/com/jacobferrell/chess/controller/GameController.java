package com.jacobferrell.chess.controller;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/games")
    public ResponseEntity<List<GameDTO>> getUserGames(HttpServletRequest request) {
        List<GameDTO> userGames = gameService.getUserGames(request);
        return ResponseEntity.ok().body(userGames);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @GetMapping("/game/{id}")
    ResponseEntity<GameDTO> getGame(@PathVariable Long id, HttpServletRequest request) {
        GameDTO game = gameService.getGame(id, request);
        return ResponseEntity.ok().body(game);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @PostMapping("/games/{p2}")
    ResponseEntity<GameDTO> createGame(@PathVariable Long p2, HttpServletRequest request) throws URISyntaxException {
        GameDTO newGame = gameService.createGame(p2, request);
        return ResponseEntity.created(new URI("/api/game/" + newGame.getId()))
                .body(newGame);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @PostMapping("/games/computer")
    ResponseEntity<GameDTO> createComputerGame(HttpServletRequest request) throws URISyntaxException {
        GameDTO newGame = gameService.createGame(-1, request);
        return ResponseEntity.created(new URI("/api/game/" + newGame.getId()))
                .body(newGame);
    }

    /* @CrossOrigin(origins = { "https://www.jacobferrell.net", "http://localhost:5175" })
    @PutMapping("/game/{id}")
    ResponseEntity<GameDTO> updateGame(@Valid @RequestBody GameDTO game) {
        log.info("Request to update game: {}", game);
        GameDTO result = gameRepository.save(game);
        return ResponseEntity.ok().body(result);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "http://localhost:5175" })
    @DeleteMapping("/game/{id}")
    ResponseEntity<GameDTO> deleteGame(@PathVariable Long id) {
        log.info("Request to delete game: {}", id);
        gameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    } */

}
