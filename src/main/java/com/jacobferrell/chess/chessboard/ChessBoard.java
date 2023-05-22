package com.jacobferrell.chess.chessboard;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashSet;
import com.jacobferrell.chess.pieces.*;
import com.jacobferrell.chess.model.Piece;

public class ChessBoard {
    public Set<ChessPiece> board;
    private Set<ChessPiece> graveyard = new HashSet<>();

    public ChessBoard() {
        this.board = setBoard();
    }

    public ChessBoard getClone() {
        ChessBoard clonedBoard = new ChessBoard();
        clonedBoard.clearBoard();
        for(ChessPiece piece : board) {
            clonedBoard.board.add(piece.getClone(clonedBoard));
        }
        return clonedBoard;
    }

    private Set<ChessPiece> setBoard() {
        Set<ChessPiece> board = new HashSet<>();
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
        return board;
    }

    public ChessPiece getPieceAtPosition(int x, int y) {
        Position pos = new Position(x, y);
        return board.stream().filter(p -> p.position.equals(pos)).findFirst().orElse(null);
    }

    public boolean isSpaceOccupied(int x, int y) {
        Position pos = new Position(x, y);
        ChessPiece piece = board.stream().filter(p -> p.position.equals(pos)).findFirst().orElse(null);
        return piece != null;
    }

    public Set<Move> getAllPossibleMoves() {
        Set<Move> allPossibleMoves = new HashSet<>();
        for (ChessPiece piece : board) {
            Set<Position> possiblePositions = piece.generatePossibleMoves();
            for (Position pos : possiblePositions) {
                allPossibleMoves.add(new Move(piece, pos));
            }
        }
        return allPossibleMoves;

    }

    public void setPieceAtPosition(int x, int y, ChessPiece piece) {
        ChessPiece takenPiece = getPieceAtPosition(x, y);
        if (takenPiece != null && piece.isEnemyPiece(takenPiece)) {   
            graveyard.add(takenPiece);
            board.remove(takenPiece);
        }
        piece.position = new Position(x, y);
        piece.setHasMoved();
    }

    public void removePieceAtPosition(int x, int y) {
        ChessPiece piece = getPieceAtPosition(x, y);
        if (piece == null) return;
        board.remove(piece);
    }

    public Set<King> getKings() {
        Set<King> kings = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isSpaceOccupied(i, j)) {
                    continue;
                }
                ChessPiece piece = getPieceAtPosition(i, j);

                if (!(piece instanceof King)) {
                    continue;
                }
                kings.add((King) piece);
            }
        }
        return kings;
    }

    public King getPlayerKing(PieceColor color) {
        Set<King> kings = getKings();
        return kings.stream().filter(king -> king.getColor() == color).findFirst().get();
    }

    public boolean hasBothKings() {
        Set<King> kings = getKings();
        boolean hasWhiteKing = false;
        boolean hasBlackKing = false;
        for (King king : kings) {
            if (king.getColor() == PieceColor.BLACK) {
                hasBlackKing = true;
                continue;
            }
            hasWhiteKing = true;
        }
        return kings.size() == 2 && hasWhiteKing && hasBlackKing;
    }

    public void clearBoard() {
        this.board = new HashSet<>();
    }

    // Return a Set of Rooks which are capable of being castled for a given player
    public Set<Rook> getCastleRooks(PieceColor color) {
        Set<Rook> castleRooks = new HashSet<>();
        King king = getPlayerKing(color);
        //Return empty set if player's king has moved
        if (king.hasMoved) {
            return castleRooks;
        }
        //Get all players rooks which have not been moved
        Set<Rook> rooks = board.stream()
                .filter(piece -> (piece instanceof Rook) && !piece.hasMoved && piece.getColor().equals(color))
                .map(r -> (Rook) r).collect(Collectors.toSet());

        //Test if all spaces between king and given rook are empty, and also are not in the path of any enemy pieces
        outerloop: for (Rook rook : rooks) {
            int kingX = king.position.x;
            int rookX = rook.position.x;
            int y = king.position.y;
            int max = Math.max(kingX, rookX);
            int min = Math.min(kingX, rookX);
            for (int n = min + 1; n < max; n++) {
                if (isSpaceOccupied(n, y)) {
                    continue outerloop;
                }
                Move move = new Move(king, new Position(n, y));
                ChessBoard clonedBoard = move.simulateMove(this);
                if (clonedBoard.getPlayerKing(color).isInCheck()) {
                    continue outerloop;
                }
                castleRooks.add((Rook) rook);
            }
        }
        return castleRooks;
    }

    public void setBoardFromData(Set<Piece> pieces) {
        clearBoard();
        for (Piece piece : pieces) {
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
                getPieceAtPosition(x, y).setHasMoved();
            }
        }
    }

    public Set<Piece> getPieceData() {
        Set<Piece> pieces = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isSpaceOccupied(i, j)) {
                    continue;
                }
                ChessPiece piece = getPieceAtPosition(i, j);
                String color = piece.getColor() == PieceColor.WHITE ? "WHITE" : "BLACK";
                boolean hasMoved = piece.getHasMoved();
                pieces.add(Piece.builder().type(piece.getName()).color(color).x(i).y(j).hasMoved(hasMoved).build());
            }
        }
        return pieces;
    }

    public Set<ChessPiece> getGraveyard() {
        return graveyard;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ChessPiece piece = getPieceAtPosition(x, y);
                if (piece == null) {
                    sb.append("-");
                } else {
                    sb.append(piece.getSymbol());
                }
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

}
