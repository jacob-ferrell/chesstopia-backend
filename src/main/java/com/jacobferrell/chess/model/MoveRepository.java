package com.jacobferrell.chess.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository extends JpaRepository<MoveModel, Long> {
    MoveModel findById(long id);
}
