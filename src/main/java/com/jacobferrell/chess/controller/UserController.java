package com.jacobferrell.chess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/current-user")
    ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.getCurrentUser(request));
    }

    @PostMapping("/add-friend")
    ResponseEntity<?> addFriend(@RequestParam String email, HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.addFriend(email, request));
    }

    @GetMapping("/user/{id}/friends")
    ResponseEntity<?> getFriends(@PathVariable Long userId, HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.getFriends(userId, request));
    }

}
