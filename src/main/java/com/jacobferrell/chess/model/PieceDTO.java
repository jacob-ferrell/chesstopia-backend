package com.jacobferrell.chess.model;

import lombok.Data;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "pieces")
public class PieceDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private String color;

    private int x;

    private int y;

    @JsonIgnore
    @Builder.Default
    private int moveCount = 0;

    @JsonIgnore
    @Builder.Default
    private boolean hasMoved = false;

    public boolean getHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean newHasMoved) {
        hasMoved = newHasMoved;
    }
}
