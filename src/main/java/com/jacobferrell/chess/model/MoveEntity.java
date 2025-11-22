package com.jacobferrell.chess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacobferrell.chess.game.pieces.Move;
import lombok.*;

import java.util.Date;


import jakarta.persistence.*;

@Data
@Entity
@Builder
@Table(name = "move")
@NoArgsConstructor
@AllArgsConstructor
public class MoveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private GameEntity game;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private PieceEntity piece;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private PositionEntity from;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private PositionEntity to;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Transient
    @JsonIgnore
    @ToString.Exclude
    private GamePiecePosition gamePiecePosition;

    public static MoveEntity fromMove(GamePiecePosition gamePiecePosition, Move move) {
        return MoveEntity
                .builder()
                .game(gamePiecePosition.getGame())
                .piece(gamePiecePosition.getPiece())
                .from(PositionEntity.of(move.getFrom()))
                .to(PositionEntity.of(move.getTo()))
                .gamePiecePosition(gamePiecePosition)
                .build();
    }

}
