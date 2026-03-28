package com.jacobferrell.chess.service.game.piece;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.ChessPiece;
import com.jacobferrell.chess.game.pieces.Move;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.PositionEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.PositionEntityRepository;
import com.jacobferrell.chess.service.game.GameService;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.jacobferrell.chess.game.Game.createGameFromEntity;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionEntityRepository repository;

    private final GameService gameService;

    private final NotificationService notificationService;

    public Set<Position> getPossiblePositions(long gameId, int x, int y) {
        User user = SecurityUtils.getCurrentUser();

        GameEntity gameEntityData = gameService.getById(gameId);

        Game game = createGameFromEntity(gameEntityData);

        ChessPiece piece = ChessPiece.getAndValidatePiece(new Position(x, y), game, user, PieceColor.fromGameAndUser(gameEntityData, user));

        notificationService.showPlayerIsConnectedToGame(gameId, user);

        return piece.generatePossibleMoves().stream()
                .filter(move -> { move.simulate(); return move.isLegal(); })
                .map(Move::getTo)
                .collect(Collectors.toSet());

    }

    @PostConstruct
    private void loadCache() {
        PositionEntity.preload(repository.findAll());
    }

}
