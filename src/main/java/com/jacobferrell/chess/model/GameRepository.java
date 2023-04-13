package com.jacobferrell.chess.model;

import org.springframework.data.jpa.repository.JpaRepository;


public interface GameRepository extends JpaRepository<GameModel, Long> {
    GameModel findById(long id);
}
