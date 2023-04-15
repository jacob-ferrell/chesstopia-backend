package com.jacobferrell.chess.model;

import lombok.Data;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "pieces")
public class Piece {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String color;

    private int x;

    private int y;
}
