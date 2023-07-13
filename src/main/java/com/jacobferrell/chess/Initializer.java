package com.jacobferrell.chess;

import com.jacobferrell.chess.chessboard.ChessBoard;
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
        /* GameDTO game = GameDTO.builder().winner(null)
                .build();
        gameRepository.save(game);

        var player1 = UserDTO.builder()
                .firstName("Jacob")
                .email("boomkablamo@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player1);
        var player2 = UserDTO.builder()
                .firstName("Cindy")
                .email("cindy@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player2);
        var computer = UserDTO.builder().firstName("Computer").email("computer@chesstopia")
                .password(passwordEncoder.encode(COMPUTER_PASSWORD)).role(Role.AI).build();
        userRepository.save(computer);

        GameDTO checkMateTest = GameDTO.builder().winner(null)
                .build();
        gameRepository.save(checkMateTest);
        Set<UserDTO> testPlayers = new HashSet<>();
        testPlayers.add(player1);
        testPlayers.add(computer);
        checkMateTest.setPlayers(testPlayers);
        checkMateTest.setWhitePlayer(computer);
        checkMateTest.setBlackPlayer(player1);
        checkMateTest.setCurrentTurn(player1);
        ChessBoard board = new ChessBoard();
        board.setBoardOneMoveFromComputerPromotion();
        checkMateTest.setPieces(board.getPieceData());
        gameRepository.save(checkMateTest);

        GameDTO kingCounterTest = GameDTO.builder().winner(null)
                .build();
        gameRepository.save(kingCounterTest);
        Set<UserDTO> counterTestPlayers = new HashSet<>();
        counterTestPlayers.add(player1);
        counterTestPlayers.add(computer);
        kingCounterTest.setPlayers(testPlayers);
        kingCounterTest.setWhitePlayer(computer);
        kingCounterTest.setBlackPlayer(player1);
        kingCounterTest.setCurrentTurn(player1);
        ChessBoard counterTestBoard = new ChessBoard();
        counterTestBoard.setBoardOnlyKings();
        kingCounterTest.setPieces(counterTestBoard.getPieceData());
        gameRepository.save(checkMateTest);

        Set<UserDTO> players = new HashSet<>();
        players.add(player1);
        players.add(player2);

        game.setPlayers(players);
        game.setWhitePlayer(player1);
        game.setBlackPlayer(player2);
        game.setCurrentTurn(player1);
        gameRepository.save(game); */

    }

}
