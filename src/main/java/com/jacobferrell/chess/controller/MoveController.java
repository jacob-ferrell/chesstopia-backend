package com.jacobferrell.chess.controller;

import com.jacobferrell.chess.model.GameModel;
import com.jacobferrell.chess.model.MoveModel;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.service.MoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
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

}