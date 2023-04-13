package com.jacobferrell.chess.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import jakarta.persistence.*;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "moves")
public class MoveModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "piece_id")
    private Piece piece;

    @Column(name = "from_x")
    private int fromX;

    @Column(name = "from_y")
    private int fromY;

    @Column(name = "to_x")
    private int toX;

    @Column(name = "to_y")
    private int toY;

}
