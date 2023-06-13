package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.*;
import java.util.Set;
import java.util.HashSet;

public class Pawn extends ChessPiece {
    public String name = "PAWN";

    public Pawn(PieceColor color, Position pos, ChessBoard board) {
        super(color, pos, board);
        this.SYMBOL = color == PieceColor.WHITE ? '♙' : '♟';
        this.rank = 6;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ChessPiece getClone(ChessBoard board) {
        Pawn clone = new Pawn(color, new Position(position.x, position.y), board);
        clone.hasMoved = hasMoved;
        return clone;
    }

    @Override
    public Set<Move> generatePossibleMoves() {
        Set<Move> possibleMoves = new HashSet<>();
        int yMultiplier = getColor() == PieceColor.WHITE ? -1 : 1;
        possibleMoves = getPawnDiagonalMoves(possibleMoves, yMultiplier);
        possibleMoves = getPawnVerticalMoves(possibleMoves, yMultiplier);
        return possibleMoves;
    }

    private Set<Move> getPawnDiagonalMoves(Set<Move> possibleMoves, int yMultiplier) {
        int currentX = position.x;
        int currentY = position.y;
        for (int x = currentX - 1; x < currentX + 2 && x < 8; x++) {
            int y = currentY + (1 * yMultiplier);
            Position pos = new Position(x, y);
            if (Math.min(x, y) < 0 || y > 8 || x == currentX || !board.isSpaceOccupied(pos)) {
                continue;
            }
            ChessPiece otherPiece = board.getPieceAtPosition(pos);
            if (isEnemyPiece(otherPiece)) {
                possibleMoves.add(new Move(this, pos));
            }

            
        }
        return possibleMoves;
    }

    private Set<Move> getPawnVerticalMoves(Set<Move> possibleMoves, int yMultiplier) {
        int currentX = position.x;
        int currentY = position.y;
        int maxSpaces = hasMoved ? 1 : 2;
        int yMovement = 1 * yMultiplier;
        int x = currentX;
        int spacesMoved = 0;
        for (int y = currentY + yMovement; y < 8 && y > -1 && spacesMoved < maxSpaces; y += yMovement) {
            spacesMoved++;
            Position pos = new Position(x, y);
            if (board.isSpaceOccupied(pos)) {
                break;
            }
            possibleMoves.add(new Move(this, pos));
        }
        return possibleMoves;
    }

    
    
}
