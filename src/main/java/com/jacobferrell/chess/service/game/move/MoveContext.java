package com.jacobferrell.chess.service.game.move;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.dto.MoveParams;
import com.jacobferrell.chess.game.Player;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.pieces.ChessPiece;
import com.jacobferrell.chess.game.pieces.Move;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.GamePiecePosition;
import com.jacobferrell.chess.model.MoveEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.service.game.GameService;
import lombok.Getter;

@Getter
public class MoveContext {

    private final User user;

    private final PieceColor playerColor;

    private final GameEntity gameEntity;

    private final ChessPiece piece;

    private final GamePiecePosition gamePiecePosition;

    private final Move move;

    private final ChessBoard board;

    private final MoveEntity moveEntity;

    private final boolean opponentInCheck;

    private final boolean opponentInCheckMate;

    public MoveContext(GameEntity gameEntity, MoveParams params) {
        this.gameEntity = gameEntity;
        this.user = SecurityUtils.getCurrentUser();

        Player.validate(gameEntity, user);

        this.move = Move.fromParams(gameEntity, params, user);
        this.piece = move.getPiece();
        this.playerColor = piece.getColor();
        this.board = piece.getBoard();
        this.moveEntity = MoveService.executeMove(
                gameEntity,
                move,
                params.promotion()
        );
        this.gamePiecePosition = moveEntity.getGamePiecePosition();
        this.opponentInCheck = board.getOpponentKing(playerColor).isInCheck();
        this.opponentInCheckMate = board.getOpponentKing(playerColor).isInCheckMate();
        complete();
    }

    public MoveContext(GameEntity gameEntity, Move move, User user) {
        this.gameEntity = gameEntity;
        this.user = user;
        this.move = move;

        Player.validate(gameEntity, user);

        this.piece = move.getPiece();
        this.playerColor = piece.getColor();
        this.board = piece.getBoard();
        this.moveEntity = MoveService.executeMove(
                gameEntity,
                move
        );
        this.gamePiecePosition = moveEntity.getGamePiecePosition();
        this.opponentInCheck = board.getOpponentKing(playerColor).isInCheck();
        this.opponentInCheckMate = board.getOpponentKing(playerColor).isInCheckMate();
        complete();
    }


    private void complete() {

        gameEntity.overwritePieces(
                board.getAllPieces().stream()
                        .map(ChessPiece::copyToGamePieceMapping)
                        .toList()
        );

        gameEntity.getMoves().add(moveEntity);

        gameEntity.switchTurns();

        if (opponentInCheck) {
            gameEntity.setPlayerInCheck(playerColor.enemy());
        } else {
            gameEntity.setPlayerInCheck(null);
        }

    }


}
