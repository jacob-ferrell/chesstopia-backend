package com.jacobferrell.chess.pieces;
import com.jacobferrell.chess.chessboard.Position;
import java.util.Set;

public interface MoveSet {
    Set<Position> generatePossibleMoves();
}
