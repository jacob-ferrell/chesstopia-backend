package com.jacobferrell.chess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
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
        UserDTO user = userService.getCurrentUser(request);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/add-friend")
    ResponseEntity<?> addFriend(@RequestParam String email, HttpServletRequest request) {
        UserDTO friend = userService.addFriend(email, request);
        return ResponseEntity.ok().body(friend);
    }

}
