package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.game.*;
import com.jacobferrell.chess.pieces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;


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

    public GameController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/games")
    Collection<GameModel> games() {
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
    ResponseEntity<GameModel> createGame(@Valid @RequestBody GameModel game) throws URISyntaxException {
        log.info("Request to create game: {}", game);
        GameModel result = gameRepository.save(game);
        return ResponseEntity.created(new URI("/api/game/" + result.getId()))
                .body(result);
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
