package com.jacobferrell.chess.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jacobferrell.chess.config.JwtService;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.model.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    private UserRepository userRepository;
    private JwtService jwtService;

    public UserController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/current-user")
    ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        System.out.println("Firing");
        User user =  jwtService.getUserFromRequest(request);
        if (user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return ResponseEntity.ok().body(user);
    }

}
