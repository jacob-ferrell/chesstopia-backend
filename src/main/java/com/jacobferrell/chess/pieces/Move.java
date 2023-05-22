package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;

public class Move {
    protected ChessPiece piece;
    public Position position;

    public Move(ChessPiece piece, Position to) {
        this.piece = piece;
        this.position = to;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public ChessBoard simulateMove(ChessBoard board) {
        ChessBoard clonedBoard = board.getClone();
        ChessPiece pieceToMove = piece.getClone(clonedBoard);
        clonedBoard.setPieceAtPosition(position.x, position.y, pieceToMove);
        return clonedBoard;
    }

    public boolean isLegal(ChessBoard board) {
        return board.hasBothKings() && !board.getPlayerKing(piece.color).isInCheck(); 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getName() + ": [" + position.y + ", " + position.x + "]");
        return sb.toString();
    }
    
}
