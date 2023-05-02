package com.jacobferrell.chess.web;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.game.*;
import com.jacobferrell.chess.pieces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class MoveController {

    private final Logger log = LoggerFactory.getLogger(GameController.class);
    private GameRepository gameRepository;
    private JwtService jwtService;

    public MoveController(GameRepository gameRepository, JwtService jwtService) {
        this.gameRepository = gameRepository;
        this.jwtService = jwtService;
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/game/{gameId}/possible-moves")
    ResponseEntity<?> getPossibleMoves(@PathVariable Long gameId, @RequestParam int x, @RequestParam int y) {
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // TODO: Add authentication to test if player is moving their own piece
        GameModel gameData = optionalGame.get();
        Game game = createGameFromData(gameData);
        if (!game.board.isSpaceOccupied(x, y)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ChessPiece piece = game.board.getPieceAtPosition(x, y);
        Set<Position> possibleMoves = piece.removeMovesIntoCheck(piece.generatePossibleMoves());
        Map<String, Set<Position>> responseBody = new HashMap<>();
        responseBody.put("possibleMoves", possibleMoves);
        return ResponseEntity.ok().body(responseBody);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/game/{gameId}/move")
    ResponseEntity<GameModel> makeMove(@PathVariable Long gameId, @RequestParam int x0, @RequestParam int y0,
            @RequestParam int x1, @RequestParam int y1, HttpServletRequest request)
            throws URISyntaxException {
        // TODO: add authentication to see if game and piece belong to player
        Optional<GameModel> optionalGame = gameRepository.findById(gameId);
        if (!optionalGame.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User user = jwtService.getUserFromRequest(request);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        GameModel gameData = optionalGame.get();
        Set<User> players = gameData.getPlayers();
        // Test if current user is a player of the game
        if (!players.contains(user)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        // Test if a winner has been declared, meaning the game is over
        if (gameData.getWinner() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Test if it is the current user's turn
        boolean isUsersTurn = gameData.getCurrentTurn().equals(user);
        if (!isUsersTurn) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        PieceColor playerColor = gameData.getWhitePlayer().equals(user) ? PieceColor.WHITE : PieceColor.BLACK;

        log.info("Request to create move");
        // Convert game object from frontend into backend game object
        Game game = createGameFromData(gameData);
        // Test that a piece has been selected and belongs to the current user
        if (!game.board.isSpaceOccupied(x0, y0)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        ChessPiece selectedPiece = game.board.getPieceAtPosition(x0, y0);
        if (!selectedPiece.getColor().equals(playerColor)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Test if the requested move is possible for the selected piece
        Set<Position> possibleMoves = selectedPiece.generatePossibleMoves();
        if (!possibleMoves.stream().anyMatch(pos -> pos.equals(new Position(x1, y1)))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        MoveModel move = MoveModel.builder().pieceType(selectedPiece.getName()).pieceColor(playerColor.toString())
                .fromX(x0).fromY(y0)
                .toX(x1).toY(y1).build();
        Move chessMove = getMoveFromData(selectedPiece, move);
        ChessBoard simulatedBoard = chessMove.simulateMove(game.board);
        if (!simulatedBoard.hasBothKings() || simulatedBoard.getPlayerKing(selectedPiece.getColor()).isInCheck()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        game.board.setPieceAtPosition(x1, y1, selectedPiece);
        game.board.setPositionToNull(x0, y0);
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
        gameRepository.findAll().forEach(System.out::println);
        return ResponseEntity.created(new URI("/api/game/" + gameData.getId() + "/move/" + move.getId()))
                .body(gameData);
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
}