package com.jacobferrell.chess.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.chessboard.ChessBoard;
import com.jacobferrell.chess.chessboard.Position;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.MoveDTO;
import com.jacobferrell.chess.model.NotificationDTO;
import com.jacobferrell.chess.model.Role;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.pieces.ChessPiece;
import com.jacobferrell.chess.pieces.King;
import com.jacobferrell.chess.pieces.Move;
import com.jacobferrell.chess.pieces.PieceColor;
import com.jacobferrell.chess.repository.GameRepository;

@Service
public class ComputerMoveService {

    @Autowired
    private MoveService moveService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JsonService jsonService;

    private static final Random random = new Random();

    public Map<String, Object> makeComputerMove(long gameId) {
        GameDTO gameData = moveService.getGameById(gameId);
        var computer = getComputerPlayer(gameData);
        Map<String, Object> outMap = new HashMap<>();

        try {
            moveService.validateGameIsNotOver(gameData);
            moveService.validateIsPlayersTurn(gameData, computer);
        } catch (Throwable ex) {
            return outMap;
        }
        PieceColor computerColor = moveService.getPlayerColor(gameData, computer);
        Game game = moveService.createGameFromDTO(gameData);
        Set<Move> allPossibleComputerMoves = game.board.getAllPossibleMoves(computerColor);
        Map<String, Map<ChessPiece, Set<Move>>> moveMap = getMoveMap(allPossibleComputerMoves);
        System.out.println(moveMap);
        var checkMateMoves = moveMap.get("checkMate");
        if (!checkMateMoves.isEmpty()) {
            makeRandomMove(checkMateMoves, gameData, game, outMap);
            return outMap;
        }
        var checkMoves = moveMap.get("check");
        if (!checkMoves.isEmpty()) {
            makeRandomMove(checkMoves, gameData, game, outMap);
            return outMap;
        }
        var takePieceMoves = moveMap.get("takePiece");
        if (!takePieceMoves.isEmpty()) {
            makeRandomMove(takePieceMoves, gameData, game, outMap);
            return outMap;
        }
        var regularMoves = moveMap.get("regular");
        if (!regularMoves.isEmpty()) {
            makeRandomMove(regularMoves, gameData, game, outMap);
            return outMap;
        }
        return outMap;

    }

    private void setAndSave(GameDTO gameData, Game game, ChessPiece piece, Move move, Map<String, Object> outMap) {
        int fromX = piece.position.x;
        int fromY = piece.position.y;
        int toX = move.position.x;
        int toY = move.position.y;
        piece.makeMove(toX, toY);
        MoveDTO moveData = moveService.createMoveDTO(piece, piece.getColor(), fromX, fromY, toX, toY);
        gameData.setPieces(piece.getBoard().getPieceData());
        Set<MoveDTO> moves = gameData.getMoves();
        moves.add(moveData);
        moveService.switchTurns(gameData);
        moveService.setPlayerInCheck(game, gameData, getComputerPlayer(gameData));
        gameRepository.save(gameData);
        sendMessageAndNotification(gameData);
        outMap.put("gameData", gameData);
        outMap.put("moveData", moveData);

    }

    private ChessPiece getRandomPiece(Map<ChessPiece, Set<Move>> map) {
        List<ChessPiece> keyList = new ArrayList<>(map.keySet());
        int randomIndex = random.nextInt(keyList.size());
        ChessPiece randomPiece = keyList.get(randomIndex);
        return randomPiece;
    }

    private Move getRandomMove(Set<Move> moves) {
        Move[] movesArray = moves.toArray(new Move[0]);
        int randomIndex = random.nextInt(movesArray.length);
        return movesArray[randomIndex];
    }

    private void makeRandomMove(Map<ChessPiece, Set<Move>> moves, GameDTO gameData, Game game,
            Map<String, Object> outMap) {
        ChessPiece piece = getRandomPiece(moves);
        Move move = getRandomMove(moves.get(piece));
        setAndSave(gameData, game, piece, move, outMap);
    }

    private void sendMessageAndNotification(GameDTO gameData) {
        UserDTO computer = getComputerPlayer(gameData);
        NotificationDTO notification = notificationService.createNotification(computer, gameData);
        messagingTemplate.convertAndSend("/topic/game/" + gameData.getId(),
                jsonService.toJSON(moveService.getMessageBody(gameData, notification)));

    }

    private UserDTO getComputerPlayer(GameDTO gameData) {
        return gameData.getPlayers().stream().filter(p -> p.getRole().equals(Role.AI)).findFirst()
                .orElseThrow();
    }

    public static void addMoveToSet(Map<String, Map<ChessPiece, Set<Move>>> map, ChessPiece piece, String key,
            Move move) {
        Map<ChessPiece, Set<Move>> subMap = map.get(key);
        Set<Move> set = subMap.get(piece);
        if (set == null) {
            set = new HashSet<>();
            subMap.put(piece, set);
        }
        set.add(move);

    }

    private Map<String, Map<ChessPiece, Set<Move>>> getMoveMap(Set<Move> allPossibleMoves) {
        Map<String, Map<ChessPiece, Set<Move>>> map = initializeMoveMap();
        addMovesToMap(allPossibleMoves, map);
        return map;
    }

    private Map<String, Map<ChessPiece, Set<Move>>> initializeMoveMap() {
        Map<String, Map<ChessPiece, Set<Move>>> map = new HashMap<>();
        map.put("checkMate", new HashMap<>());
        map.put("takePiece", new HashMap<>());
        map.put("check", new HashMap<>());
        map.put("regular", new HashMap<>());
        return map;
    }

    private void addMovesToMap(Set<Move> moves, Map<String, Map<ChessPiece, Set<Move>>> map) {
        for (Move move : moves) {
            ChessPiece piece = move.getPiece();
            Position pos = move.position;
            ChessBoard simulatedBoard = move.simulateMove(piece.getBoard());
            if (!move.isLegal(simulatedBoard)) {
                continue;
            }
            King playerKing = simulatedBoard.getOpponentKing(piece.getColor());
            if (playerKing.isInCheckMate()) {
                addMoveToSet(map, piece, "checkMate", move);
                break;
            }
            if (playerKing.isInCheck()) {
                addMoveToSet(map, piece, "check", move);
            }
            if (piece.getBoard().isSpaceOccupied(pos)) {
                addMoveToSet(map, piece, "takePiece", move);
            }
            addMoveToSet(map, piece, "regular", move);
        }
    }
}
