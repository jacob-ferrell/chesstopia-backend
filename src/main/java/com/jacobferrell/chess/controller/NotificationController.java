package com.jacobferrell.chess.controller;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.model.NotificationDTO;
import com.jacobferrell.chess.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications/user/{id}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(notificationService.getUserNotifications(id, request));
    }

    @PutMapping("/notifications/{id}")
    public ResponseEntity<NotificationDTO> updateNotification(@PathVariable Long id, HttpServletRequest request) {
        return ResponseEntity.ok().body(notificationService.updateNotification(id, request));
    }


}
