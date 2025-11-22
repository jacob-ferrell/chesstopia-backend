package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.ChessPiece;
import com.jacobferrell.chess.game.pieces.Knight;
import com.jacobferrell.chess.game.pieces.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Set;

@Slf4j
public class KnightTest {
    @Test
    public void testKnightMoves() {
        ChessBoard board = new ChessBoard();
        Knight knight = (Knight) board.getPieceAtPosition(new Position(1, 0)).get();
        Set<Move> possibleMoves = knight.generatePossibleMoves();

        return;
    }
}
