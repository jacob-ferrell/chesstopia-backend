package com.jacobferrell.chess.game.chessboard;

import java.util.*;
import java.util.stream.Collectors;

import com.jacobferrell.chess.game.pieces.*;
import com.jacobferrell.chess.model.GamePiecePosition;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChessBoard {

    private final Map<Long, GamePiecePosition> gamePiecePositionsById = new HashMap<>();

    @Getter
    private final ChessPiece[][] board = new ChessPiece[8][8];

    private final List<ChessPiece> whitePieces = new ArrayList<>();

    private final List<ChessPiece> blackPieces = new ArrayList<>();

    private King whiteKing;
    private King blackKing;

    public ChessBoard() {
        setBoard();
    }

    public ChessBoard(List<GamePiecePosition> pieceData) {
        pieceData.stream()
                .peek(pieceMapping -> gamePiecePositionsById.put(pieceMapping.getId(), pieceMapping))
                .map(pieceDTO -> ChessPiece.fromGamePieceMapping(pieceDTO, this))
                .forEach(this::addPiece);
    }

    public ChessBoard (
            List<ChessPiece> whitePieces,
            List<ChessPiece> blackPieces
    ) {
        whitePieces.forEach(this::addPiece);
        blackPieces.forEach(this::addPiece);
    }

    public List<GamePiecePosition> toGamePieceMappings() {
        return getAllPieces().stream()
                .map(ChessPiece::toGamePieceMapping)
                .collect(Collectors.toList());
    }

    public List<ChessPiece> getAllPieces() {
        List<ChessPiece> result = new ArrayList<>();
        result.addAll(whitePieces);
        result.addAll(blackPieces);
        return result;
    }

    public void addPiece(ChessPiece piece) {
        var position = piece.getPosition();
        board[position.y()][position.x()] = piece;

        switch(piece.getColor()) {
            case WHITE -> {
                whitePieces.add(piece);
                if (piece instanceof King king) {
                    whiteKing = king;
                }
            }
            case BLACK -> {
                blackPieces.add(piece);
                if (piece instanceof King king) {
                    blackKing = king;
                }
            }
        }
    }

    private void setBoard() {
        setWhitePieces();
        setBlackPieces();
    }

    private void setWhitePieces() {
        addPiece(new Rook(PieceColor.WHITE, new Position(0, 7), this));
        addPiece(new Knight(PieceColor.WHITE, new Position(1, 7), this));
        addPiece(new Bishop(PieceColor.WHITE, new Position(2, 7), this));
        addPiece(new Queen(PieceColor.WHITE, new Position(3, 7), this));
        addPiece(new King(PieceColor.WHITE, new Position(4, 7), this));
        addPiece(new Bishop(PieceColor.WHITE, new Position(5, 7), this));
        addPiece(new Knight(PieceColor.WHITE, new Position(6, 7), this));
        addPiece(new Rook(PieceColor.WHITE, new Position(7, 7), this));
        for (int i = 0; i < 8; i++) {
            addPiece(new Pawn(PieceColor.WHITE, new Position(i, 6), this));
        }

    }

    private void setBlackPieces() {
        addPiece(new Rook(PieceColor.BLACK, new Position(0, 0), this));
        addPiece(new Knight(PieceColor.BLACK, new Position(1, 0), this));
        addPiece(new Bishop(PieceColor.BLACK, new Position(2, 0), this));
        addPiece(new Queen(PieceColor.BLACK, new Position(3, 0), this));
        addPiece(new King(PieceColor.BLACK, new Position(4, 0), this));
        addPiece(new Bishop(PieceColor.BLACK, new Position(5, 0), this));
        addPiece(new Knight(PieceColor.BLACK, new Position(6, 0), this));
        addPiece(new Rook(PieceColor.BLACK, new Position(7, 0), this));

        for (int i = 0; i < 8; i++) {
            addPiece(new Pawn(PieceColor.BLACK, new Position(i, 1), this));
        }

    }

    public Optional<ChessPiece> getPieceAtPosition(Position pos) {
        return Optional.ofNullable(board[pos.y()][pos.x()]);
    }

    public Optional<ChessPiece> getPieceAtPosition(int x, int y) {
        return Optional.ofNullable(board[y][x]);
    }

    public boolean isPositionOccupied(Position pos) {
        return getPieceAtPosition(pos).isPresent();
    }

    public Map<PieceColor, Set<Move>> getAllPossibleMoves() {
        var moveMap = Arrays.stream(PieceColor.values())
                .collect(Collectors.toMap(
                        color -> color,
                        this::getAllPossibleMoves
                ));

/*    Ignoring castling here, for now
        Arrays.stream(new King[]{whiteKing, blackKing})
                .filter(king -> !king.isHasMoved())
                .forEach(king -> {
                    Set<Rook> castleRooks = getCastleRooks(king, moveMap.get(getEnemyColor(king.color)));
                    for (Rook rook : castleRooks) {
                        Set<Move> castleMoves = new HashSet<>();
                        castleMoves.add(new Move(king, rook.position));
                        castleMoves.add(new Move(rook, king.position));
                        addToSet(moveMap, king.color, castleMoves);
                    }
                });
*/

        return moveMap;
    }

    public Set<Move> getAllPossibleMoves(PieceColor color) {
        return getPiecesByColor(color).stream()
                .map(ChessPiece::generatePossibleMoves)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public Optional<ChessPiece> placePieceAndCapture(Position moveTo, ChessPiece piece) {
        Optional<ChessPiece> existingPiece = getPieceAtPosition(moveTo);

        Optional<ChessPiece> enemyPiece = existingPiece
                .filter(piece::isEnemyPiece);

        if (existingPiece.isPresent() && enemyPiece.isEmpty()) {
            return Optional.empty();
        }

        enemyPiece.ifPresent(this::removePiece);

        piece.moveTo(moveTo);

        return enemyPiece;
    }

    public void removePiece(ChessPiece piece) {
        Position position = piece.getPosition();
        board[position.y()][position.x()] = null;
        getPiecesByColor(piece.getColor()).remove(piece);
        if (piece instanceof King) {
            log.warn("removePiece called on KING {} at {}", piece.getColor(), position,
                    new RuntimeException("KING REMOVAL STACK TRACE"));
        }
        // Do NOT remove from gamePiecePositionsById here — simulate()/reverse() cycles
        // remove and re-add pieces, but addPiece() cannot restore the map entry.
        // The map is only queried via copyToGamePieceMapping() which is called on
        // getAllPieces() (live pieces only), so stale entries are harmless.
    }

    public void removePiece(Position pos) {
        getPieceAtPosition(pos).ifPresent(this::removePiece);
    }

    public King getPlayerKing(PieceColor color) {
        return switch(color) {
            case WHITE -> whiteKing;
            case BLACK -> blackKing;
        };
    }

    public King getOpponentKing(PieceColor color) {
        return switch(color) {
            case WHITE -> blackKing;
            case BLACK -> whiteKing;
        };
    }

    public boolean hasBothKings() {
        return existsOnBoard(whiteKing) && existsOnBoard(blackKing);
    }

    public boolean existsOnBoard(ChessPiece piece) {
        if (piece == null) return false;
        Position position = piece.getPosition();
        return board[position.y()][position.x()].equals(piece);
    }

    public void clear() {
        whitePieces.clear();
        blackPieces.clear();
        whiteKing = null;
        blackKing = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
    }
/*

    private List<Rook> getCastleRooks(PieceColor playerColor, Set<Position> enemyMoves) {

        return getPiecesByColor(playerColor).stream()
                .filter(piece -> piece instanceof Rook rook && rook.canCastle(enemyMoves))
                .map(Rook.class::cast)
                .toList();

    }
*/

    public ChessPiece createNewPiece(PieceType type, Position position, PieceColor color) {
        return switch (type) {
            case ROOK -> new Rook(color, position, this);
            case BISHOP -> new Bishop(color, position, this);
            case KING -> new King(color, position, this);
            case PAWN -> new Pawn(color, position, this);
            case QUEEN -> new Queen(color, position, this);
            case KNIGHT -> new Knight(color, position, this);
        };
    }

    public List<ChessPiece> getPiecesByColor(PieceColor color) {
        return switch(color) {
            case WHITE -> whitePieces;
            case BLACK -> blackPieces;
        };
    }

    public boolean isStalemate(PieceColor playerColor) {
        boolean over50Count = getPiecesByColor(playerColor).stream()
                .anyMatch(p -> p.getCounter() >= 50);

        boolean onlyKingsLeft = whitePieces.size() == 1 && blackPieces.size() == 1;


        return over50Count || onlyKingsLeft;
    }

    public Map<Long, GamePiecePosition> getGamePiecePositionsById() {
        if (gamePiecePositionsById.isEmpty()) {
            throw new IllegalStateException("pieceDataById is empty.  Gameboard was not constructed properly from a GameEntity");
        }
        return gamePiecePositionsById;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                char ch = getPieceAtPosition(x, y)
                        .map(ChessPiece::getSymbol)
                        .orElse('-');
                sb.append(ch);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
