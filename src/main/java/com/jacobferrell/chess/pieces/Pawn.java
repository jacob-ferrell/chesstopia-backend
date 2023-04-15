package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public class Pawn extends ChessPiece {
    private char SYMBOL = '♙';
    public String name = "PAWN";

    public Pawn(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♙' : '♟';
    }

    @Override
    public char getSymbol() {
        return SYMBOL;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        Pawn clone = new Pawn(color, new Position(xPosition, yPosition), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Position> generatePossibleMoves() {
        Set<Position> possibleMoves = new HashSet<>();
        int yMultiplier = getColor() == PieceColor.WHITE ? -1 : 1;
        possibleMoves = getPawnDiagonalMoves(possibleMoves, yMultiplier);
        possibleMoves = getPawnVerticalMoves(possibleMoves, yMultiplier);
        return possibleMoves;
    }

    private Set<Position> getPawnDiagonalMoves(Set<Position> possibleMoves, int yMultiplier) {
        int currentX = getXPosition();
        int currentY = getYPosition();
        for (int x = currentX - 1; x < currentX + 2 && x < 8; x++) {
            int y = currentY + (1 * yMultiplier);
            if (x < 0 || x == currentX || !board.isSpaceOccupied(x, y)) {
                continue;
            }
            ChessPiece otherPiece = board.getPieceAtPosition(x, y);
            if (isEnemyPiece(otherPiece)) {
                possibleMoves.add(new Position(x, y));
            }

            
        }
        return possibleMoves;
    }

    private Set<Position> getPawnVerticalMoves(Set<Position> possibleMoves, int yMultiplier) {
        int currentX = getXPosition();
        int currentY = getYPosition();
        int maxSpaces = getHasMoved() ? 1 : 2;
        int yMovement = 1 * yMultiplier;
        int x = currentX;
        int spacesMoved = 0;
        for (int y = currentY + yMovement; y < 8 && y > -1 && spacesMoved < maxSpaces; y += yMovement) {
            spacesMoved++;
            if (board.isSpaceOccupied(x, y)) {
                break;
            }
            possibleMoves.add(new Position(x, y));
        }
        return possibleMoves;
    }

    
    
}
