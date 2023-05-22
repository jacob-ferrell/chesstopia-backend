package com.jacobferrell.chess.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GameRepository extends JpaRepository<GameModel, Long> {
    Optional<GameModel> findById(long id);
    
    @Query("SELECT g FROM GameModel g JOIN g.players p WHERE p = :user")
    List<GameModel> findByPlayer(User user);
}
