package com.jacobferrell.chess.pieces;

import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public abstract class ChessPiece {
    protected PieceColor color;
    protected int xPosition;
    protected int yPosition;
    protected int counter;
    protected boolean hasMoved = false;
    protected ChessBoard board;

    public ChessPiece(PieceColor color, Position pos, ChessBoard board) {
        this.color = color;
        this.xPosition = pos.getX();
        this.yPosition = pos.getY();
        this.board = board;
    }

    public boolean isValidMove(int x, int y) {
        if (Math.min(x, y) < 0 || Math.max(x, y) > 7) {
            return false;
        }
        counter++;
        if (this instanceof King && counter > 1) {
            counter = 0;
            return false;
        }
        if (!board.isSpaceOccupied(x, y)) {
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
        hasMoved = true;
    }

    public boolean getHasMoved() {
        return hasMoved;
    }

    // Add all possible horizontal moves
    public Set<Position> getHorizontalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        int y = yPosition;
        for (int x = xPosition + 1; isValidMove(x, y); x++) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        for (int x = xPosition - 1; isValidMove(x, y); x--) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        return possibleMoves;
    }

    // Add all possible vertical moves
    public Set<Position> getVerticalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        int x = xPosition;
        for (int y = yPosition + 1; isValidMove(x, y); y++) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        for (int y = yPosition - 1; isValidMove(x, y); y--) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        return possibleMoves;
    }

    // Add all possible diagonal moves
    public Set<Position> getDiagonalMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        for (int x = xPosition + 1, y = yPosition + 1; isValidMove(x, y); x++, y++) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        for (int x = xPosition + 1, y = yPosition - 1; isValidMove(x, y); x++, y--) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        for (int x = xPosition - 1, y = yPosition + 1; isValidMove(x, y); x--, y++) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }

        for (int x = xPosition - 1, y = yPosition - 1; isValidMove(x, y); x--, y--) {
            possibleMoves.add(new Position(x, y));
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
        }
        return possibleMoves;
    }

    public void movePiece(int x, int y) {
        board.setPieceAtPosition(x, y, this);
        setXPosition(x);
        setYPosition(y);
        setHasMoved();
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

    public ChessBoard getBoard() {
        return this.board;
    }

    public int getXPosition() {
        return this.xPosition;
    }

    public int getYPosition() {
        return this.yPosition;
    }

    public void setXPosition(int x) {
        this.xPosition = x;
    }

    public void setYPosition(int y) {
        this.yPosition = y;
    }

}
