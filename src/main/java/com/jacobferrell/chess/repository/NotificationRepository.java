package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.Notification;
import com.jacobferrell.chess.model.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(long id);

    @Query("SELECT n FROM Notification n WHERE n.to = :user")
    List<Notification> findByRecipient(User user);

    @Query("SELECT n FROM Notification n WHERE n.game = :game AND n.to = :user AND n.read = false")
    List<Notification> findUnreadByGame(GameEntity game, User user);

    @Query("SELECT n FROM Notification n WHERE n.to = :user AND n.read = false")
    List<Notification> findUnreadByUser(User user);

}
