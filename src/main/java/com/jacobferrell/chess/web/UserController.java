package com.jacobferrell.chess.web;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.model.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public UserController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @GetMapping("/current-user")
    ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        User user = jwtService.getUserFromRequest(request);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/add-friend")
    ResponseEntity<?> addFriend(@RequestParam String email, HttpServletRequest request) {
        User user = jwtService.getUserFromRequest(request);
        Optional<User> friend = userRepository.findByEmail(email);
        if (!friend.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        User foundFriend = friend.get();
        addFriend(user, foundFriend);
        return ResponseEntity.ok().body(foundFriend);
    }

    public void addFriend(User user, User friend) {
        Set<User> userFriends = user.getFriends();
        userFriends.add(friend);
        user.setFriends(userFriends);
        userRepository.save(user);

        /* Set<User> friendFriends = friend.getFriends();
        friendFriends.add(user);
        friend.setFriends(friendFriends);
        userRepository.save(friend); */


    }

}
