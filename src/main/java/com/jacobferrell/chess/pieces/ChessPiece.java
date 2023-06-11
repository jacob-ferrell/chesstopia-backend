package com.jacobferrell.chess.pieces;

import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public abstract class ChessPiece {
    public PieceColor color;
    protected int counter;
    public boolean hasMoved;
    public Position position;
    protected ChessBoard board;

    public ChessPiece(PieceColor color, Position pos, ChessBoard board) {
        this.color = color;
        this.position = pos;
        this.board = board;
        this.hasMoved = false;
    }

    public boolean isValidMove(int x, int y) {
        if (Math.min(x, y) < 0 || Math.max(x, y) > 7) {
            return false;
        }
        if (!board.isSpaceOccupied(new Position(x, y))) {
            return true;
        }
        ChessPiece otherPiece = board.getPieceAtPosition(x, y);
        if (isEnemyPiece(otherPiece)) {
            return true;
        }
        return false;
    }

    public abstract ChessPiece getClone(ChessBoard clonedBoard);

    public abstract char getSymbol();

    public abstract Set<Position> generatePossibleMoves();

    public abstract String getName();

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    // Add all possible horizontal moves
    public Set<Position> getHorizontalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        int y = position.y;
        for (int x = position.x + 1; isValidMove(x, y); x++) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        for (int x = position.x - 1; isValidMove(x, y); x--) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        return possibleMoves;
    }

    // Add all possible vertical moves
    public Set<Position> getVerticalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        int x = position.x;
        for (int y = position.y + 1; isValidMove(x, y); y++) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        for (int y = position.y - 1; isValidMove(x, y); y--) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        return possibleMoves;
    }

    // Add all possible diagonal moves
    public Set<Position> getDiagonalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        for (int x = position.x + 1, y = position.y + 1; isValidMove(x, y); x++, y++) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        for (int x = position.x + 1, y = position.y - 1; isValidMove(x, y); x++, y--) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        for (int x = position.x - 1, y = position.y + 1; isValidMove(x, y); x--, y++) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }

        for (int x = position.x - 1, y = position.y - 1; isValidMove(x, y); x--, y--) {
            Position pos = new Position(x, y);
            possibleMoves.add(pos);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
        }
        return possibleMoves;
    }

    public void makeMove(int x, int y) {
        if (!board.isSpaceOccupied(new Position(x, y)) || isEnemyPiece(board.getPieceAtPosition(x, y))) {
            board.setPieceAtPosition(x, y, this);
            return;
        }
        ChessPiece rook = board.getPieceAtPosition(x, y);
        if (!(rook instanceof Rook)) {
            return;
        }
        King king = board.getPlayerKing(color);
        board.setPieceAtPosition(king.position.x, king.position.y, rook);
        board.setPieceAtPosition(x, y, king);
    }

    public void movePiece(int x, int y) {
        board.setPieceAtPosition(x, y, this);
        position = new Position(x, y);
        setHasMoved();

    }

    public Set<Position> removeMovesIntoCheck(Set<Position> moves) {
        return moves.stream().filter(pos -> {
            Move move = new Move(this, pos);
            ChessBoard simulatedBoard = move.simulateMove(board);
            return move.isLegal(simulatedBoard);
        }).collect(Collectors.toSet());
    }

    public PieceColor getColor() {
        return this.color;
    }

    public boolean isEnemyPiece(ChessPiece otherPiece) {
        if (otherPiece.getColor() == getColor()) {
            return false;
        }
        return true;
    }

    public boolean canCastle() {
        if (!(this instanceof Rook)) {
            return false;
        }
        Set<Rook> castleRooks = board.getCastleRooks(color);
        for (Rook rook : castleRooks) {
            if (this.equals(rook)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return getName() + ", " + getColor() + " [" + position.y + ", " + position.x + "]";
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public void setPosition(int x, int y) {
        this.position = new Position(x, y);
    }

}
