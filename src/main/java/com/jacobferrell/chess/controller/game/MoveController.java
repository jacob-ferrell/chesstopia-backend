package com.jacobferrell.chess.controller.game;

import com.jacobferrell.chess.dto.MoveParams;
import com.jacobferrell.chess.dto.MoveResult;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.MoveEntity;
import com.jacobferrell.chess.service.game.computer.ComputerService;
import com.jacobferrell.chess.service.game.move.MoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MoveController {

    private final MoveService moveService;

    private final ComputerService computerService;

    @CrossOrigin(origins = { "https://www.jacobferrell.net", "https://jacob-ferrell.github.io", "http://localhost:5175" })
    @PostMapping("/game/{gameId}/move")
    ResponseEntity<GameEntity> makeMove(
            @PathVariable Long gameId,
            @ModelAttribute MoveParams moveParams
    ) throws URISyntaxException {

        MoveResult moveResult = moveService.makeMove(gameId, moveParams);

        GameEntity gameEntityData = moveResult.gameData();

        MoveEntity moveEntity = moveResult.moveData();

        if (moveEntity != null) {
            return ResponseEntity
                    .created(new URI("/api/game/" + gameEntityData.getId() + "/move/" + moveEntity.getId()))
                    .body(gameEntityData);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(gameEntityData);

    }

    @PostMapping("/game/{gameId}/computer-move")
    ResponseEntity<?> makeComputerMove(@PathVariable Long gameId) throws URISyntaxException {

        MoveResult result = computerService.makeMoveWithRetry(gameId);

        GameEntity gameEntityData = result.gameData();

        MoveEntity moveEntity = result.moveData();

        if (moveEntity != null) {
            return ResponseEntity
                    .created(new URI("/api/game/" + gameEntityData.getId() + "/move/" + moveEntity.getId()))
                    .body(gameEntityData);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameEntityData);
    }

}