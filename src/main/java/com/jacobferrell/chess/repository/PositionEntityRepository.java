package com.jacobferrell.chess.repository;

import com.jacobferrell.chess.model.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionEntityRepository extends JpaRepository<PositionEntity, Long> {
}
