package com.jacobferrell.chess;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.pieces.*;

public class ChessBoardTest {

	@Test
	public void testChessBoard() {
		ChessBoard chessBoard = new ChessBoard();
		System.out.println(chessBoard);
	}

	@Test
	public void testCloneBoard() {
		ChessBoard chessBoard = new ChessBoard();
		ChessBoard clonedBoard = chessBoard.getClone();
		ChessPiece rook = clonedBoard.getPieceAtPosition(0, 0);
		ChessPiece rookClone = rook.getClone(clonedBoard);
		clonedBoard.setPieceAtPosition(0, 2, rookClone);
		System.out.println(chessBoard);
		System.out.println(clonedBoard);
	}

	@Test
	public void testHasBothKings() {
		ChessBoard board = new ChessBoard();
		assertTrue(board.hasBothKings());
		board.clearBoard();
		board.setPieceAtPosition(0, 4, new King(PieceColor.WHITE, new Position(0, 4), board));
		board.setPieceAtPosition(7, 4, new King(PieceColor.BLACK, new Position(7, 4), board));
		assertTrue(board.hasBothKings());
		board.setPieceAtPosition(7, 4, new King(PieceColor.WHITE, new Position(0, 4), board));
		assertFalse(board.hasBothKings());
		board.removePieceAtPosition(7, 4);
		assertFalse(board.hasBothKings());
	}
}