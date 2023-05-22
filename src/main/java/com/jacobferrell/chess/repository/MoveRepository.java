package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacobferrell.chess.model.MoveModel;


public interface MoveRepository extends JpaRepository<MoveModel, Long> {
    MoveModel findById(long id);
}
