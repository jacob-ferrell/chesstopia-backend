package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.util.ChessBoardTestUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static com.jacobferrell.chess.service.game.computer.ComputerService.selectMove;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that follow the full game loop: starting a game, executing a
 * sequence of moves, and verifying board state and game-end conditions.
 *
 * Coordinate system: x=file (0=a, 7=h), y=rank (0=rank8/black back rank, 7=rank1/white back rank)
 * White pieces start at y=7 (back rank) and y=6 (pawns).
 * Black pieces start at y=0 (back rank) and y=1 (pawns).
 * White pawns move toward y=0 (yDirection=-1); black pawns move toward y=7 (yDirection=+1).
 */
public class GameLoopTest {

    /**
     * Fool's Mate — the fastest checkmate in chess (2 moves for each side).
     * 1. f3 (5,6→5,5)  e5 (4,1→4,3)
     * 2. g4 (6,6→6,4)  Qh4# (3,0→7,4)
     *
     * After Qh4, the white king at e1 (4,7) is in check along the h4-e1 diagonal.
     * All white king escape squares are blocked by its own pawns; no white piece
     * can block or capture the queen. White has no legal moves.
     */
    @Test
    public void testFoolsMate_whiteLosesWithNoLegalMoves() {
        ChessBoard board = new ChessBoard();

        // 1. f3 — white f-pawn (5,6) → (5,5)
        Move whiteF3 = executeMove(board, 5, 6, 5, 5);
        assertTrue(whiteF3.isLegal());
        assertFalse(whiteF3.isOtherPlayerInCheck(), "f3 should not give check");

        // 1...e5 — black e-pawn (4,1) → (4,3)
        Move blackE5 = executeMove(board, 4, 1, 4, 3);
        assertTrue(blackE5.isLegal());

        // 2. g4 — white g-pawn (6,6) → (6,4)
        Move whiteG4 = executeMove(board, 6, 6, 6, 4);
        assertTrue(whiteG4.isLegal());
        assertFalse(whiteG4.isOtherPlayerInCheck(), "g4 should not give check");

        // 2...Qh4# — black queen (3,0) → (7,4)
        Move blackQh4 = executeMove(board, 3, 0, 7, 4);
        assertTrue(blackQh4.isLegal(), "Black queen move to h4 should be legal");
        assertTrue(blackQh4.isOtherPlayerInCheck(), "Qh4 should put white king in check");

        // Verify white king is in check
        King whiteKing = board.getPlayerKing(PieceColor.WHITE);
        assertTrue(whiteKing.isInCheck(), "White king should be in check after Qh4");

        // Verify white has no legal moves (checkmate via selectMove)
        Set<Move> whitePseudoMoves = board.getAllPossibleMoves(PieceColor.WHITE);
        assertFalse(whitePseudoMoves.isEmpty(), "White should still have pseudo-legal moves");
        Optional<Move> legalMove = selectMove(whitePseudoMoves);
        assertTrue(legalMove.isEmpty(), "White should have no legal moves — this is checkmate");
    }

