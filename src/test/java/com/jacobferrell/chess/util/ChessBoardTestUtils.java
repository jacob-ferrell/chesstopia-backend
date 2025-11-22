package com.jacobferrell.chess.util;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.King;
import com.jacobferrell.chess.game.pieces.Pawn;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.game.pieces.Rook;

public class ChessBoardTestUtils {

    private ChessBoardTestUtils(){}

    public static ChessBoard clearBoard() {
        ChessBoard board = new ChessBoard();
        board.clear();
        return board;
    }

    public void setBoardOneMoveFromCheckmate(ChessBoard board) {
        board.clear();
        board.addPiece(new Rook(PieceColor.WHITE, new Position(1, 1), board));
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 1), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
    }

    public void setBoardOneMoveFromComputerPromotion(ChessBoard board) {
        board.clear();
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(0, 1), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
    }

    public static ChessBoard withKingsOnly() {
        ChessBoard board = clearBoard();
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        return board;
    }


}
