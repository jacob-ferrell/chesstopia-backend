package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.Move;
import com.jacobferrell.chess.game.pieces.Pawn;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.jacobferrell.chess.service.game.computer.ComputerService.selectMove;

public class ComputerTest {

    @Test
    public void testMoveMap() {
        ChessBoard board = ChessBoardTestUtils.withKingsOnly();
        board.addPiece(new Pawn(PieceColor.BLACK, new Position(0, 1), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(1, 2), board));
        board.addPiece(new Pawn(PieceColor.WHITE, new Position(0, 2), board));

        Set<Move> allPossibleComputerMoves =
                board.getAllPossibleMoves(PieceColor.BLACK);

        Move move = selectMove(allPossibleComputerMoves);

    }
}
