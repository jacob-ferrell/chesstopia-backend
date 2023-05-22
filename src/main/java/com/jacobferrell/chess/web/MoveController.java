package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.game.*;
import com.jacobferrell.chess.pieces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class MoveController {

    @Autowired
    private MoveService moveService;

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/game/{gameId}/possible-moves")
    ResponseEntity<?> getPossibleMoves(@PathVariable Long gameId, @RequestParam int x, @RequestParam int y) {
        Set<Position> possibleMoves = moveService.getPossibleMoves(gameId, x, y);
        Map<String, Set<Position>> responseBody = new HashMap<>();
        responseBody.put("possibleMoves", possibleMoves);
        return ResponseEntity.ok().body(responseBody);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/game/{gameId}/move")
    ResponseEntity<GameModel> makeMove(@PathVariable Long gameId, @RequestParam int x0, @RequestParam int y0,
            @RequestParam int x1, @RequestParam int y1, HttpServletRequest request)
            throws URISyntaxException {
        Map<String, Object> moveData = moveService.makeMove(gameId, x0, y0, x1, y1, request);
        GameModel gameData = (GameModel) moveData.get("gameData");
        MoveModel move = (MoveModel) moveData.get("moveData");
        return ResponseEntity.created(new URI("/api/game/" + gameData.getId() + "/move/" + move.getId()))
                .body(gameData); 
    }

    private void switchTurns(GameModel gameData) {
        if (gameData.getCurrentTurn().equals(gameData.getWhitePlayer())) {
            gameData.setCurrentTurn(gameData.getBlackPlayer());
            return;
        }
        gameData.setCurrentTurn(gameData.getWhitePlayer());
    }

    private void setPlayerInCheck(Game game, GameModel gameData, User player) {
        boolean isWhite = gameData.getWhitePlayer().equals(player);
        PieceColor enemyColor = isWhite ? PieceColor.BLACK : PieceColor.WHITE;
        King enemyKing = game.board.getPlayerKing(enemyColor);
        if (!enemyKing.isInCheck()) {
            gameData.setPlayerInCheck(null);
            return;
        }

        if (!enemyKing.isInCheckMate()) {
            gameData.setPlayerInCheck(enemyColor.toString());
            return;
        }
        gameData.setWinner(player);

    }

    private Move getMoveFromData(ChessPiece piece, MoveModel move) {
        Move chessMove = new Move(piece, new Position(move.getToX(), move.getToY()));
        return chessMove;
    }

    private String toJSON(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return null;
        }
    }
}