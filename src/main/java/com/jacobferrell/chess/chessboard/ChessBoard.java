package com.jacobferrell.chess.chessboard;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.jacobferrell.chess.pieces.*;
import com.jacobferrell.chess.model.PieceDTO;

public class ChessBoard {
    public Set<ChessPiece> board = new HashSet<>();
    private Set<ChessPiece> graveyard = new HashSet<>();

    public ChessBoard() {
        setBoard();
    }

    public ChessBoard getClone() {
        ChessBoard clonedBoard = new ChessBoard();
        clonedBoard.clearBoard();
        for (ChessPiece piece : board) {
            clonedBoard.board.add(piece.getClone(clonedBoard));
        }
        return clonedBoard;
    }

    private void setBoard() {
        // Initialize black pieces
        board.add(new Rook(PieceColor.BLACK, new Position(0, 0), this));
        board.add(new Knight(PieceColor.BLACK, new Position(1, 0), this));
        board.add(new Bishop(PieceColor.BLACK, new Position(2, 0), this));
        board.add(new Queen(PieceColor.BLACK, new Position(3, 0), this));
        board.add(new King(PieceColor.BLACK, new Position(4, 0), this));
        board.add(new Bishop(PieceColor.BLACK, new Position(5, 0), this));
        board.add(new Knight(PieceColor.BLACK, new Position(6, 0), this));
        board.add(new Rook(PieceColor.BLACK, new Position(7, 0), this));
        for (int i = 0; i < 8; i++) {
            board.add(new Pawn(PieceColor.BLACK, new Position(i, 1), this));
        }

        // Initialize white pieces
        board.add(new Rook(PieceColor.WHITE, new Position(0, 7), this));
        board.add(new Knight(PieceColor.WHITE, new Position(1, 7), this));
        board.add(new Bishop(PieceColor.WHITE, new Position(2, 7), this));
        board.add(new Queen(PieceColor.WHITE, new Position(3, 7), this));
        board.add(new King(PieceColor.WHITE, new Position(4, 7), this));
        board.add(new Bishop(PieceColor.WHITE, new Position(5, 7), this));
        board.add(new Knight(PieceColor.WHITE, new Position(6, 7), this));
        board.add(new Rook(PieceColor.WHITE, new Position(7, 7), this));
        for (int i = 0; i < 8; i++) {
            board.add(new Pawn(PieceColor.WHITE, new Position(i, 6), this));
        }
    }

    public void setBoardOneMoveFromCheckmate() {
        clearBoard();
        board.add(new Rook(PieceColor.WHITE, new Position(1, 1), this));
        board.add(new Rook(PieceColor.WHITE, new Position(0, 1), this));
        board.add(new King(PieceColor.BLACK, new Position(7, 0), this));
        board.add(new King(PieceColor.WHITE, new Position(7, 7), this));
    }

    public ChessPiece getPieceAtPosition(Position pos) {
        return board.stream().filter(p -> p.position.equals(pos)).findFirst().orElse(null);
    }

    public boolean isSpaceOccupied(Position pos) {
        ChessPiece piece = board.stream().filter(p -> p.position.equals(pos)).findFirst().orElse(null);
        return piece != null;
    }

    public Map<PieceColor, Set<Move>> getAllPossibleMoves() {
        Map<PieceColor, Set<Move>> moveMap = new HashMap<>();
        Set<King> kings = getKings();
        for (ChessPiece piece : board) {
            addToSet(moveMap, piece.color, piece.generatePossibleMoves());
        }
        for (King king : kings) {
            Set<Rook> castleRooks = getCastleRooks(king, moveMap.get(getEnemyColor(king.color)));
            for (Rook rook : castleRooks) {
                Set<Move> castleMoves = new HashSet<>();
                castleMoves.add(new Move(king, rook.position));
                castleMoves.add(new Move(rook, king.position));
                addToSet(moveMap, king.color, castleMoves);
            }
        }
        return moveMap;
    }

    private void addToSet(Map<PieceColor, Set<Move>> map, PieceColor color, Set<Move> moves) {
        Set<Move> set = map.get(color);
        if (set == null) {
            set = new HashSet<>();
        }
        set.addAll(moves);
        map.put(color, set);
    }

