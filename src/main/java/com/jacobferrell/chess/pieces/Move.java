package com.jacobferrell.chess.pieces;
import java.util.Set;
import java.util.stream.Collectors;

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

    public ChessBoard simulateMove() {
        ChessBoard clonedBoard = piece.board.getClone();
        ChessPiece pieceToMove = clonedBoard.getPieceAtPosition(piece.position);
        clonedBoard.setPieceAtPosition(position, pieceToMove);
        return clonedBoard;
    }

    public boolean isLegal(ChessBoard board) {
        return board.hasBothKings() && !board.getPlayerKing(piece.color).isInCheck(); 
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(piece.getColor() + " " + piece.getName() + ": " + convertToChessCoordinates(position));
        return sb.toString();
    }

    public static Set<Move> removeMovesIntoCheck(Set<Move> moves) {
        return moves.stream().filter(move -> {
            return move.isLegal(move.simulateMove());
        }).collect(Collectors.toSet());
    }

    public static String convertToChessCoordinates(Position position) {
        int x = position.x;
        int y = position.y;
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    
        char xChar = (char) ('A' + x);
        int yInt = 8 - y;
    
        return Character.toString(xChar) + yInt;
    }

    /* @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        Move m = (Move) o;
        return m.piece.equals(piece) && m.position.equals(position);
    } */
    
}
