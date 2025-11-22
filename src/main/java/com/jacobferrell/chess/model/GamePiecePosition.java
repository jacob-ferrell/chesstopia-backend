package com.jacobferrell.chess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacobferrell.chess.game.pieces.ChessPiece;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "game_piece_mapping", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"game_id", "piece_id", "position_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GamePiecePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "game_id")
    @JsonIgnore
    private GameEntity game;

    @ManyToOne
    @JoinColumn(nullable = false, name = "piece_id")
    private PieceEntity piece;

    @ManyToOne
    @JoinColumn(nullable = false, name = "position_id")
    private PositionEntity position;

    @Builder.Default
    @Column(nullable = false)
    private Boolean hasMoved = false;

    public boolean equals(ChessPiece chessPiece) {
        return position.equals(chessPiece.getPosition()) && piece.equals(chessPiece);
    }

}
