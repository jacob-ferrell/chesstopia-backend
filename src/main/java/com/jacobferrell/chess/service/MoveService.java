package com.jacobferrell.chess.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jacobferrell.chess.chessboard.ChessBoard;
import com.jacobferrell.chess.chessboard.Position;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.Player;
import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.MoveDTO;
import com.jacobferrell.chess.model.NotificationDTO;
import com.jacobferrell.chess.model.PieceDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.pieces.ChessPiece;
import com.jacobferrell.chess.pieces.King;
import com.jacobferrell.chess.pieces.Move;
import com.jacobferrell.chess.pieces.PieceColor;
import com.jacobferrell.chess.repository.GameRepository;

import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class MoveService {
    
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private JsonService jsonService;

    public Set<Position> getPossibleMoves(long gameId, int x, int y, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        GameDTO gameData = getGameById(gameId);
        Game game = createGameFromDTO(gameData);
        ChessPiece piece = getAndValidatePiece(x, y, game, user, getPlayerColor(gameData, user));
        return piece.removeMovesIntoCheck(piece.generatePossibleMoves());
    }

    public Map<String, Object> makeMove(long gameId, int x0, int y0, int x1, int y1, HttpServletRequest request) {
        GameDTO gameData = getGameById(gameId);
        UserDTO user = jwtService.getUserFromRequest(request);
        validateGameIncludesPlayer(gameData, user);
        validateGameIsNotOver(gameData);
        validateIsPlayersTurn(gameData, user);

        PieceColor playerColor = getPlayerColor(gameData, user);
        // Convert game object from frontend into backend game object
        Game game = createGameFromDTO(gameData);
        // Test that a piece has been selected and belongs to the current user
        ChessPiece selectedPiece = getAndValidatePiece(x0, y0, game, user, playerColor);
        // Create move object and simulate the move to see if it is legal
        validateAndMakeMove(selectedPiece, x1, y1, game.board);
        // Set and save the board, moves, turn, and playerInCheck
        MoveDTO move = createMoveDTO(selectedPiece, playerColor, x0, y0, x1, y1);
        gameData.setPieces(game.board.getPieceData());
        Set<MoveDTO> moves = gameData.getMoves();
        moves.add(move);
        switchTurns(gameData);
        setPlayerInCheck(game, gameData, user);
        gameRepository.save(gameData);
        //Send message to websocket with updated game state and notification
        //so that if other player is connected, the notifcation can automatically 
        //be marked as read
        NotificationDTO notification = notificationService.createNotification(user, gameData);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, jsonService.toJSON(getMessageBody(gameData, notification)));
        Map<String, Object> moveData = new HashMap<>();
        moveData.put("gameData", gameData);
        moveData.put("moveData", move);
        return moveData;
    }

    private Game createGameFromDTO(GameDTO data) {
        Player player1 = getPlayerFromUser(data.getWhitePlayer(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getBlackPlayer(), PieceColor.BLACK);
        Game game = new Game(player1, player2);
        Set<PieceDTO> pieces = data.getPieces();
        game.board.setBoardFromData(pieces);
        return game;
    }

    private Player getPlayerFromUser(UserDTO user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

    private void switchTurns(GameDTO gameData) {
        if (gameData.getCurrentTurn().equals(gameData.getWhitePlayer())) {
            gameData.setCurrentTurn(gameData.getBlackPlayer());
            return;
        }
        gameData.setCurrentTurn(gameData.getWhitePlayer());
    }

    private void setPlayerInCheck(Game game, GameDTO gameData, UserDTO player) {
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

    private Map<String, Object> getMessageBody(GameDTO game, NotificationDTO notification) {
        Map<String, Object> body = new HashMap<>();
        body.put("game", game);
        body.put("notification", notification);
        return body;
    }

    private void validateGameIncludesPlayer(GameDTO game, UserDTO user) {
        if (!game.getPlayers().contains(user)) {
            throw new AccessDeniedException("User with id: " + user.getId() + " is not a player of game with id: " + game.getId());
        }
    }

    private void validateIsPlayersTurn(GameDTO game, UserDTO user) {
        if (!game.getCurrentTurn().equals(user)) {
            throw new IllegalArgumentException("User " + user.getEmail() + " is attempting to move out of turn");
        }
    }

    private void validateGameIsNotOver(GameDTO game) {
        if (game.getWinner() != null) {
            throw new IllegalArgumentException("Game with id: " + game.getId() + " is over and additional moves cannot be made");
        }
    }

    private ChessPiece getAndValidatePiece(int x0, int y0, Game game, UserDTO user, PieceColor playerColor) {
        ChessPiece piece = game.board.getPieceAtPosition(x0, y0);
        if (piece == null) {
            throw new IllegalArgumentException("There exists no piece coordinates: x: " + x0 + ", y: " + y0);
        }
        if (!piece.getColor().equals(playerColor)) {
            throw new IllegalArgumentException("The piece at coordinates: x: " + x0 + ", y: " + y0 + " does not belong to user " + user.getEmail());
        }
        return piece;
    }

    private GameDTO getGameById(long id) {
        Optional<GameDTO> optionalGame = gameRepository.findById(id);
        if (!optionalGame.isPresent()) {
            throw new NotFoundException("Game not found with id: " + id);
        }
        return optionalGame.get();
    }

    private PieceColor getPlayerColor(GameDTO game, UserDTO user) {
        return game.getWhitePlayer().equals(user) ? PieceColor.WHITE : PieceColor.BLACK;
    }

    private void validateAndMakeMove(ChessPiece piece, int x1, int y1, ChessBoard board) {
        Set<Position> possibleMoves = piece.generatePossibleMoves();
        if (!possibleMoves.stream().anyMatch(pos -> pos.equals(new Position(x1, y1)))) {
            throw new IllegalArgumentException("Moving " + piece.getName() + " at " + "coordinates: x: " + piece.position.x + ", y: " + piece.position.y + " to coordinates: x: " + x1 + ", y: " + y1 + " is not a valid move");

        }
        Move chessMove = new Move(piece, new Position(x1, y1));
        ChessBoard simulatedBoard = chessMove.simulateMove(board);
        if (!chessMove.isLegal(simulatedBoard)) {
            throw new IllegalArgumentException("The attempted move is not legal");
        }
        piece.makeMove(x1, y1);
    }

    private MoveDTO createMoveDTO(ChessPiece piece, PieceColor playerColor, int x0, int y0, int x1, int y1) {
        MoveDTO move = MoveDTO.builder().pieceType(piece.getName()).pieceColor(playerColor.toString())
                .fromX(x0).fromY(y0)
                .toX(x1).toY(y1).build();
        return move;
    }
    
}
