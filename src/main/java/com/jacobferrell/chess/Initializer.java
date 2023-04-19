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

    @Override
    public void run(String... strings) {
        /* Set<User> players = new HashSet<>();
        String password = passwordEncoder.encode("asdf");
        User player1 = User.builder().name("Jacob").email("boomkablamo@gmail.com").password(password).role(Role.ADMIN).build();
        userRepository.save(player1);
        players.add(player1);
        User player2 = User.builder().name("Cindy").email("cindy@a.com").password(password).role(Role.USER).build();
        userRepository.save(player2);
        players.add(player2);
        GameModel game = GameModel.builder().players(players).build();
        gameRepository.save(game);
        gameRepository.findAll().forEach(System.out::println); */

    }

}
