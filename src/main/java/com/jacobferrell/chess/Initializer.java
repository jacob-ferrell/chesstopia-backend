package com.jacobferrell.chess;

import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.repository.GameRepository;
import com.jacobferrell.chess.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Component
public class Initializer implements CommandLineRunner {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.computer.password}")
    private String COMPUTER_PASSWORD;

    public void run(String... strings) {
        GameDTO game = GameDTO.builder().winner(null)
                .build();
        gameRepository.save(game);

        var player1 = UserDTO.builder()
                .name("Jacob")
                .email("boomkablamo@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player1);
        var player2 = UserDTO.builder()
                .name("Cindy")
                .email("cindy@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player2);
        System.out.println(COMPUTER_PASSWORD);
        var computer = UserDTO.builder().name("Computer").email("computer@chesstopia")
                .password(passwordEncoder.encode(COMPUTER_PASSWORD)).role(Role.AI).build();
        userRepository.save(computer);

        Set<UserDTO> players = new HashSet<>();
        players.add(player1);
        players.add(player2);

        game.setPlayers(players);
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player2);
        game.setCurrentTurn(player1);
        gameRepository.save(game);

    }

}
