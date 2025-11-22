package com.jacobferrell.chess.service.game.computer;

import java.util.*;

import com.jacobferrell.chess.dto.MoveResult;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.service.game.GameService;
import com.jacobferrell.chess.service.game.move.MoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.repository.GameEntityRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComputerService {

    private final MoveService moveService;

    private final GameService gameService;

    private final GameEntityRepository gameEntityRepository;

    @Transactional
    public MoveResult makeMove(long gameId) {

        GameEntity gameEntity = gameService.getById(gameId);

        var computer = getComputerPlayer(gameEntity);

        if (gameEntity.getGameOver()) {
            return new MoveResult(gameEntity, null);
        }

        gameEntity.validateIsPlayersTurn(computer);

        PieceColor computerColor = gameEntity.getPlayersColor(computer);

        Game game = Game.createGameFromEntity(gameEntity);

        Set<Move> allPossibleComputerMoves =
                game.board().getAllPossibleMoves(computerColor);

        if (allPossibleComputerMoves.isEmpty()) {
            return handleCheckmate(gameEntity, computer);
        }

        Move move = selectMove(allPossibleComputerMoves);
        log.info(move.toString());
        return moveService.makeMove(gameEntity, move, computer);

    }

    public static Move selectMove(Set<Move> allPossibleComputerMoves) {

        var moveMap = getMoveMap(allPossibleComputerMoves);

        return selectMoveFromMap(moveMap);

    }

    private static Move selectMoveFromMap(Map<MoveType, List<Move>> moveMap) {
        return Arrays.stream(MoveType.values())
                .filter(moveType -> !moveMap.getOrDefault(moveType, List.of()).isEmpty())
                .map(moveType -> moveType.selectMove(moveMap.get(moveType)))
                .findFirst()
                .orElseThrow();
    }
/*

    private void setAndSave(GamePieceMapping gamePieceMapping, Move move, Map<String, Object> outMap) {
        ChessPiece piece = move.getPiece();
        int fromX = piece.getPosition().x();
        int fromY = piece.getPosition().y();
        int toX = move.getTo().x();
        int toY = move.getTo().y();
        if (MoveService.isPromotion(piece, toY)) {
            piece = piece.getBoard().createNewPiece(PieceType.QUEEN, move.getTo(), piece.getColor());
            MoveService.handlePromotion(piece, new Position(fromX, fromY));
        }


        piece.makeMove(new Position(toX, toY));
        MoveEntity moveEntityData = moveService.createMoveEntity(gamePieceMapping, fromX, fromY, toX, toY);
        gameEntityData.setPieces(piece.getBoard().getPieceData(gameEntityData));
        List<MoveEntity> moveEntities = gameEntityData.getMoveEntities();
        moveEntities.add(moveEntityData);
        moveService.switchTurns(gameEntityData);
        moveService.setPlayerInCheck(game, gameEntityData, getComputerPlayer(gameEntityData));
        if (game.board().isStalemate(piece.getColor())) {
            moveService.handleDraw(gameEntityData, outMap);
        }
        gameRepository.save(gameEntityData);
        sendMessageAndNotification(gameEntityData);
        outMap.put("gameData", gameEntityData);
        outMap.put("moveData", moveEntityData);

    }
*/

    private MoveResult handleCheckmate(GameEntity gameEntity, User computer) {
        gameEntity.setWinnerFromLoser(computer);
        gameEntityRepository.save(gameEntity);
        return new MoveResult(gameEntity, null);
    }

/*

    private void sendMessageAndNotification(GameEntity gameEntityData) {
        User computer = getComputerPlayer(gameEntityData);
        String message = "Computer made move in game " + gameEntityData.getId();
        Notification notification = notificationService.createNotification(computer, UserService.getOtherPlayer(computer, gameEntityData), message);
        notification.setGameEntity(gameEntityData);
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/game/" + gameEntityData.getId(),
                jsonService.toJSON(moveService.getMessageBody(gameEntityData, notification)));

    }
*/

    private User getComputerPlayer(GameEntity gameEntityData) {
        return gameEntityData
                .getPlayers()
                .stream()
                .filter(p -> p.getRole().equals(Role.AI))
                .findFirst()
                .orElseThrow();
    }

    private static Map<MoveType, List<Move>> getMoveMap(Set<Move> allPossibleMoves) {
        Map<MoveType, List<Move>> map = new EnumMap<>(MoveType.class);
        for (Move move: allPossibleMoves) {

            move.simulate();

            if (!move.isLegal()) continue;

            // short circuit for checkmate moves
            if (move.isOtherPlayerInCheckMate()) {
                return Map.of(MoveType.CHECK_MATE, List.of(move));
            }

            map.computeIfAbsent(move.getMoveType(), k -> new ArrayList<>())
                    .add(move);
        }
        return map;
    }

}
