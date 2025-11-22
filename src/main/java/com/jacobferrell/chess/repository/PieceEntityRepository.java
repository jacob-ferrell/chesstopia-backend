package com.jacobferrell.chess.repository;

import com.jacobferrell.chess.model.PieceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PieceEntityRepository extends JpaRepository<PieceEntity, Long> {
}
