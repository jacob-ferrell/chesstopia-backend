package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.UserDTO;


public interface GameRepository extends JpaRepository<GameDTO, Long> {
    Optional<GameDTO> findById(long id);
    
    @Query("SELECT g FROM GameDTO g JOIN g.players p WHERE p = :user")
    List<GameDTO> findByPlayer(UserDTO user);
}
