package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.jacobferrell.chess.service.game.computer.ComputerService.selectMove;
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

    @Test
    public void testMoveThatTakesKingIsNotLegal() {
        ChessBoard chessBoard = new ChessBoard();
        Move move = new Move(chessBoard.getPieceAtPosition(4, 0).get(), new Position(4, 7));
        move.execute();
        assertFalse(move.isLegal());
    }

    @Test
    public void testMoveSetsPieceHasMoved() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);

        assertFalse(pawn.isHasMoved());
        Move move = new Move(pawn, new Position(4, 5));
        move.execute();
        assertTrue(pawn.isHasMoved(), "Piece should be marked as moved after execute()");
    }

    @Test
    public void testMoveReversalRestoresHasMoved() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Pawn pawn = new Pawn(PieceColor.WHITE, new Position(4, 6), board);
        board.addPiece(pawn);

        Move move = new Move(pawn, new Position(4, 5));
        move.execute();
        assertTrue(pawn.isHasMoved());
        move.reverse();
        assertFalse(pawn.isHasMoved(), "hasMoved should be restored to false after reverse()");
    }

    @Test
    public void testSimulateDoesNotChangeBoardState() {
        ChessBoard board = new ChessBoard();
        String before = board.toString();
        ChessPiece pawn = board.getPieceAtPosition(4, 6).get();
        Move move = new Move(pawn, new Position(4, 4));

        move.simulate();

        assertEquals(before, board.toString(), "Board state should be unchanged after simulate()");
        assertFalse(pawn.isHasMoved(), "hasMoved should be restored after simulate()");
    }

    @Test
    public void testMoveCaptureSetsAndReturnsTakenPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        Pawn enemy = new Pawn(PieceColor.BLACK, new Position(3, 1), board);
        board.addPiece(enemy);

        Move move = new Move(rook, new Position(3, 1));
        move.execute();

        assertNotNull(move.getTakenPiece(), "TakenPiece should be set after capturing a piece");
        assertEquals(enemy, move.getTakenPiece(), "TakenPiece should be the captured enemy");
        assertFalse(board.getPieceAtPosition(3, 1).map(p -> p == enemy).orElse(false));
    }

    @Test
    public void testMoveReversalRestoresCapturedPiece() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        Pawn enemy = new Pawn(PieceColor.BLACK, new Position(3, 1), board);
        board.addPiece(enemy);
        String before = board.toString();

        Move move = new Move(rook, new Position(3, 1));
        move.execute();
        move.reverse();

        assertEquals(before, board.toString(), "Board should be restored after reversing a capture");
        assertTrue(board.getPieceAtPosition(3, 1).isPresent(), "Captured piece should be restored");
    }

    @Test
    public void testMoveLeavingOwnKingInCheckIsIllegal() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7), board);
        board.addPiece(whiteKing);
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        // Pin: white rook is the only piece between the white king and enemy rook
        Rook ownRook = new Rook(PieceColor.WHITE, new Position(4, 4), board);
        board.addPiece(ownRook);
        board.addPiece(new Rook(PieceColor.BLACK, new Position(4, 0), board));

        // Moving own rook sideways exposes king to the enemy rook on file 4
        Move illegalMove = new Move(ownRook, new Position(3, 4));
        illegalMove.execute();

        assertFalse(illegalMove.isLegal(), "Move that leaves own king in check should be illegal");
    }

    @Test
    public void testMoveToOccupiedFriendlySquareIsNotCapture() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        Rook rook = new Rook(PieceColor.WHITE, new Position(3, 3), board);
        board.addPiece(rook);
        Pawn ally = new Pawn(PieceColor.WHITE, new Position(3, 1), board);
        board.addPiece(ally);

        // Attempting to move rook onto friendly pawn — placePieceAndCapture won't capture ally
        Move move = new Move(rook, new Position(3, 1));
        move.execute();

        assertNull(move.getTakenPiece(), "Friendly piece should not be captured");
    }

    @Test
    public void testConvertToChessCoordinates() {
        assertEquals("A8", Move.convertToChessCoordinates(new Position(0, 0)));
        assertEquals("H1", Move.convertToChessCoordinates(new Position(7, 7)));
        assertEquals("E4", Move.convertToChessCoordinates(new Position(4, 4)));
        assertEquals("A1", Move.convertToChessCoordinates(new Position(0, 7)));
        assertEquals("H8", Move.convertToChessCoordinates(new Position(7, 0)));
    }

    @Test
    public void testMoveCheckDetection() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(4, 7), board));
        King blackKing = new King(PieceColor.BLACK, new Position(4, 0), board);
        board.addPiece(blackKing);
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(0, 3), board);
        board.addPiece(whiteRook);

        // Move rook to file 4, giving check to black king
        Move checkMove = new Move(whiteRook, new Position(4, 3));
        checkMove.execute();

        assertTrue(checkMove.isLegal(), "Check-giving move should be legal");
        assertTrue(checkMove.isOtherPlayerInCheck(), "Move should put opponent in check");
        assertTrue(blackKing.isInCheck(), "Black king should be in check");
    }
}
