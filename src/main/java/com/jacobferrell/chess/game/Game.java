package com.jacobferrell.chess.game;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.GamePiecePosition;

import java.util.List;

import static com.jacobferrell.chess.game.Player.getPlayerFromUser;

public record Game(Player player1, Player player2, ChessBoard board) {

    public Game(Player player1, Player player2) {
        this(player1, player2, new ChessBoard());
    }

    public static Game createGameFromEntity(GameEntity data) {
        Player player1 = getPlayerFromUser(data.getWhitePlayer(), PieceColor.WHITE);
        Player player2 = getPlayerFromUser(data.getBlackPlayer(), PieceColor.BLACK);
        List<GamePiecePosition> pieces = data.getPieces();
        return new Game(player1, player2, new ChessBoard(pieces));
    }


}
