package com.jacobferrell.chess.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.NotificationDTO;
import com.jacobferrell.chess.model.UserDTO;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationDTO, Long> {
    Optional<NotificationDTO> findById(long id);

    @Query("SELECT n FROM NotificationDTO n WHERE n.to = :user")
    List<NotificationDTO> findByRecipient(UserDTO user);

    @Query("SELECT n FROM NotificationDTO n WHERE n.game = :game AND n.to = :user AND n.read = false")
    List<NotificationDTO> findUnreadByGame(GameDTO game, UserDTO user);

    @Query("SELECT n FROM NotificationDTO n WHERE n.to = :user AND n.read = false")
    List<NotificationDTO> findUnreadByUser(UserDTO user);

}
