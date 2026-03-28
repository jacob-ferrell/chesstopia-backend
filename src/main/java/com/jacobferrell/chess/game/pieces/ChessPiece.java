package com.jacobferrell.chess.game.pieces;

import com.jacobferrell.chess.game.Game;
import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.model.GamePiecePosition;
import com.jacobferrell.chess.model.PieceEntity;
import com.jacobferrell.chess.model.PositionEntity;
import com.jacobferrell.chess.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class ChessPiece {

    protected final PieceColor color;

    protected Long gamePiecePositionId;

    protected final PieceType type;

    protected Position position;

    protected int counter;

    protected boolean hasMoved;

    protected ChessBoard board;

    protected int rank;

    protected char symbol;

    protected final Integer moveLimit;

    protected final List<Integer[]> moveset;

    protected static List<Integer[]> VERTICAL_MOVE_SET =
            List.of(new Integer[]{0, 1}, new Integer[]{0, -1});

    protected static List<Integer[]> HORIZONTAL_MOVE_SET =
            List.of(new Integer[]{1, 0}, new Integer[]{-1, 0});

    protected static List<Integer[]> DIAGONAL_MOVE_SET =
            List.of(
                    new Integer[]{1, 1}, new Integer[]{1, -1},
                    new Integer[]{-1, 1}, new Integer[]{-1, -1}
            );

    public ChessPiece(PieceColor color, Position pos, ChessBoard board, PieceType type, List<Integer[]> moveset) {
        this.color = color;
        this.position = pos;
        this.board = board;
        this.hasMoved = false;
        this.counter = 0;
        this.type = type;
        this.symbol = 0;
        this.moveset = moveset;
        this.moveLimit = null;
    }

    public ChessPiece(PieceColor color, Position pos, ChessBoard board, PieceType type, List<Integer[]> moveset, Integer moveLimit) {
        this.color = color;
        this.position = pos;
        this.board = board;
        this.hasMoved = false;
        this.counter = 0;
        this.type = type;
        this.symbol = 0;
        this.moveset = moveset;
        this.moveLimit = moveLimit;
    }

    public boolean isValidMove(int x, int y) {

        Position pos = new Position(x, y);

        return pos.isValid() && board.getPieceAtPosition(pos)
                .map(this::isEnemyPiece)
                .orElse(true);

    }

    public Set<Position> generatePossiblePositions() {
        return getPositionsFromMoveset(getMoveset(), moveLimit);
    }

    public Set<Move> generatePossibleMoves() {
        return generatePossiblePositions().stream()
                        .map(position -> new Move(this, position))
                        .collect(Collectors.toSet());
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    private Set<Position> getPositionsFromMoveset(List<Integer[]> movesets) {
        return getPositionsFromMoveset(movesets, null);
    }

    protected Set<Position> getPositionsFromMoveset(List<Integer[]> movesets, Integer limit) {
        Set<Position> possibleMoves = new HashSet<>();

        movesets.forEach(moveset -> {

            for (
                    int i = 0, x = position.x() + moveset[1], y = position.y() + moveset[0];
                    (limit == null || i < limit) && isValidMove(x, y);
                    i++, x += moveset[1], y += moveset[0]
            ) {

                Position pos = new Position(x, y);

                possibleMoves.add(pos);
                if (board.isPositionOccupied(pos)) {
                    break;
                }
            }
        });

        return possibleMoves;
    }

    protected Optional<ChessPiece> getEnemyPieceFromMoveset(List<Integer[]> movesets, Integer limit, List<PieceType> piecesWithMoveset) {
        for (var moveset : movesets) {
            for (
                    int i = 0, x = position.x() + moveset[1], y = position.y() + moveset[0];
                    (limit == null || i < limit) && isValidMove(x, y);
                    i++, x += moveset[1], y += moveset[0]
            ) {
                Optional<ChessPiece> atPosition = board.getPieceAtPosition(x, y);
                Optional<ChessPiece> enemyPiece = atPosition
                        .filter(this::isEnemyPiece)
                        .filter(p -> piecesWithMoveset.contains(p.getType()));

                if (enemyPiece.isPresent()) {
                    return enemyPiece;
                }
                if (atPosition.isPresent()) {
                    break; // piece blocks the line of attack
                }
            }

        }
        return Optional.empty();
    }

    protected boolean canBeAttacked() {
        return getEnemyPieceFromMoveset(Pawn.getAttackMoveset(this), 1, List.of(PieceType.PAWN)).isPresent() ||
                getEnemyPieceFromMoveset(HORIZONTAL_MOVE_SET, null, List.of(PieceType.QUEEN, PieceType.ROOK)).isPresent() ||
                getEnemyPieceFromMoveset(VERTICAL_MOVE_SET, null, List.of(PieceType.QUEEN, PieceType.ROOK)).isPresent() ||
                getEnemyPieceFromMoveset(DIAGONAL_MOVE_SET, null, List.of(PieceType.QUEEN, PieceType.BISHOP)).isPresent() ||
                getEnemyPieceFromMoveset(Knight.MOVESET, 1, List.of(PieceType.KNIGHT)).isPresent();
    }

    // Add all possible horizontal moves
    public Set<Position> getHorizontalPositions() {
        return getPositionsFromMoveset(HORIZONTAL_MOVE_SET);
    }

    public Set<Position> getHorizontalMoves(Integer limit) {
        return getPositionsFromMoveset(HORIZONTAL_MOVE_SET, limit);
    }

    // Add all possible vertical moves
    public Set<Position> getVerticalPositions(Integer limit) {
        return getPositionsFromMoveset(VERTICAL_MOVE_SET, limit);
    }

    // Add all possible vertical moves
    public Set<Position> getVerticalPositions() {
        return getPositionsFromMoveset(VERTICAL_MOVE_SET);
    }
    // Add all possible diagonal moves
    public Set<Position> getDiagonalPositions() {
        return getPositionsFromMoveset(DIAGONAL_MOVE_SET);
    }

    // Add all possible diagonal moves
    public Set<Position> getDiagonalPositions(Integer limit) {
        return getPositionsFromMoveset(DIAGONAL_MOVE_SET, limit);
    }

    public void moveTo(Position newPosition) {
        board.getBoard()[position.y()][position.x()] = null;
        board.getBoard()[newPosition.y()][newPosition.x()] = this;
        // Original position must be cleared on board
        this.position = newPosition;
        setHasMoved();
    }

    public PieceColor getEnemyColor() {
        return color.enemy();
    }

    public boolean isEnemyPiece(ChessPiece otherPiece) {
        return otherPiece.color != color;
    }

    public static ChessPiece fromGamePieceMapping(GamePiecePosition mapping, ChessBoard board) {
        PieceEntity pieceMapping = mapping.getPiece();
        PieceType type = pieceMapping.getType();

        PositionEntity positionMapping = mapping.getPosition();
        Position position =  new Position(positionMapping.getX(), positionMapping.getY());

        PieceColor color = pieceMapping.getColor();
        ChessPiece piece = createNewPiece(type, position, color, board);

        piece.setGamePiecePositionId(mapping.getId());
        piece.setHasMoved(mapping.getHasMoved());
        return piece;
    }

    public GamePiecePosition copyToGamePieceMapping() {
        GamePiecePosition mapping = board.getGamePiecePositionsById().get(gamePiecePositionId);
        mapping.setPosition(position.toEntity());
        mapping.setPiece(this.toEntity());
        return mapping;
    }

    public static ChessPiece createNewPiece(PieceType type, Position position, PieceColor color, ChessBoard board) {
        return switch (type) {
            case ROOK -> new Rook(color, position, board);
            case BISHOP -> new Bishop(color, position, board);
            case KING -> new King(color, position, board);
            case PAWN -> new Pawn(color, position, board);
            case QUEEN -> new Queen(color, position, board);
            case KNIGHT -> new Knight(color, position, board);
        };
    }

    public GamePiecePosition toGamePieceMapping() {
        return GamePiecePosition
                .builder()
                .position(position.toEntity())
                .piece(this.toEntity())
                .build();
    }

    public PieceEntity toEntity() {
        return PieceEntity.of(this);
    }

    public static ChessPiece getAndValidatePiece(Position from, Game game, User user, PieceColor playerColor) {
        ChessPiece piece = game.board().getPieceAtPosition(from)
                .orElseThrow(() -> new IllegalArgumentException("There exists no piece coordinates: x: " + from.x() + ", y: " + from.y()));

        if (!piece.getColor().equals(playerColor)) {
            throw new IllegalArgumentException("The piece at coordinates: x: " + from.x() + ", y: " +from.y()
                    + " does not belong to user " + user.getEmail());
        }

        return piece;
    }


    @Override
    public String toString() {
        return getType() + ", " + color + " [" + position.y() + ", " + position.x() + "]";
    }

    public boolean isLastPlayerPiece() {
        return board.getPiecesByColor(color).size() == 1;
    }

    /*
     * @Override
     * public boolean equals(Object o) {
     * if (o == this) {
     * return true;
     * }
     * if (!(o instanceof ChessPiece)) {
     * return false;
     * }
     * ChessPiece p = (ChessPiece) o;
     * return color.equals(p.color) && getName().equals(p.getName()) &&
     * position.equals(p.position);
     * }
     */

}
