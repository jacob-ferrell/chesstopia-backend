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
        moveService.validateGameIsNotOver(gameData);
        moveService.validateIsPlayersTurn(gameData, computer);
        PieceColor computerColor = moveService.getPlayerColor(gameData, computer);
        Game game = moveService.createGameFromDTO(gameData);
        Set<Move> allPossibleComputerMoves = Move.removeMovesIntoCheck(game.board.getAllPossibleMoves().get(computerColor));
        Map<String, Set<Move>> moveMap = getMoveMap(allPossibleComputerMoves);
        King computerKing = game.board.getPlayerKing(computerColor);
        boolean computerKingIsInCheck = computerKing.isInCheck();
        if (computerKingIsInCheck && allPossibleComputerMoves.isEmpty()) {
            handleCheckmate(gameData, computer, outMap);
            return outMap;
        }
        if (computerKingIsInCheck) {
            handleCheck(gameData, game, computerColor, allPossibleComputerMoves, outMap);
            return outMap;
        }
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
            makeRandomMove(getMovesWithHighestRankedPiece(game, takePieceMoves), gameData, game, outMap);
            return outMap;
        }
        var regularMoves = moveMap.get("regular");
        if (!regularMoves.isEmpty()) {
            makeRandomMove(regularMoves, gameData, game, outMap);
            return outMap;
        }
        return outMap;

    }

    private void setAndSave(GameDTO gameData, Game game, Move move, Map<String, Object> outMap) {
        ChessPiece piece = move.piece;
        int fromX = piece.position.x;
        int fromY = piece.position.y;
        int toX = move.position.x;
        int toY = move.position.y;
        piece.makeMove(new Position(toX, toY));
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

    private void handleCheck(GameDTO gameData, Game game, PieceColor color, Set<Move> possibleMoves,
            Map<String, Object> outMap) {
        Move move = getRandomMove(possibleMoves);
        setAndSave(gameData, game, move, outMap);
    }

    private void handleCheckmate(GameDTO gameData, UserDTO computer, Map<String, Object> outMap) {
        gameData.setWinner(gameData.getPlayers().stream().filter(u -> !u.equals(computer)).findFirst().orElseThrow());
        gameRepository.save(gameData);
        outMap.put("gameData", gameData);
        outMap.put("moveData", null);
    }

    private Set<Move> getMovesWithHighestRankedPiece(Game game, Set<Move> moves) {
        int maxRank = 0;
        for (Move move : moves) {
            ChessPiece pieceToTake = game.board.getPieceAtPosition(move.position);
            maxRank = Math.max(pieceToTake.rank, maxRank);
        }
        System.out.println("Max rank: " + maxRank);
        final int finalMaxRank = maxRank;
        Set<Move> filtered = moves.stream().filter(m -> {
            ChessPiece pieceToTake = game.board.getPieceAtPosition(m.position);
            return pieceToTake.rank == finalMaxRank;
        }).collect(Collectors.toSet());
        System.out.println(filtered);
        return filtered;
    }

    private ChessPiece getRandomPiece(Set<Move> moves) {
        Set<ChessPiece> pieceSet = moves.stream().map(m -> m.piece).collect(Collectors.toSet());
        List<ChessPiece> pieceList = new ArrayList<>(pieceSet);
        int randomIndex = random.nextInt(pieceList.size());
        ChessPiece randomPiece = pieceList.get(randomIndex);
        return randomPiece;
    }

    private Move getRandomMove(Set<Move> moves) {
        ChessPiece piece = getRandomPiece(moves);
        Set<Move> movesFilteredByPiece = moves.stream().filter(m -> m.piece.equals(piece))
                .collect(Collectors.toSet());
        Move[] movesArray = movesFilteredByPiece.toArray(new Move[0]);
        int randomIndex = random.nextInt(movesArray.length);
        return movesArray[randomIndex];
    }

    private void makeRandomMove(Set<Move> moves, GameDTO gameData, Game game,
            Map<String, Object> outMap) {
        Move move = getRandomMove(moves);
        setAndSave(gameData, game, move, outMap);
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

    public static void addMoveToSet(Map<String, Set<Move>> map, Move move, String key) {
        Set<Move> set = map.get(key);
        set.add(move);
    }

    private Map<String, Set<Move>> getMoveMap(Set<Move> allPossibleMoves) {
        Map<String, Set<Move>> map = initializeMoveMap();
        addMovesToMap(allPossibleMoves, map);
        return map;
    }

    private Map<String, Set<Move>> initializeMoveMap() {
        Map<String, Set<Move>> map = new HashMap<>();
        map.put("checkMate", new HashSet<>());
        map.put("takePiece", new HashSet<>());
        map.put("check", new HashSet<>());
        map.put("regular", new HashSet<>());
        return map;
    }

    private void addMovesToMap(Set<Move> moves, Map<String, Set<Move>> map) {
        for (Move move : moves) {
            ChessPiece piece = move.piece;
            Position pos = move.position;
            ChessBoard simulatedBoard = move.simulateMove();
            King playerKing = simulatedBoard.getOpponentKing(piece.getColor());
            boolean inCheck = playerKing.isInCheck();
            if (inCheck && playerKing.isInCheckMate()) {
                addMoveToSet(map, move, "checkMate");
                break;
            }
            if (inCheck) {
                addMoveToSet(map, move, "check");
            }
            if (piece.getBoard().isSpaceOccupied(pos)) {
                addMoveToSet(map, move, "takePiece");
            }
            addMoveToSet(map, move, "regular");
        }
    }
}
