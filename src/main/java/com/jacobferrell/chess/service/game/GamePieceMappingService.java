package com.jacobferrell.chess.service.game;

import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.GamePiecePosition;
import com.jacobferrell.chess.model.PieceEntity;
import com.jacobferrell.chess.model.PositionEntity;
import com.jacobferrell.chess.repository.GamePieceMappingRepository;
import com.jacobferrell.chess.service.game.piece.PieceService;
import com.jacobferrell.chess.service.game.piece.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GamePieceMappingService {

    private final GamePieceMappingRepository repository;

    private final PieceService pieceService;

    private final PositionService positionService;

    public List<GamePiecePosition> mapFromGame(GameEntity gameEntity, Game game) {
        return game.board().getAllPieces().stream()
                .map(piece -> GamePiecePosition
                        .builder()
                        .game(gameEntity)
                        .position(PositionEntity.of(piece.getPosition()))
                        .piece(PieceEntity.of(piece))
                        .build()
                )
                .toList();
    }

}
