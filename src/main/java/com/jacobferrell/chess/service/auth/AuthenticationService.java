package com.jacobferrell.chess.service.auth;

import com.jacobferrell.chess.service.game.GameCreationService;
import com.jacobferrell.chess.service.JwtService;
import com.jacobferrell.chess.service.game.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.auth.AuthenticationRequest;
import com.jacobferrell.chess.auth.AuthenticationResponse;
import com.jacobferrell.chess.auth.RegisterRequest;
import com.jacobferrell.chess.model.Role;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final GameCreationService gameCreationService;

    private final AuthenticationManager authenticationManager;

    private final UserService userService;

    public AuthenticationResponse register(RegisterRequest request) {
        User user;
        if (request.isDemoUser()) {
            user = buildDemoUser();
            gameCreationService.createDemoGames(user);
            userService.buildFriendship(repository.findByEmail("boomkablamo@gmail.com").orElseThrow(), user);
        } else {
            user = fromRequest(request);
            repository.save(user);
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private User fromRequest(RegisterRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
    }
    private User buildDemoUser() {
        var demoUser = User.builder()
                .firstName("Demo")
                .lastName("User")
                .email("Demo" + (repository.findHighestUserId().orElse(0L) + 1) + "@jacob-ferrell.com")
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
