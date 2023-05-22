package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jacobferrell.chess.model.GameModel;
import com.jacobferrell.chess.model.User;


public interface GameRepository extends JpaRepository<GameModel, Long> {
    Optional<GameModel> findById(long id);
    
    @Query("SELECT g FROM GameModel g JOIN g.players p WHERE p = :user")
    List<GameModel> findByPlayer(User user);
}
