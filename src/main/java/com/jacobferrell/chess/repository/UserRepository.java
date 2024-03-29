package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.UserDTO;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserDTO, Long> {
    Optional<UserDTO> findByEmail(String email);

    @Query("SELECT u FROM UserDTO u WHERE u.inLobby = true")
    Set<UserDTO> findByInLobby();

    @Query("SELECT u FROM UserDTO u WHERE u.role = com.jacobferrell.chess.model.Role.AI ORDER BY u.id ASC")
    Optional<UserDTO> findAIUser();

    @Query("SELECT u FROM UserDTO u WHERE u.role = com.jacobferrell.chess.model.Role.DEMO")
    Set<UserDTO> findDemoUsers();

    @Query("SELECT MAX(u.id) FROM UserDTO u")
    Long findHighestUserId();

}
