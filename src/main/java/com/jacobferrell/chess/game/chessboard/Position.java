package com.jacobferrell.chess.game.chessboard;
import com.jacobferrell.chess.model.PositionEntity;
import lombok.NonNull;

import java.util.Objects;

public record Position(int x, int y) {

    public static boolean isValid(int x, int y) {
        return Math.min(x, y) >= 0 && Math.max(x, y) <= 7;
    }

    public boolean isValid() {
        return isValid(this.x, this.y);
    }

    public PositionEntity toEntity() {
        return PositionEntity.of(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Position(int x1, int y1))) {
            return false;
        }
        return x == x1 && y == y1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    @NonNull
    public String toString() {
        return "[ Y: "  + y + ", X: " + x + " ]";
    }
}
