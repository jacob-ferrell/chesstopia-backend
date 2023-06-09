package com.jacobferrell.chess.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Date;


import jakarta.persistence.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "moves")
public class MoveDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pieceType;

    private String pieceColor;

    @Column(name = "from_x")
    private int fromX;

    @Column(name = "from_y")
    private int fromY;

    @Column(name = "to_x")
    private int toX;

    @Column(name = "to_y")
    private int toY;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

}
