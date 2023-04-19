package com.jacobferrell.chess.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GameRepository extends JpaRepository<GameModel, Long> {
    GameModel findById(long id);
    
    @Query("SELECT g FROM GameModel g JOIN g.players p WHERE p = :user")
    List<GameModel> findByPlayer(User user);
}
