package com.jacobferrell.chess.game;
import com.jacobferrell.chess.chessboard.*;
import com.jacobferrell.chess.model.GameDTO;

public class Game {
    private Player player1;
    private Player player2;
    public ChessBoard board;

    public Game(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.board = new ChessBoard();
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }
}
