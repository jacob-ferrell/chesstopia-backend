package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.MoveDTO;

@Repository
public interface MoveRepository extends JpaRepository<MoveDTO, Long> {
    MoveDTO findById(long id);
}
