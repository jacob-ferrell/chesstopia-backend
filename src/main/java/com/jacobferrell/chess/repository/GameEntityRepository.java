package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;

@Repository
public interface GameEntityRepository extends JpaRepository<GameEntity, Long> {

    Optional<GameEntity> findById(long id);

    @Query("SELECT g FROM GameEntity g WHERE g.blackPlayer = :user OR g.whitePlayer = :user")
    List<GameEntity> findAllByUser(User user);

}
