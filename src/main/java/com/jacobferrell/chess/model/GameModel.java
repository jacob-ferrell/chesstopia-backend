package com.jacobferrell.chess.model;

import com.jacobferrell.chess.chessboard.ChessBoard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Builder;

import java.util.Optional;


import jakarta.persistence.*;
import java.util.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "games")
public class GameModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "game_players", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> players;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MoveModel> moves;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Piece> pieces = new ChessBoard().getPieceData();

    @ManyToOne
    @JoinTable(name = "game_white_player", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User whitePlayer;

    @ManyToOne
    @JoinTable(name = "game_black_player", joinColumns = @JoinColumn(name = "game_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private User blackPlayer;

    @ManyToOne
    private User currentTurn;

    @Builder.Default
    private String playerInCheck = null;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    /* public void setWinner(User user) {
        winner = Optional.ofNullable(user);
    } */

}
