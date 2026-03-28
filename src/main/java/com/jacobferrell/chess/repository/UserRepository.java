package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.User;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = com.jacobferrell.chess.model.Role.AI ORDER BY u.id ASC")
    Optional<User> findAIUser();

    @Query("SELECT u FROM User u WHERE u.role = com.jacobferrell.chess.model.Role.DEMO")
    Set<User> findDemoUsers();

    @Query("SELECT MAX(u.id) FROM User u")
    Optional<Long> findHighestUserId();

}
