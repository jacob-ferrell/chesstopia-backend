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
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class MoveController {
    //@Autowired
    //private SimpMessagingTemplate messagingTemplate;

    private final Logger log = LoggerFactory.getLogger(GameController.class);
    private GameRepository gameRepository;
    private JwtService jwtService;

    public MoveController(GameRepository gameRepository, JwtService jwtService) {
        this.gameRepository = gameRepository;
        this.jwtService = jwtService;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/game/{gameId}/possible-moves")
    ResponseEntity<?> getPossibleMoves(@PathVariable Long gameId, @RequestParam int x, @RequestParam int y) {
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // TODO: Add authentication to test if player is moving their own piece
        GameModel gameData = optionalGame.get();
        Game game = createGameFromData(gameData);
        if (!game.board.isSpaceOccupied(x, y)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ChessPiece piece = game.board.getPieceAtPosition(x, y);
        Set<Position> possibleMoves = piece.removeMovesIntoCheck(piece.generatePossibleMoves());
        Map<String, Set<Position>> responseBody = new HashMap<>();
        responseBody.put("possibleMoves", possibleMoves);
        return ResponseEntity.ok().body(responseBody);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/game/{gameId}/move")
    ResponseEntity<GameModel> makeMove(@PathVariable Long gameId, @RequestParam int x0, @RequestParam int y0,
            @RequestParam int x1, @RequestParam int y1, HttpServletRequest request)
            throws URISyntaxException {
        // TODO: add authentication to see if game and piece belong to player
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user =  jwtService.getUserFromRequest(request);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        GameModel gameData = optionalGame.get();
        boolean userIsPlayer = false;
        boolean isUsersTurn = gameData.getCurrentTurn().equals(user);
        for (User player : gameData.getPlayers()) {
            if (player.getEmail() == user.getEmail()) userIsPlayer = true;
        }

        if (!userIsPlayer || !isUsersTurn) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        String playerColor = gameData.getWhitePlayer().equals(user) ? "WHITE" : "BLACK";

        log.info("Request to create move");
        Game game = createGameFromData(gameData);
        if (!game.board.isSpaceOccupied(x0, y0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ChessPiece selectedPiece = game.board.getPieceAtPosition(x0, y0);
        Set<Position> possibleMoves = selectedPiece.generatePossibleMoves();
        if (!possibleMoves.stream().anyMatch(pos -> pos.equals(new Position(x1, y1)))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        String color = selectedPiece.getColor() == PieceColor.WHITE ? "WHITE" : "BLACK";
        if (color != playerColor) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        MoveModel move = MoveModel.builder().pieceType(selectedPiece.getName()).pieceColor(color).fromX(x0).fromY(y0)
                .toX(x1).toY(y1).build();
        Move chessMove = getMoveFromData(selectedPiece, move);
        ChessBoard simulatedBoard = chessMove.simulateMove(game.board);
        if (!simulatedBoard.hasBothKings() || simulatedBoard.getPlayerKing(selectedPiece.getColor()).isInCheck()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        game.board.setPieceAtPosition(x1, y1, selectedPiece);
        game.board.setPositionToNull(x0, y0);
        System.out.println(game.board);
        gameData.setPieces(game.board.getPieceData());
        Set<MoveModel> moves = gameData.getMoves();
        if (moves == null) {
            moves = new HashSet<>();
            gameData.setMoves(moves);
        }
        moves.add(move);
        switchTurns(gameData);
        gameRepository.save(gameData);
        //sendToSocket(gameData); 
        gameRepository.findAll().forEach(System.out::println);
        return ResponseEntity.created(new URI("/api/game/" + gameData.getId() + "/move/" + move.getId()))
                .body(gameData);
    }

    private Game createGameFromData(GameModel data) {
        Player player1 = getPlayerFromUser(data.getWhitePlayer(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getBlackPlayer(), PieceColor.BLACK);
        Game game = new Game(player1, player2);
        Set<Piece> pieces = data.getPieces();
        game.board.setBoardFromData(pieces);
        return game;
    }

    private void sendToSocket(GameModel gameData) {
        String destination = "/game/" + gameData.getId() + "/move";
        //messagingTemplate.convertAndSend(destination, gameData);

    }

    private Player getPlayerFromUser(User user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

    private void switchTurns(GameModel gameData) {
        if (gameData.getCurrentTurn().equals(gameData.getWhitePlayer())) {
            gameData.setCurrentTurn(gameData.getBlackPlayer());
            return;
        }
        gameData.setCurrentTurn(gameData.getWhitePlayer());
    }

    private Move getMoveFromData(ChessPiece piece, MoveModel move) {
        Move chessMove = new Move(piece, new Position(move.getToX(), move.getToY()));
        return chessMove;
    }
}