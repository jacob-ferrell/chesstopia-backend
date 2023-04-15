package com.jacobferrell.chess.chessboard;

import java.util.Set;
import java.util.HashSet;
import com.jacobferrell.chess.pieces.*;
import com.jacobferrell.chess.model.Piece;

public class ChessBoard {
    private ChessPiece[][] board;
    private Set<ChessPiece> graveyard = new HashSet<>();

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
        initializeBoard();
    }

    public ChessBoard getClone() {
        ChessBoard clonedBoard = new ChessBoard();
        clonedBoard.clearBoard();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isSpaceOccupied(i, j)) {
                    continue;
                }
                ChessPiece pieceToClone = getPieceAtPosition(i, j);
                ChessPiece clonedPiece = pieceToClone.getClone(clonedBoard);
                clonedBoard.setPieceAtPosition(i, j, clonedPiece);
            }
        }
        return clonedBoard;
    }

    private void initializeBoard() {
        // Initialize black pieces
        board[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0), this);
        board[0][1] = new Knight(PieceColor.BLACK, new Position(1, 0), this);
        board[0][2] = new Bishop(PieceColor.BLACK, new Position(2, 0), this);
        board[0][3] = new Queen(PieceColor.BLACK, new Position(3, 0), this);
        board[0][4] = new King(PieceColor.BLACK, new Position(4, 0), this);
        board[0][5] = new Bishop(PieceColor.BLACK, new Position(5, 0), this);
        board[0][6] = new Knight(PieceColor.BLACK, new Position(6, 0), this);
        board[0][7] = new Rook(PieceColor.BLACK, new Position(7, 0), this);
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(PieceColor.BLACK, new Position(i, 1), this);
        }

        // Initialize white pieces
        board[7][0] = new Rook(PieceColor.WHITE, new Position(0, 7), this);
        board[7][1] = new Knight(PieceColor.WHITE, new Position(1, 7), this);
        board[7][2] = new Bishop(PieceColor.WHITE, new Position(2, 7), this);
        board[7][3] = new Queen(PieceColor.WHITE, new Position(3, 7), this);
        board[7][4] = new King(PieceColor.WHITE, new Position(4, 7), this);
        board[7][5] = new Bishop(PieceColor.WHITE, new Position(5, 7), this);
        board[7][6] = new Knight(PieceColor.WHITE, new Position(6, 7), this);
        board[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7), this);
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn(PieceColor.WHITE, new Position(i, 6), this);
        }
    }

    public ChessPiece getPieceAtPosition(int x, int y) {
        return board[y][x];
    }

    public boolean isSpaceOccupied(int x, int y) {
        return board[y][x] != null;
    }

    public Set<Move> getAllPossibleMoves() {
        Set<Move> allPossibleMoves = new HashSet<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (!isSpaceOccupied(i, j)) {
                    continue;
                }
                ChessPiece piece = getPieceAtPosition(i, j);
                Set<Position> possiblePositions = piece.generatePossibleMoves();
                for (Position pos : possiblePositions) {
                    allPossibleMoves.add(new Move(piece, pos));
                }
            }
        }
        return allPossibleMoves;

    }

    public void setPieceAtPosition(int x, int y, ChessPiece piece) {
        if (isSpaceOccupied(x, y)) {
            ChessPiece takenPiece = getPieceAtPosition(x, y);
            graveyard.add(takenPiece);
        }
        board[y][x] = piece;
        piece.setXPosition(x);
        piece.setYPosition(y);
    }

    public void setPositionToNull(int x, int y) {
        board[y][x] = null;
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                setPositionToNull(i, j);
            }
        }
    }

    public void setBoardFromData(Set<Piece> pieces) {
        clearBoard();
        for (Piece piece : pieces) {
            PieceColor color = piece.getColor() == "WHITE" ? PieceColor.WHITE : PieceColor.BLACK;
            int x = piece.getX();
            int y = piece.getY();
            switch (piece.getType()) {
                case "ROOK":
                    board[y][x] = new Rook(color, new Position(x, y), this);
                    break;
                case "BISHOP":
                    board[y][x] = new Bishop(color, new Position(x, y), this);
                    break;

                case "KING":
                    board[y][x] = new King(color, new Position(x, y), this);
                    break;

                case "PAWN":
                    board[y][x] = new Pawn(color, new Position(x, y), this);
                    break;

                case "QUEEN":
                    board[y][x] = new Queen(color, new Position(x, y), this);
                    break;

                case "KNIGHT":
                    board[y][x] = new Knight(color, new Position(x, y), this);
                    break;
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
                pieces.add(Piece.builder().type(piece.getName()).color(color).x(i).y(j).build());
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
                ChessPiece piece = board[y][x];
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
