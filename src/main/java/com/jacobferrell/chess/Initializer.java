package com.jacobferrell.chess;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.chessboard.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Component
public class Initializer implements CommandLineRunner {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public Initializer(GameRepository gameRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(String... strings) {
        GameModel game = GameModel.builder()
            .build();
        gameRepository.save(game);

        var player1 = User.builder()
            .name("Jacob")
            .email("boomkablamo@gmail.com")
            .password(passwordEncoder.encode("asdf"))
            .role(Role.USER)
            .build();
        userRepository.save(player1);
        var player2 = User.builder()
            .name("Cindy")
            .email("cindy@gmail.com")
            .password(passwordEncoder.encode("asdf"))
            .role(Role.USER)
            .build();
        userRepository.save(player2);

        Set<User> players = new HashSet<>();
        players.add(player1);
        players.add(player2);

        game.setPlayers(players);
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player2);
        game.setCurrentTurn(player1);
        gameRepository.save(game);

    }

}
