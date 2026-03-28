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

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.service.game.user.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/current-user")
    ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok().body(SecurityUtils.getCurrentUser());
    }

    @PostMapping("/add-friend")
    ResponseEntity<?> addFriend(@RequestParam String email) throws URISyntaxException {
        FriendshipEntity friendshipEntity = userService.addFriend(email);
        return ResponseEntity.created(new URI("/api/friendships/" + friendshipEntity.getId()))
        .body(friendshipEntity);
    }

    @GetMapping("/friends")
    ResponseEntity<?> getFriends() {
        return ResponseEntity.ok().body(userService.getFriends());
    }

    @PutMapping("/lobby")
    ResponseEntity<?> joinLobby() {
        return ResponseEntity.ok().body(userService.joinLobby());
    }

}
