package com.jacobferrell.chess.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

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
import com.jacobferrell.chess.pieces.Pawn;
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

    @Autowired
    private GameService gameService;

    public Set<Position> getPossibleMoves(long gameId, int x, int y, HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        GameDTO gameData = getGameById(gameId);
        Game game = createGameFromDTO(gameData);
        ChessPiece piece = getAndValidatePiece(x, y, game, user, getPlayerColor(gameData, user));
        gameService.showPlayerIsConnectedToGame(gameId, request, true);
        return getAllPossibleMovesForPiece(piece).stream().map(m -> m.position)
                .collect(Collectors.toSet());
    }

    private Set<Move> getAllPossibleMovesForPiece(ChessPiece piece) {
        return Move.removeMovesIntoCheck(piece.getBoard().getAllPossibleMoves().get(piece.color)).stream()
                .filter(move -> move.piece.equals(piece))
                .collect(Collectors.toSet());
    }

    public void sendMessageAndNotification(UserDTO user, GameDTO gameData, HttpServletRequest request) {
        long gameId = gameData.getId();
        NotificationDTO notification = notificationService.createNotification(user, gameData);
        messagingTemplate.convertAndSend("/topic/game/" + gameId,
                jsonService.toJSON(getMessageBody(gameData, notification)));
        gameService.showPlayerIsConnectedToGame(gameId, request, true);

    }

    public static Game createGameFromDTO(GameDTO data) {
        Player player1 = getPlayerFromUser(data.getWhitePlayer(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getBlackPlayer(), PieceColor.BLACK);
        Game game = new Game(player1, player2);
        Set<PieceDTO> pieces = data.getPieces();
        game.board.setBoardFromData(pieces);
        return game;
    }

    public static Player getPlayerFromUser(UserDTO user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

    public void switchTurns(GameDTO gameData) {
        if (gameData.getCurrentTurn().equals(gameData.getWhitePlayer())) {
            gameData.setCurrentTurn(gameData.getBlackPlayer());
            return;
        }
        gameData.setCurrentTurn(gameData.getWhitePlayer());
    }

    public void setPlayerInCheck(Game game, GameDTO gameData, UserDTO player) {
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
        gameData.setGameOver(true);
    }

    public Map<String, Object> getMessageBody(GameDTO game, NotificationDTO notification) {
        Map<String, Object> body = new HashMap<>();
        body.put("game", game);
        body.put("notification", notification);
        return body;
    }

    public void validateGameIncludesPlayer(GameDTO game, UserDTO user) {
        if (!game.getPlayers().contains(user)) {
            throw new AccessDeniedException(
                    "User with id: " + user.getId() + " is not a player of game with id: " + game.getId());
        }
    }

    public void validateIsPlayersTurn(GameDTO game, UserDTO user) {
        if (!game.getCurrentTurn().equals(user)) {
            throw new IllegalArgumentException("User " + user.getEmail() + " is attempting to move out of turn");
        }
    }

    public void validateGameIsNotOver(GameDTO game) {
        if (game.getGameOver()) {
            throw new IllegalArgumentException(
                    "Game with id: " + game.getId() + " is over and additional moves cannot be made");
        }
    }

    public static ChessPiece getAndValidatePiece(int x0, int y0, Game game, UserDTO user, PieceColor playerColor) {
        ChessPiece piece = game.board.getPieceAtPosition(new Position(x0, y0));
        if (piece == null) {
            throw new IllegalArgumentException("There exists no piece coordinates: x: " + x0 + ", y: " + y0);
        }
        if (!piece.color.equals(playerColor)) {
            throw new IllegalArgumentException("The piece at coordinates: x: " + x0 + ", y: " + y0
                    + " does not belong to user " + user.getEmail());
        }
        return piece;
    }

    public GameDTO getGameById(long id) {
        Optional<GameDTO> optionalGame = gameRepository.findById(id);
        if (!optionalGame.isPresent()) {
            throw new NotFoundException("Game not found with id: " + id);
        }
        return optionalGame.get();
    }

    public static PieceColor getPlayerColor(GameDTO game, UserDTO user) {
        return game.getWhitePlayer().equals(user) ? PieceColor.WHITE : PieceColor.BLACK;
    }

    public void validateAndMakeMove(ChessPiece piece, int x1, int y1, String upgradeType) {
        Set<Move> possibleMoves = getAllPossibleMovesForPiece(piece);
        Position movePosition = new Position(x1, y1);
        Move chessMove = possibleMoves.stream().filter(move -> move.position.equals(movePosition)).findFirst()
                .orElse(null);
        if (chessMove == null) {
            throw new IllegalArgumentException(
                    "Moving " + piece.getName() + " at " + "coordinates: x: " + piece.position.x + ", y: "
                            + piece.position.y + " to coordinates: x: " + x1 + ", y: " + y1 + " is not a valid move");
        }
        if (isPromotion(piece, y1)) {
            Position from = piece.position;
            piece = (ChessPiece) piece.getBoard().createNewPiece(upgradeType, new Position(x1, y1), piece.color);
            handlePromotion(piece, from);
        }
        piece.makeMove(movePosition);
    }

    public static boolean isPromotion(ChessPiece piece, int y) {
        return piece instanceof Pawn && ((piece.color.equals(PieceColor.WHITE) && y == 0)
                || (piece.color.equals(PieceColor.BLACK) && y == 7));
    }

    public MoveDTO createMoveDTO(ChessPiece piece, PieceColor playerColor, int x0, int y0, int x1, int y1) {
        MoveDTO move = MoveDTO.builder().pieceType(piece.getName()).pieceColor(playerColor.toString())
                .fromX(x0).fromY(y0)
                .toX(x1).toY(y1).build();
        return move;
    }

    public static void handlePromotion(ChessPiece piece, Position from) {
        ChessBoard board = piece.getBoard();
        board.removePieceAtPosition(from);
        board.setPieceAtPosition(piece.position, piece);
    }

    public void handleDraw(GameDTO gameData, Map<String, Object> outMap) {
        gameData.setGameOver(true);
        gameRepository.save(gameData);
        outMap.put("gameData", gameData);
        outMap.put("moveData", null);
    }

}
