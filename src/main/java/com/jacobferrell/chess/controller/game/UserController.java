package com.jacobferrell.chess.controller.game;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.service.game.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.getCurrentUser(request));
    }

    @PostMapping("/add-friend")
    ResponseEntity<?> addFriend(@RequestParam String email, HttpServletRequest request) throws URISyntaxException {
        FriendshipEntity friendshipEntity = userService.addFriend(email, request);
        return ResponseEntity.created(new URI("/api/friendships/" + friendshipEntity.getId()))
        .body(friendshipEntity);
    }

    @GetMapping("/friends")
    ResponseEntity<?> getFriends(HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.getFriends(request));
    }

    @PutMapping("/lobby")
    ResponseEntity<?> joinLobby(HttpServletRequest request) {
        return ResponseEntity.ok().body(userService.joinLobby(request));
    }

}
