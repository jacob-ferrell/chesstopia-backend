package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;

public class Move {
    protected ChessPiece piece;
    protected Position to;
    private Position from;

    public Move(ChessPiece piece, Position to) {
        this.piece = piece;
        this.to = to;
        this.from = new Position(piece.getXPosition(), piece.getYPosition());
    }

    public ChessPiece getPiece() {
        return piece;
    }

    public Position getToPosition() {
        return to;
    }

    public Position getFromPosition() {
        return from;
    }

    public ChessBoard simulateMove(ChessBoard board) {
        ChessBoard clonedBoard = board.getClone();
        ChessPiece pieceToMove = piece.getClone(clonedBoard);
        clonedBoard.setPieceAtPosition(to.x, to.y, pieceToMove);
        clonedBoard.setPositionToNull(from.x, from.y);
        return clonedBoard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getName() + ": [" + to.getY() + ", " + to.getX() + "]");
        return sb.toString();
    }
    
}
