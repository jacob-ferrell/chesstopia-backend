package com.jacobferrell.chess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jacobferrell.chess.game.chessboard.Position;
import com.jacobferrell.chess.game.pieces.PieceType;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "position", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"x", "y"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionEntity {

    public PositionEntity(@NonNull Integer x, @NonNull Integer y) {
        this.x = x;
        this.y = y;
    }

    @Transient
    private static Map<PositionEntity, PositionEntity> CACHE = new HashMap<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false, name = "x", updatable = false)
    private Integer x;

    @Column(nullable = false, name = "y", updatable = false)
    private Integer y;

    @OneToMany(mappedBy = "from")
    @JsonIgnore
    @Builder.Default
    private List<MoveEntity> fromMoves = new ArrayList<>();

    @OneToMany(mappedBy = "to")
    @JsonIgnore
    @Builder.Default
    private List<MoveEntity> toMoves = new ArrayList<>();

    public static PositionEntity of(int x, int y) {
        return CACHE.get(new PositionEntity(x, y));
    }

    public static PositionEntity of(Position position) {
        return CACHE.get(new PositionEntity(position.x(), position.y()));
    }

    public static void preload(List<PositionEntity> fromDb) {
        fromDb.forEach(pE -> CACHE.put(pE, pE));
    }

    public boolean equals(Position position) {
        return x.equals(position.x()) && y.equals(position.y());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionEntity that)) return false;
        return x.equals(that.x) && y.equals(that.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}
