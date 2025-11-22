package com.jacobferrell.chess.repository;

import com.jacobferrell.chess.model.GamePiecePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GamePieceMappingRepository extends JpaRepository<GamePiecePosition, Long> {
}
