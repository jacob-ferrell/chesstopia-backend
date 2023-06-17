package com.jacobferrell.chess.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.MoveDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.pieces.ChessPiece;
import com.jacobferrell.chess.pieces.PieceColor;
import com.jacobferrell.chess.repository.GameRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class MoveCreationService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MoveService moveService;

    public Map<String, Object> makeMove(long gameId, int x0, int y0, int x1, int y1, HttpServletRequest request, String upgradeType) {
        GameDTO gameData = moveService.getGameById(gameId);
        UserDTO user = jwtService.getUserFromRequest(request);
        moveService.validateGameIncludesPlayer(gameData, user);
        moveService.validateGameIsNotOver(gameData);
        moveService.validateIsPlayersTurn(gameData, user);
        PieceColor playerColor = MoveService.getPlayerColor(gameData, user);
        // Convert game object from frontend into backend game object
        Game game = MoveService.createGameFromDTO(gameData);
        // Test that a piece has been selected and belongs to the current user
        ChessPiece selectedPiece = MoveService.getAndValidatePiece(x0, y0, game, user, playerColor);

        // Create move object and simulate the move to see if it is legal
        moveService.validateAndMakeMove(selectedPiece, x1, y1, upgradeType);
        // Set and save the board, moves, turn, and playerInCheck
        MoveDTO move = moveService.createMoveDTO(selectedPiece, playerColor, x0, y0, x1, y1);
        gameData.setPieces(selectedPiece.getBoard().getPieceData());

        Set<MoveDTO> moves = gameData.getMoves();
        moves.add(move);

        moveService.switchTurns(gameData);

        moveService.setPlayerInCheck(game, gameData, user);
        // End game as draw if king is lone piece and has moved >= 50 times

        if (game.board.isDraw()) {
            Map<String, Object> outMap = new HashMap<>();
            moveService.handleDraw(gameData, outMap);
            return outMap;
        }
        gameRepository.save(gameData);

        // Send message to websocket with updated game state and notification
        // so that if other player is connected, the notifcation can automatically
        // be marked as read
        moveService.sendMessageAndNotification(user, gameData, request);
        Map<String, Object> moveData = new HashMap<>();
        moveData.put("gameData", gameData);
        moveData.put("moveData", move);
        return moveData;

    }
}
