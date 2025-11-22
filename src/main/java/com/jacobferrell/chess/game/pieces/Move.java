package com.jacobferrell.chess.game.pieces;

import com.jacobferrell.chess.dto.MoveParams;
import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.service.game.computer.MoveType;
import lombok.Data;

import static com.jacobferrell.chess.game.Game.createGameFromEntity;

@Data
public class Move {
    
    private final ChessPiece piece;

    private final ChessBoard board;

    // Original hasMoved state of piece
    private final boolean hasMoved;

    private final Position from;

    private final Position to;

    private ChessPiece takenPiece;

    private boolean isLegal = false;

    private boolean otherPlayerInCheck = false;

    private boolean otherPlayerInCheckMate = false;

    public Move(ChessPiece piece, Position to) {
        this.piece = piece;
        this.board = piece.getBoard();
        this.hasMoved = piece.isHasMoved();
        this.from = piece.getPosition();
        this.to = to;
    }

    public void execute() {
        board.placePieceAndCapture(to, piece).ifPresent(this::setTakenPiece);
        this.isLegal = setIsLegal();
        King otherPlayerKing = board.getPlayerKing(piece.getColor().enemy());
        this.otherPlayerInCheck = otherPlayerKing.isInCheck();
        this.otherPlayerInCheckMate = otherPlayerKing.isInCheckMate();
    }

    public void reverse() {
        // Move piece back to original position and restore original hasMoved state
        board.placePieceAndCapture(from, piece);
        piece.setHasMoved(hasMoved);

        // If piece was taken, move piece back to original position, and add back to board
        if (takenPiece != null) {
            board.addPiece(takenPiece);
        }
    }

    public void simulate() {
        execute();
        reverse();
    }

    private boolean setIsLegal() {
        return board.hasBothKings() && !board.getPlayerKing(piece.color).isInCheck();
    }

    public MoveType getMoveType() {
        if (isOtherPlayerInCheckMate()) return MoveType.CHECK_MATE;
        if (isOtherPlayerInCheck()) return MoveType.CHECK;
        if (takenPiece != null) return MoveType.TAKE;
        return MoveType.NORMAL;
    }

    public static Move fromParams(GameEntity gameEntity, MoveParams params, User user) {

        PieceColor playerColor = PieceColor.fromGameAndUser(gameEntity, user);
        // Convert game object from database into backend game object
        Game game = createGameFromEntity(gameEntity);

        Position from = new Position(params.x0(), params.y0());
        // Test that a piece has been selected and belongs to the current user
        ChessPiece selectedPiece = ChessPiece.getAndValidatePiece(
                from,
                game,
                user,
                playerColor
        );

        Position to = new Position(params.x1(), params.y1());

        return new Move(selectedPiece, to);

    }

    @Override
    public String toString() {
        return "%s %s from %s to %s".formatted(piece.getColor(), piece.getType(), from.toString(), to.toString());
    }

    public static String convertToChessCoordinates(Position position) {
        int x = position.x();
        int y = position.y();
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    
        char xChar = (char) ('A' + x);
        int yInt = 8 - y;
    
        return Character.toString(xChar) + yInt;
    }

    /* @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Move)) {
            return false;
        }
        Move m = (Move) o;
        return m.piece.equals(piece) && m.position.equals(position);
    } */
    
}
