package com.jacobferrell.chess.game;

import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;

public class Turn {
    protected Player player;
    protected Move move;
    protected ChessBoard board;

    public Turn(Player player, Move move, ChessBoard board) {
        this.player = player;
        this.move = move;
        this.board = board;

    }

    public ChessBoard simulateMove() {
        ChessBoard clonedBoard = board.getClone();
        ChessPiece pieceToMove = move.getPiece().getClone(clonedBoard);
        Position to = move.position;
        clonedBoard.setPieceAtPosition(to.x, to.y, pieceToMove);
        return clonedBoard;
    }

    private boolean moveIsLegal() {
        ChessBoard simulatedBoard = simulateMove();
        if (!simulatedBoard.hasBothKings()) {
            return false;
        }
        King playerKing = simulatedBoard.getPlayerKing(player.getColor());
        return !playerKing.isInCheck();
    }
}
