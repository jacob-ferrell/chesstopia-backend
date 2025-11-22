package com.jacobferrell.chess.controller.game;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.service.game.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class GameController {

    private final GameService gameService;

    @GetMapping("/games")
    public ResponseEntity<List<GameEntity>> getUserGames() {
        List<GameEntity> userGameEntities = gameService.getUserGames();
        return ResponseEntity.ok().body(userGameEntities);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @GetMapping("/game/{id}")
    ResponseEntity<GameEntity> getGame(@PathVariable Long id) {
        GameEntity gameEntity = gameService.getGame(id);
        return ResponseEntity.ok().body(gameEntity);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @PostMapping("/games/{p2}")
    ResponseEntity<GameEntity> createGame(@PathVariable Long p2) throws URISyntaxException {
        GameEntity newGameEntity = gameService.createGame(p2);
        return ResponseEntity.created(new URI("/api/game/" + newGameEntity.getId()))
                .body(newGameEntity);
    }

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @PostMapping("/games/computer")
    ResponseEntity<GameEntity> createComputerGame() throws URISyntaxException {
        GameEntity newGameEntity = gameService.createGame(-1);
        return ResponseEntity.created(new URI("/api/game/" + newGameEntity.getId()))
                .body(newGameEntity);
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
