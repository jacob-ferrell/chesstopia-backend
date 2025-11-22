package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.model.User;

@Repository
public interface FriendshipRepository extends JpaRepository<FriendshipEntity, Long> {
    Optional<FriendshipEntity> findById(long id);
    
    @Query("SELECT f FROM FriendshipEntity f JOIN f.users u WHERE u = :user")
    List<FriendshipEntity> findByUser(User user);

    @Query("SELECT f FROM FriendshipEntity f WHERE :user1 MEMBER OF f.users AND :user2 MEMBER OF f.users")
    Optional<FriendshipEntity> findByUsers(User user1, User user2);
}
