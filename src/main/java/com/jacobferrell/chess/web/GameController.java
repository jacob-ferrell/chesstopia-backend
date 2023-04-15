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

    @PostMapping("/game/{gameId}/move")
    ResponseEntity<MoveModel> createMove(@PathVariable Long gameId, @Valid @RequestBody MoveModel move)
            throws URISyntaxException {
        //log.info("Request to create move: {}", move);
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        GameModel gameData = optionalGame.get();
        move.setPiece(gameData.getPieces().stream().filter(piece -> piece.getId() == move.getPiece().getId())
                .findFirst().get());
        //log.info("Request to create move: {}", move);
        Game game = createGameFromData(gameData);
        System.out.println(game.board);
        int x = move.getFromX();
        int y = move.getFromY();
        if (!game.board.isSpaceOccupied(x, y)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ChessPiece selectedPiece = game.board.getPieceAtPosition(x, y);
        System.out.println(selectedPiece);
        Set<Position> possibleMoves = selectedPiece.generatePossibleMoves();
        for (Position pos : possibleMoves) {
            System.out.println(pos);
        }
        if (!possibleMoves.stream().anyMatch(pos -> pos.equals(new Position(move.getToX(), move.getToY())))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        Move chessMove = getMoveFromData(selectedPiece, move);
        ChessBoard simulatedBoard = chessMove.simulateMove(game.board);
        if (!simulatedBoard.hasBothKings() || simulatedBoard.getPlayerKing(selectedPiece.getColor()).isInCheck()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        game.board.setPieceAtPosition(move.getToX(), move.getToY(), selectedPiece);
        game.board.setPositionToNull(x, y);
        System.out.println(game.board);
        gameData.setPieces(game.board.getPieceData());
        Set<MoveModel> moves = gameData.getMoves();
        if (moves == null) {
            moves = new HashSet<>();
            gameData.setMoves(moves);
        }
        MoveModel newMove = MoveModel.builder().piece(move.getPiece()).fromX(x).fromY(y).toX(move.getToX()).toY(move.getToY()).build();
        moves.add(newMove);
        gameRepository.save(gameData);
        gameRepository.findAll().forEach(System.out::println);
        return ResponseEntity.created(new URI("/api/game/" + gameData.getId() + "/move/" + newMove.getId()))
                .body(newMove);
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
        game.board.setBoardFromData(pieces);
        return game;
    }

    private Player getPlayerFromUser(User user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

    private Move getMoveFromData(ChessPiece piece, MoveModel move) {
        Move chessMove = new Move(piece, new Position(move.getToX(), move.getToY()));
        return chessMove;
    }

}
