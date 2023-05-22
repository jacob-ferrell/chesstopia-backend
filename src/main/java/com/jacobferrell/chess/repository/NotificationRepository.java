package com.jacobferrell.chess.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jacobferrell.chess.model.Notification;



public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Notification findById(long id);
}
