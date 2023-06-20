package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.Friendship;
import com.jacobferrell.chess.model.UserDTO;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findById(long id);
    
    @Query("SELECT f FROM Friendship f JOIN f.users u WHERE u = :user")
    List<Friendship> findByUser(UserDTO user);

    @Query("SELECT f FROM Friendship f WHERE :user1 MEMBER OF f.users AND :user2 MEMBER OF f.users")
    Optional<Friendship> findByUsers(UserDTO user1, UserDTO user2);
}
