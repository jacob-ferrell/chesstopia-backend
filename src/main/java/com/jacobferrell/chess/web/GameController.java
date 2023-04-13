package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.game.*;
import com.jacobferrell.chess.pieces.PieceColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public GameController(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/games")
    Collection<GameModel> games() {
        return gameRepository.findAll();
    }

    @GetMapping("/game/{id}")
    ResponseEntity<?> getGame(@PathVariable Long id) {
        Optional<GameModel> game = gameRepository.findById(id);
        return game.map(response -> ResponseEntity.ok().body(response))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/game/{id}")
    ResponseEntity<GameModel> createGame(@Valid @RequestBody GameModel game) throws URISyntaxException {
        log.info("Request to create game: {}", game);
        GameModel result = gameRepository.save(game);
        return ResponseEntity.created(new URI("/api/game/" + result.getId()))
                .body(result);
    }

    @PostMapping("/game/{gameId}/move")
    ResponseEntity<MoveModel> createMove(@PathVariable Long gameId, @Valid @RequestBody MoveModel move)
            throws URISyntaxException {
        log.info("Request to create move: {}", move);
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GameModel gameData = optionalGame.get();
        

        Set<MoveModel> moves = gameData.getMoves();
        if (moves == null) {
            moves = new HashSet<>();
            gameData.setMoves(moves);
        }
        moves.add(move);
        gameRepository.save(gameData);
        return ResponseEntity.created(new URI("/api/game/" + gameData.getId() + "/move/" + move.getId()))
                .body(move);
    }
    
    @PutMapping("/game/{id}")
    ResponseEntity<GameModel> updateGame(@Valid @RequestBody GameModel game) {
        log.info("Request to update game: {}", game);
        GameModel result = gameRepository.save(game);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/game/{id}")
    ResponseEntity<GameModel> deleteGame(@PathVariable Long id) {
        log.info("Request to delete game: {}", id);
        gameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    private Game createGameFromData(GameModel data) {
        Player player1 = getPlayerFromUser(data.getPlayer1(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getPlayer2(), PieceColor.BLACK);
        Game game = new Game(player1, player2);
        Set<Piece> pieces = data.getPieces();
        game.board.setBoardFromData(data);
    }

    private Player getPlayerFromUser(User user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

}
