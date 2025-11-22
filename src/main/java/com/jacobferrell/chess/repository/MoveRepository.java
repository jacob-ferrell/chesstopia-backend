package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.MoveEntity;

@Repository
public interface MoveRepository extends JpaRepository<MoveEntity, Long> {
    MoveEntity findById(long id);
}