    /**
     * Scholar's Mate — a common 4-move checkmate.
     * 1. e4 (4,6→4,4)   e5 (4,1→4,3)
     * 2. Bc4 (5,7→2,4)  Nc6 (1,0→2,2)
     * 3. Qh5 (3,7→7,3)  Nf6?? (6,0→5,2)
     * 4. Qxf7# (7,3→5,1)
     *
     * White queen on f7 (5,1) delivers check to black king on e8 (4,0).
     * White bishop on c4 (2,4) defends f7 so king cannot capture queen.
     * All black king escape squares are occupied or attacked.
     */
    @Test
    public void testScholarsMate_blackLosesWithNoLegalMoves() {
        ChessBoard board = new ChessBoard();

        // 1. e4 — white e-pawn (4,6) → (4,4)
        assertTrue(executeMove(board, 4, 6, 4, 4).isLegal());

        // 1...e5 — black e-pawn (4,1) → (4,3)
        assertTrue(executeMove(board, 4, 1, 4, 3).isLegal());

        // 2. Bc4 — white bishop (5,7) → (2,4)
        assertTrue(executeMove(board, 5, 7, 2, 4).isLegal());

        // 2...Nc6 — black knight (1,0) → (2,2)
        assertTrue(executeMove(board, 1, 0, 2, 2).isLegal());

        // 3. Qh5 — white queen (3,7) → (7,3)
        assertTrue(executeMove(board, 3, 7, 7, 3).isLegal());

        // 3...Nf6?? — black knight (6,0) → (5,2)
        assertTrue(executeMove(board, 6, 0, 5, 2).isLegal());

        // 4. Qxf7# — white queen (7,3) → (5,1), capturing pawn
        Move mateMove = executeMove(board, 7, 3, 5, 1);
        assertTrue(mateMove.isLegal(), "Qxf7 should be a legal move");
        assertNotNull(mateMove.getTakenPiece(), "Queen should capture a pawn at f7");
        assertEquals(PieceType.PAWN, mateMove.getTakenPiece().getType());
        assertTrue(mateMove.isOtherPlayerInCheck(), "Qxf7 should put black king in check");

        // Verify black king is in check
        King blackKing = board.getPlayerKing(PieceColor.BLACK);
        assertTrue(blackKing.isInCheck(), "Black king should be in check after Qxf7");

        // Verify black has no legal moves (checkmate)
        Set<Move> blackPseudoMoves = board.getAllPossibleMoves(PieceColor.BLACK);
        Optional<Move> legalMove = selectMove(blackPseudoMoves);
        assertTrue(legalMove.isEmpty(), "Black should have no legal moves — this is checkmate");
    }

    /**
     * Simple capture sequence: pawn captures pawn using the en-passant-like diagonal.
     * e4 d5, exd5 — white e-pawn captures black d-pawn.
     */
    @Test
    public void testPawnCaptureSequence() {
        ChessBoard board = new ChessBoard();

        // 1. e4 (4,6) → (4,4)
        Move e4 = executeMove(board, 4, 6, 4, 4);
        assertTrue(e4.isLegal());
        assertNull(e4.getTakenPiece(), "No capture on e4");

        // 1...d5 (3,1) → (3,3)
        Move d5 = executeMove(board, 3, 1, 3, 3);
        assertTrue(d5.isLegal());

        // 2. exd5 — white e-pawn at (4,4) captures black d-pawn at (3,3)
        Move capture = executeMove(board, 4, 4, 3, 3);
        assertTrue(capture.isLegal(), "Pawn diagonal capture should be legal");
        assertNotNull(capture.getTakenPiece(), "Should have captured a piece");
        assertEquals(PieceType.PAWN, capture.getTakenPiece().getType());
        assertEquals(PieceColor.BLACK, capture.getTakenPiece().getColor());

        // White pawn should now be at d5 (3,3)
        Optional<ChessPiece> pieceAtD5 = board.getPieceAtPosition(3, 3);
        assertTrue(pieceAtD5.isPresent());
        assertEquals(PieceColor.WHITE, pieceAtD5.get().getColor());
        assertEquals(PieceType.PAWN, pieceAtD5.get().getType());

        // Old position should be empty
        assertFalse(board.isPositionOccupied(new Position(4, 4)), "Old e4 square should be empty");
    }

