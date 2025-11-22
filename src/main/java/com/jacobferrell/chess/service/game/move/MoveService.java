package com.jacobferrell.chess.service.game.move;

import java.util.*;
import java.util.stream.Collectors;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.dto.MoveParams;
import com.jacobferrell.chess.dto.MoveResult;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.service.JsonService;
import com.jacobferrell.chess.service.game.GameService;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import com.jacobferrell.chess.service.game.user.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.repository.GameEntityRepository;
import com.jacobferrell.chess.repository.NotificationRepository;

import static com.jacobferrell.chess.game.Game.createGameFromEntity;

@Service
@RequiredArgsConstructor
public class MoveService {

    private final GameEntityRepository gameEntityRepository;

    private final GameService gameService;

    @Transactional
    public MoveResult makeMove(long gameId, MoveParams moveParams) {
        GameEntity gameEntity = gameService.getById(gameId);
        MoveContext context = new MoveContext(gameEntity, moveParams);
        return finalize(context);
    }

    @Transactional
    public MoveResult makeMove(GameEntity gameEntity, Move move, User user) {
        MoveContext context = new MoveContext(gameEntity, move, user);
        return finalize(context);
    }

    public MoveResult finalize(MoveContext context) {
        GameEntity gameEntity = context.getGameEntity();

        // TODO: revisit: End game as draw if king is lone piece and has moved >= 50 times
        if (context.getBoard().isStalemate(context.getPlayerColor())) {
            return handleDraw(gameEntity);
        }

        gameEntityRepository.save(gameEntity);

        // Send message to websocket with updated game state and notification
        // so that if other player is connected, the notification can automatically
        // be marked as read
        //notificationService.sendMessageAndNotification(SecurityUtils.getCurrentUser(), gameEntity);
        return new MoveResult(gameEntity, context.getMoveEntity());
    }

    public static MoveEntity executeMove(
            @NonNull GameEntity gameEntity,
            @NonNull Move move
    ) {
        var piece = move.getPiece();
        var position = move.getTo();
        GamePiecePosition pieceMapping = gameEntity.getGamePieceMapping(piece);

        move.execute();

        if (!move.isLegal()) {
            throw new IllegalArgumentException(
                    "Moving " + piece.getType() + " at " + "coordinates: x: " + piece.getPosition().x() + ", y: "
                            + piece.getPosition().y() + " to coordinates: x: " + position.x() + ", y: " + position.y() + " is not a valid move");
        }

        pieceMapping.setPosition(PositionEntity.of(piece.getPosition()));

        return MoveEntity.fromMove(pieceMapping, move);

    }

    public static MoveEntity executeMove(
            @NonNull GameEntity gameEntity,
            @NonNull Move move,
            PieceType promotion
    ) {

        if (isPromotion(move.getPiece(), move.getTo().y())) {
            handlePromotion(move.getPiece(), promotion, move.getTo().x(), move.getTo().y());
        }

        return executeMove(gameEntity, move);

    }

    public static boolean isPromotion(ChessPiece piece, int y) {
        return piece instanceof Pawn && ((piece.getColor().equals(PieceColor.WHITE) && y == 0)
                || (piece.getColor().equals(PieceColor.BLACK) && y == 7));
    }

    public static void handlePromotion(ChessPiece piece, @NonNull PieceType promotion, int x1, int y1) {
        Position from = piece.getPosition();
        piece = piece.getBoard().createNewPiece(promotion, new Position(x1, y1), piece.getColor());
        ChessBoard board = piece.getBoard();
        board.removePiece(from);
        board.placePieceAndCapture(piece.getPosition(), piece);
    }

    public MoveResult handleDraw(GameEntity gameEntityData) {
        gameEntityData.setGameOver(true);
        gameEntityRepository.save(gameEntityData);
        return new MoveResult(gameEntityData, null);
    }

}
