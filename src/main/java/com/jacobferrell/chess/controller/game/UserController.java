package com.jacobferrell.chess.controller.game;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.service.game.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

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

}