    /**
     * Check detection: a move that gives check must set isOtherPlayerInCheck=true.
     */
    @Test
    public void testCheckGivingMoveIsDetected() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.WHITE, new Position(4, 7), board));
        King blackKing = new King(PieceColor.BLACK, new Position(4, 0), board);
        board.addPiece(blackKing);
        Rook whiteRook = new Rook(PieceColor.WHITE, new Position(0, 3), board);
        board.addPiece(whiteRook);

        // Move rook to file 4, directly threatening black king
        Move checkMove = new Move(whiteRook, new Position(4, 3));
        checkMove.execute();

        assertTrue(checkMove.isLegal());
        assertTrue(checkMove.isOtherPlayerInCheck(), "Move should flag that opponent is in check");
        assertTrue(blackKing.isInCheck(), "Black king should register as in check");
    }

    /**
     * An illegal move (exposing own king to check) should be flagged as illegal.
     */
    @Test
    public void testPinnedPieceMovementIsIllegal() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        King whiteKing = new King(PieceColor.WHITE, new Position(4, 7), board);
        board.addPiece(whiteKing);
        board.addPiece(new King(PieceColor.BLACK, new Position(0, 0), board));
        // Pin: white rook is between white king and black rook on file 4
        Rook ownRook = new Rook(PieceColor.WHITE, new Position(4, 4), board);
        board.addPiece(ownRook);
        board.addPiece(new Rook(PieceColor.BLACK, new Position(4, 0), board));

        Move illegalMove = new Move(ownRook, new Position(3, 4));
        illegalMove.execute();

        assertFalse(illegalMove.isLegal(), "Moving a pinned piece should be illegal");
        assertTrue(whiteKing.isInCheck(), "White king should be in check after moving pinned piece");
    }

    /**
     * Stalemate is detected when only two kings remain.
     */
    @Test
    public void testStalemateDetectedWhenOnlyKingsRemain() {
        ChessBoard board = ChessBoardTestUtils.withKingsOnly();
        assertTrue(board.isStalemate(PieceColor.WHITE), "White should be in stalemate with only kings");
        assertTrue(board.isStalemate(PieceColor.BLACK), "Black should be in stalemate with only kings");
    }

    /**
     * After a series of moves and reversals, the board should be back to its original state.
     */
    @Test
    public void testMultipleMovesAndReversalsRestoreBoard() {
        ChessBoard board = new ChessBoard();
        String original = board.toString();

        Move move1 = new Move(board.getPieceAtPosition(4, 6).get(), new Position(4, 4));
        move1.execute();
        Move move2 = new Move(board.getPieceAtPosition(4, 1).get(), new Position(4, 3));
        move2.execute();
        Move move3 = new Move(board.getPieceAtPosition(5, 7).get(), new Position(2, 4));
        move3.execute();

        assertNotEquals(original, board.toString(), "Board should have changed after moves");

        move3.reverse();
        move2.reverse();
        move1.reverse();

        assertEquals(original, board.toString(), "Board should be restored after reversing all moves");
    }

    /**
     * A checkmated position built manually: black king in corner, two white rooks cover all escape squares.
     * selectMove should return empty for black.
     */
    @Test
    public void testCheckmatedPositionHasNoLegalMoves() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(7, 7), board));
        // Rook on rank 0 covers the entire back rank
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 0), board));
        // Rook on rank 1 covers all escape squares above back rank
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 1), board));

        King blackKing = board.getPlayerKing(PieceColor.BLACK);
        assertTrue(blackKing.isInCheck(), "Black king should be in check");

        Set<Move> pseudoLegal = board.getAllPossibleMoves(PieceColor.BLACK);
        assertFalse(pseudoLegal.isEmpty(), "There should be pseudo-legal king moves");

        Optional<Move> legalMove = selectMove(pseudoLegal);
        assertTrue(legalMove.isEmpty(), "Black should have no legal moves in this checkmate position");
    }

    /**
     * Stalemate position: black king is not in check but has no legal moves.
     */
    @Test
    public void testStalematePositionHasNoLegalMovesAndKingNotInCheck() {
        ChessBoard board = ChessBoardTestUtils.clearBoard();
        board.addPiece(new King(PieceColor.BLACK, new Position(7, 0), board));
        board.addPiece(new King(PieceColor.WHITE, new Position(5, 2), board));
        board.addPiece(new Rook(PieceColor.WHITE, new Position(0, 1), board)); // covers rank 1
        board.addPiece(new Rook(PieceColor.WHITE, new Position(6, 7), board)); // covers file 6

        King blackKing = board.getPlayerKing(PieceColor.BLACK);
        assertFalse(blackKing.isInCheck(), "Black king should NOT be in check in a stalemate");

        Set<Move> pseudoLegal = board.getAllPossibleMoves(PieceColor.BLACK);
        Optional<Move> legalMove = selectMove(pseudoLegal);
        assertTrue(legalMove.isEmpty(), "Black should have no legal moves in stalemate");
    }

    /**
     * Board state test: verify the initial board has the correct pieces in the correct positions.
     */
    @Test
    public void testInitialBoardHasCorrectPieceLayout() {
        ChessBoard board = new ChessBoard();

        // White back rank
        assertPiece(board, 0, 7, PieceColor.WHITE, PieceType.ROOK);
        assertPiece(board, 1, 7, PieceColor.WHITE, PieceType.KNIGHT);
        assertPiece(board, 2, 7, PieceColor.WHITE, PieceType.BISHOP);
        assertPiece(board, 3, 7, PieceColor.WHITE, PieceType.QUEEN);
        assertPiece(board, 4, 7, PieceColor.WHITE, PieceType.KING);
        assertPiece(board, 5, 7, PieceColor.WHITE, PieceType.BISHOP);
        assertPiece(board, 6, 7, PieceColor.WHITE, PieceType.KNIGHT);
        assertPiece(board, 7, 7, PieceColor.WHITE, PieceType.ROOK);

        // White pawns
        for (int x = 0; x < 8; x++) {
            assertPiece(board, x, 6, PieceColor.WHITE, PieceType.PAWN);
        }

        // Black back rank
        assertPiece(board, 0, 0, PieceColor.BLACK, PieceType.ROOK);
        assertPiece(board, 1, 0, PieceColor.BLACK, PieceType.KNIGHT);
        assertPiece(board, 2, 0, PieceColor.BLACK, PieceType.BISHOP);
        assertPiece(board, 3, 0, PieceColor.BLACK, PieceType.QUEEN);
        assertPiece(board, 4, 0, PieceColor.BLACK, PieceType.KING);
        assertPiece(board, 5, 0, PieceColor.BLACK, PieceType.BISHOP);
        assertPiece(board, 6, 0, PieceColor.BLACK, PieceType.KNIGHT);
        assertPiece(board, 7, 0, PieceColor.BLACK, PieceType.ROOK);

        // Black pawns
        for (int x = 0; x < 8; x++) {
            assertPiece(board, x, 1, PieceColor.BLACK, PieceType.PAWN);
        }

        // Middle rows are empty
        for (int x = 0; x < 8; x++) {
            for (int y = 2; y < 6; y++) {
                assertFalse(board.isPositionOccupied(new Position(x, y)),
                        "Square (" + x + "," + y + ") should be empty at start");
            }
        }
    }

    // --- helpers ---

    private Move executeMove(ChessBoard board, int x0, int y0, int x1, int y1) {
        ChessPiece piece = board.getPieceAtPosition(x0, y0)
                .orElseThrow(() -> new AssertionError("No piece at (" + x0 + "," + y0 + ")"));
        Move move = new Move(piece, new Position(x1, y1));
        move.execute();
        return move;
    }

    private void assertPiece(ChessBoard board, int x, int y, PieceColor color, PieceType type) {
        Optional<ChessPiece> piece = board.getPieceAtPosition(x, y);
        assertTrue(piece.isPresent(), "Expected " + color + " " + type + " at (" + x + "," + y + ")");
        assertEquals(color, piece.get().getColor(), "Wrong color at (" + x + "," + y + ")");
        assertEquals(type, piece.get().getType(), "Wrong type at (" + x + "," + y + ")");
    }
}
