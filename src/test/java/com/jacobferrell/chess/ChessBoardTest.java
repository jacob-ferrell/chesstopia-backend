package com.jacobferrell.chess;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.King;
import com.jacobferrell.chess.game.pieces.PieceColor;
import org.junit.jupiter.api.Test;

public class ChessBoardTest {

	@Test
	public void testHasBothKings() {
		ChessBoard board = new ChessBoard();
		assertTrue(board.hasBothKings());
		board.clear();
		board.placePieceAndCapture(new Position(0, 4), new King(PieceColor.WHITE, new Position(0, 4), board));
		board.placePieceAndCapture(new Position(7, 4), new King(PieceColor.BLACK, new Position(7, 4), board));
		assertTrue(board.hasBothKings());
		board.placePieceAndCapture(new Position(7, 4), new King(PieceColor.WHITE, new Position(0, 4), board));
		assertFalse(board.hasBothKings());
		board.removePiece(new Position(7, 4));
		assertFalse(board.hasBothKings());
	}
}