package com.jacobferrell.chess.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.auth.AuthenticationRequest;
import com.jacobferrell.chess.auth.AuthenticationResponse;
import com.jacobferrell.chess.auth.RegisterRequest;
import com.jacobferrell.chess.model.Role;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private GameCreationService gameCreationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    public AuthenticationResponse register(RegisterRequest request) {
        UserDTO user;
        if (request.isDemoUser()) {
            user = buildDemoUser();
            gameCreationService.createDemoGames(user);
            userService.buildFriendship(repository.findByEmail("boomkablamo@gmail.com").orElseThrow(), user);
        } else {
            user = UserDTO.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .build();
            repository.save(user);
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private UserDTO buildDemoUser() {
        var demoUser = UserDTO.builder()
                .firstName("Demo")
                .lastName("User")
                .email("Demo" + (repository.findHighestUserId() + 1) + "@jacob-ferrell.com")
                .password("demo-password")
                .role(Role.DEMO)
                .build();
        repository.save(demoUser);
        return demoUser;
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        if (user.getRole().equals(Role.AI)) {
            throw new AccessDeniedException("Access Denied.  AI users are not accessible");
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
