package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacobferrell.chess.model.MoveDTO;


public interface MoveRepository extends JpaRepository<MoveDTO, Long> {
    MoveDTO findById(long id);
}
