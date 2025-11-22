package com.jacobferrell.chess.model;

import com.jacobferrell.chess.game.pieces.ChessPiece;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.game.pieces.PieceType;
import lombok.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "piece", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "color"})
})
public class PieceEntity {

    public PieceEntity(
            @NonNull PieceType type,
            @NonNull PieceColor color
    ) {
        this.type = type;
        this.color = color;
    }

    private static Map<PieceEntity, PieceEntity> CACHE = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false)
    private PieceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false, updatable = false)
    private PieceColor color;

    public static void preload(List<PieceEntity> fromDb) {
        fromDb.forEach(pE -> CACHE.put(pE, pE));
    }

    public static PieceEntity of(PieceType type, PieceColor color) {
        return CACHE.get(new PieceEntity(type, color));
    }

    public static PieceEntity of(ChessPiece piece) {
        return of(piece.getType(), piece.getColor());
    }

    public boolean equals(ChessPiece chessPiece) {
        return chessPiece.getType() == type && chessPiece.getColor() == color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieceEntity that)) return false;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

}
