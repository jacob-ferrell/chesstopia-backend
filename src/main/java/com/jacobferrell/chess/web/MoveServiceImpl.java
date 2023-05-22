package com.jacobferrell.chess.web;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.chessboard.Position;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.model.GameModel;
import com.jacobferrell.chess.model.GameRepository;
import com.jacobferrell.chess.pieces.ChessPiece;

@Service
public class MoveServiceImpl implements MoveService {
    
    private final GameRepository gameRepository;

    @Autowired
    public MoveServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Set<Position> getPossibleMoves(long gameId, int x, int y) {
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
}
