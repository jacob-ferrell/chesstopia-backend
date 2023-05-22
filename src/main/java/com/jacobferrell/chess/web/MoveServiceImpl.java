package com.jacobferrell.chess.web;

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
import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.Player;
import com.jacobferrell.chess.model.GameModel;
import com.jacobferrell.chess.model.GameRepository;
import com.jacobferrell.chess.model.MoveModel;
import com.jacobferrell.chess.model.Piece;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.pieces.ChessPiece;
import com.jacobferrell.chess.pieces.King;
import com.jacobferrell.chess.pieces.Move;
import com.jacobferrell.chess.pieces.PieceColor;

import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class MoveServiceImpl implements MoveService {
    
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtService jwtService;

    @Override
    public Set<Position> getPossibleMoves(long gameId, int x, int y) {
        // TODO: Add authentication to test if player is moving their own piece
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            throw new NotFoundException("Game not found with id: " + gameId);
        }
        GameModel gameData = optionalGame.get();
        Game game = createGameFromData(gameData);
        ChessPiece piece = game.board.getPieceAtPosition(x, y);
        if (piece == null) {
            throw new NotFoundException("Piece not found at position: " + x + "," + y);
        }
        return piece.removeMovesIntoCheck(piece.generatePossibleMoves());
    }

    @Override
    public Map<String, Object> makeMove(long gameId, int x0, int y0, int x1, int y1, HttpServletRequest request) {
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            throw new NotFoundException("Game not found with id: " + gameId);
        }
        User user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new AccessDeniedException("Current user could not be verified");
        }
        GameModel gameData = optionalGame.get();
        Set<User> players = gameData.getPlayers();
        // Test if current user is a player of the game
        if (!players.contains(user)) {
            throw new AccessDeniedException("Current user is not a player of game with id: " + gameId);
        }
        // Test if a winner has been declared, meaning the game is over
        if (gameData.getWinner() != null) {
            throw new IllegalArgumentException("Game with id: " + gameId + " is over and additional moves cannot be made");
        }
        // Test if it is the current user's turn
        boolean isUsersTurn = gameData.getCurrentTurn().equals(user);
        if (!isUsersTurn) {
            throw new IllegalArgumentException("User " + user.getEmail() + " is attempting to move out of turn");
        }

        PieceColor playerColor = gameData.getWhitePlayer().equals(user) ? PieceColor.WHITE : PieceColor.BLACK;
        // Convert game object from frontend into backend game object
        Game game = createGameFromData(gameData);
        // Test that a piece has been selected and belongs to the current user
        ChessPiece selectedPiece = game.board.getPieceAtPosition(x0, y0);
        if (selectedPiece == null) {
            throw new IllegalArgumentException("There exists no piece coordinates: x: " + x0 + ", y: " + y0);
        }
        if (!selectedPiece.getColor().equals(playerColor)) {
            throw new IllegalArgumentException("The piece at coordinates: x: " + x0 + ", y: " + y0 + " does not belong to user " + user.getEmail());
        }
        // Test if the requested move is possible for the selected piece
        Set<Position> possibleMoves = selectedPiece.generatePossibleMoves();
        if (!possibleMoves.stream().anyMatch(pos -> pos.equals(new Position(x1, y1)))) {
            throw new IllegalArgumentException("Moving " + selectedPiece.getName() + " at " + "coordinates: x: " + x0 + ", y: " + y0 + " to coordinates: x: " + x1 + ", y: " + y1 + " is not a valid move");

        }
        // Create move object and simulate the move to see if it is legal
        MoveModel move = MoveModel.builder().pieceType(selectedPiece.getName()).pieceColor(playerColor.toString())
                .fromX(x0).fromY(y0)
                .toX(x1).toY(y1).build();
        Move chessMove = getMoveFromData(selectedPiece, move);
        ChessBoard simulatedBoard = chessMove.simulateMove(game.board);
        if (!chessMove.isLegal(simulatedBoard)) {
            throw new IllegalArgumentException("The attempted move is not legal");
        }
        // Set and save the board, moves, turn, and playerInCheck
        selectedPiece.makeMove(x1, y1);
        System.out.println(game.board);
        gameData.setPieces(game.board.getPieceData());
        Set<MoveModel> moves = gameData.getMoves();
        if (moves == null) {
            moves = new HashSet<>();
            gameData.setMoves(moves);
        }
        moves.add(move);
        switchTurns(gameData);
        setPlayerInCheck(game, gameData, user);
        gameRepository.save(gameData);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, toJSON("moved"));
        gameRepository.findAll().forEach(System.out::println);
        Map<String, Object> moveData = new HashMap<>();
        moveData.put("gameData", gameData);
        moveData.put("moveData", move);
        return moveData;
    }

    private Game createGameFromData(GameModel data) {
        Player player1 = getPlayerFromUser(data.getWhitePlayer(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getBlackPlayer(), PieceColor.BLACK);
        Game game = new Game(player1, player2);
        Set<Piece> pieces = data.getPieces();
        game.board.setBoardFromData(pieces);
        return game;
    }

    private Player getPlayerFromUser(User user, PieceColor color) {
        Player player = new Player(user.getName(), color);
        return player;
    }

    private void switchTurns(GameModel gameData) {
        if (gameData.getCurrentTurn().equals(gameData.getWhitePlayer())) {
            gameData.setCurrentTurn(gameData.getBlackPlayer());
            return;
        }
        gameData.setCurrentTurn(gameData.getWhitePlayer());
    }

    private void setPlayerInCheck(Game game, GameModel gameData, User player) {
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

    private Move getMoveFromData(ChessPiece piece, MoveModel move) {
        Move chessMove = new Move(piece, new Position(move.getToX(), move.getToY()));
        return chessMove;
    }

    private String toJSON(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(object);
            return json;
        } catch (JsonProcessingException e) {
            System.out.println(e);
            return null;
        }
    }
}
