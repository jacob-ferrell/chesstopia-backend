package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.Move;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class MoveTest {
    @Test
    public void testMoveReversal() {
        ChessBoard chessBoard = new ChessBoard();
        String original = chessBoard.toString();
        Move move = new Move(chessBoard.getPieceAtPosition(0, 0).get(), new Position(0, 7));
        move.execute();
        assertNotEquals(original, chessBoard.toString());
        move.reverse();
        assertEquals(original, chessBoard.toString());
    }

    @Test
    public void testMoveThatTakesPieceIsLegal() {
        ChessBoard chessBoard = new ChessBoard();
        Move move = new Move(chessBoard.getPieceAtPosition(0, 0).get(), new Position(0, 7));
        move.execute();
        assertTrue(move.isLegal());
    }

    @Test void testMoveThatTakesKingIsNotLegal() {
        ChessBoard chessBoard = new ChessBoard();
        Move move = new Move(chessBoard.getPieceAtPosition(4, 0).get(), new Position(4, 7));
        move.execute();
        assertFalse(move.isLegal());
    }
}