    public PieceColor getEnemyColor(PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            return PieceColor.BLACK;
        }
        return PieceColor.WHITE;
    }

    public void setPieceAtPosition(Position pos, ChessPiece piece) {
        ChessPiece takenPiece = getPieceAtPosition(pos);
        if (takenPiece != null && piece.isEnemyPiece(takenPiece)) {
            graveyard.add(takenPiece);
            board.remove(takenPiece);
        }
        piece.position = pos;
        piece.setHasMoved();
    }

    public void removePieceAtPosition(Position pos) {
        ChessPiece piece = getPieceAtPosition(pos);
        if (piece == null)
            return;
        board.remove(piece);
    }

    public Set<King> getKings() {
        Set<King> kings = board.stream().filter(p -> p instanceof King).map(k -> (King) k).collect(Collectors.toSet());
        return kings;

    }

    public King getPlayerKing(PieceColor color) {
        Set<King> kings = getKings();
        return kings.stream().filter(king -> king.getColor() == color).findFirst().orElseThrow();
    }

    public King getOpponentKing(PieceColor color) {
        Set<King> kings = getKings();
        return kings.stream().filter(king -> king.getColor() != color).findFirst().get();
    }

    public boolean hasBothKings() {
        Set<King> kings = getKings();
        if (kings.size() != 2) {
            return false;
        }
        boolean hasWhite = false;
        boolean hasBlack = false;
        for (King king : kings) {
            if (king.getColor().equals(PieceColor.WHITE)) {
                hasWhite = true;
            }
            if (king.getColor().equals(PieceColor.BLACK)) {
                hasBlack = true;
            }
        }
        return hasWhite && hasBlack;
    }

    public void clearBoard() {
        this.board = new HashSet<>();
    }

    private Set<Rook> getCastleRooks(King king, Set<Move> enemyMoves) {
        Set<Rook> castleRooks = new HashSet<>();
        if (king.hasMoved) {
            return castleRooks;
        }
        Set<Rook> playerRooks = board.stream().filter(p -> p instanceof Rook && p.color.equals(king.color) && !p.hasMoved)
                .map(p -> (Rook) p).collect(Collectors.toSet());
        if (playerRooks.isEmpty()) {
            return castleRooks;
        }
        System.out.println("playerRooks: " + playerRooks);
        outerloop:
        for (Rook rook : playerRooks) {
            Set<Position> travelPositions = rook.getCastleTravelPositions(king);
            if (travelPositions.stream().anyMatch(pos -> !pos.equals(king.position) && !pos.equals(rook.position) && isSpaceOccupied(pos))) {
                continue;
            }
            for (Move move : enemyMoves) {
                if (travelPositions.stream().anyMatch(pos -> pos.equals(move.position))) {
                    continue outerloop;
                }
            }
            castleRooks.add(rook);
        }
        return castleRooks;

    }

    public void setBoardFromData(Set<PieceDTO> pieces) {
        clearBoard();
        for (PieceDTO piece : pieces) {
            PieceColor color = piece.getColor() == "WHITE" ? PieceColor.WHITE : PieceColor.BLACK;
            int x = piece.getX();
            int y = piece.getY();
            boolean hasMoved = piece.hasMoved;
            switch (piece.getType()) {
                case "ROOK":
                    board.add(new Rook(color, new Position(x, y), this));
                    break;
                case "BISHOP":
                    board.add(new Bishop(color, new Position(x, y), this));
                    break;

                case "KING":
                    board.add(new King(color, new Position(x, y), this));
                    break;

                case "PAWN":
                    board.add(new Pawn(color, new Position(x, y), this));
                    break;

                case "QUEEN":
                    board.add(new Queen(color, new Position(x, y), this));
                    break;

                case "KNIGHT":
                    board.add(new Knight(color, new Position(x, y), this));
                    break;
            }
            if (hasMoved) {
                getPieceAtPosition(new Position(x, y)).setHasMoved();
            }
        }
    }

    public Set<PieceDTO> getPieceData() {
        return board.stream().map(p -> convertPieceToDTO(p)).collect(Collectors.toSet());
    }

    private PieceDTO convertPieceToDTO(ChessPiece piece) {
        String color = piece.getColor() == PieceColor.WHITE ? "WHITE" : "BLACK";
        Position pos = piece.position;
        boolean hasMoved = piece.getHasMoved();
        return PieceDTO.builder().type(piece.getName()).color(color).x(pos.x).y(pos.y).hasMoved(hasMoved).build();
    }

    public Set<ChessPiece> getPiecesByColor(PieceColor color) {
        return board.stream().filter(p -> p.getColor().equals(color)).collect(Collectors.toSet());
    }

    public Set<ChessPiece> getGraveyard() {
        return graveyard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ChessPiece piece = getPieceAtPosition(new Position(x, y));
                if (piece == null) {
                    sb.append("-");
                } else {
                    sb.append(piece.SYMBOL);
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
