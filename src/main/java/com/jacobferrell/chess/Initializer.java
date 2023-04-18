package com.jacobferrell.chess;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.chessboard.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.*;

@Component
public class Initializer implements CommandLineRunner {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;


    public Initializer(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... strings) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode("asdf");
        User player1 = User.builder().name("Jacob").email("boomkablamo@gmail.com").password(password).role(Role.ADMIN).build();
        userRepository.save(player1);
        User player2 = User.builder().name("Cindy").email("cindy@a.com").password(password).role(Role.USER).build();
        userRepository.save(player2);
        GameModel game = GameModel.builder().player1(player1).player2(player2).build();
        gameRepository.save(game);
        gameRepository.findAll().forEach(System.out::println);

    }

}
