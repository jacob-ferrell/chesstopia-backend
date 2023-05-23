package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacobferrell.chess.model.NotificationDTO;



public interface NotificationRepository extends JpaRepository<NotificationDTO, Long> {
    NotificationDTO findById(long id);
}
