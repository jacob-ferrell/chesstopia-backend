package com.jacobferrell.chess.controller.game.piece;

import com.jacobferrell.chess.service.game.piece.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @CrossOrigin(origins = {
            "https://www.jacobferrell.net", "https://jacob-ferrell.github.io","http://localhost:5175"
    })
    @GetMapping("/game/{gameId}/possible-moves")
    ResponseEntity<?> getPossibleMoves(
            @PathVariable Long gameId,
            @RequestParam int x,
            @RequestParam int y
    ) {
        return ResponseEntity.ok().body(
                positionService.getPossiblePositions(gameId, x, y)
        );
    }

}
