package com.jacobferrell.chess.controller.game;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.model.Notification;
import com.jacobferrell.chess.service.game.notification.NotificationService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications() {
        return ResponseEntity.ok().body(notificationService.getUserNotifications());
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id) {
        return ResponseEntity.ok().body(notificationService.updateNotification(id));
    }

    @PutMapping("/notifications/mark-all-as-read")
    public ResponseEntity<?> markAllAsReadForUser() {
        notificationService.markAllAsReadForUser();
        return ResponseEntity.ok().body(null);
    }


}
