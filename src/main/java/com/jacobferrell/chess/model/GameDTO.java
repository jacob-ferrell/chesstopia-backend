package com.jacobferrell.chess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacobferrell.chess.chessboard.ChessBoard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Builder;



import jakarta.persistence.*;

import java.util.Date;
import java.util.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "games")
public class GameDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "game_players", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserDTO> players;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MoveDTO> moves = new HashSet<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PieceDTO> pieces = new ChessBoard().getPieceData();

    @ManyToOne
    @JoinTable(name = "game_white_player", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private UserDTO whitePlayer;

    @ManyToOne
    @JoinTable(name = "game_black_player", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private UserDTO blackPlayer;

    @ManyToOne
    private UserDTO currentTurn;

    @Builder.Default
    private String playerInCheck = null;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private UserDTO winner;

    @Builder.Default
    @JoinTable(name = "game_over")
    private boolean gameOver = false;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    public boolean getGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean newGameOver) {
        gameOver = newGameOver;
    }

}
