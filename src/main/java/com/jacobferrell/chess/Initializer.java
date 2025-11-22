package com.jacobferrell.chess;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.pieces.PieceColor;
import com.jacobferrell.chess.game.pieces.PieceType;
import com.jacobferrell.chess.model.*;
import com.jacobferrell.chess.repository.GameEntityRepository;
import com.jacobferrell.chess.repository.PieceEntityRepository;
import com.jacobferrell.chess.repository.PositionEntityRepository;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

@Component
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner {

    private final GameEntityRepository gameEntityRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final PositionEntityRepository positionEntityRepository;

    private final PieceEntityRepository pieceEntityRepository;

    @Value("${app.computer.password}")
    private String COMPUTER_PASSWORD;

    @Transactional
    public void run(String... strings) {
        if (!gameEntityRepository.findAll().isEmpty()) {
                return;
        }

        initializePositions();
        initializePieces();

        var player1 = User.builder()
                .firstName("Jacob")
                .email("boomkablamo@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player1);

        var player2 = User.builder()
                .firstName("Cindy")
                .email("cindy@gmail.com")
                .password(passwordEncoder.encode("asdf"))
                .role(Role.USER)
                .build();
        userRepository.save(player2);

        GameEntity gameEntity = GameEntity.newGame(player1, player2);
        gameEntityRepository.save(gameEntity);

        var computer = User.builder()
                .firstName("Computer")
                .email("computer@chesstopia")
                .password(passwordEncoder.encode(COMPUTER_PASSWORD))
                .role(Role.AI)
                .build();
        userRepository.save(computer);
/*

        GameEntity checkMateTest = GameEntity.builder().winner(null)
                .build();
        gameEntityRepository.save(checkMateTest);
        Set<User> testPlayers = new HashSet<>();
        testPlayers.add(player1);
        testPlayers.add(computer);
        checkMateTest.setWhitePlayer(computer);
        checkMateTest.setBlackPlayer(player1);
        checkMateTest.setCurrentTurn(player1);
        ChessBoard board = new ChessBoard();
        board.setBoardOneMoveFromComputerPromotion();
        checkMateTest.setPieces(board.getPieceData(checkMateTest));
        gameEntityRepository.save(checkMateTest);

        GameEntity kingCounterTest = GameEntity.builder().winner(null)
                .build();
        gameEntityRepository.save(kingCounterTest);
        Set<User> counterTestPlayers = new HashSet<>();
        counterTestPlayers.add(player1);
        counterTestPlayers.add(computer);
        kingCounterTest.setWhitePlayer(computer);
        kingCounterTest.setBlackPlayer(player1);
        kingCounterTest.setCurrentTurn(player1);
        ChessBoard counterTestBoard = new ChessBoard();
        counterTestBoard.setBoardOnlyKings();
        kingCounterTest.setPieces(counterTestBoard.getPieceData(kingCounterTest));
        gameEntityRepository.save(checkMateTest);
*/

        Set<User> players = new HashSet<>();
        players.add(player1);
        players.add(player2);

        gameEntity.setWhitePlayer(player1);
        gameEntity.setBlackPlayer(player2);
        gameEntity.setCurrentTurn(player1);
        gameEntityRepository.save(gameEntity);

    }

    // Populates all possible positions. Position records should never be inserted or updated after init
    private void initializePositions() {

        List<PositionEntity> positions = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                positions.add(new PositionEntity(i, j));
            }
        }

        positionEntityRepository.saveAll(positions);

        PositionEntity.preload(positions);

    }

    // Initializes every type of piece.  There only needs to be one row for each type/color
    private void initializePieces() {

        List<PieceEntity> pieces = new ArrayList<>();

        for (var color : PieceColor.values()) {
            for (var type : PieceType.values()) {
                pieces.add(new PieceEntity(type, color));
            }
        }

        pieceEntityRepository.saveAll(pieces);

        PieceEntity.preload(pieces);

    }


}
