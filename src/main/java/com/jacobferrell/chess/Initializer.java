package com.jacobferrell.chess;

import com.jacobferrell.chess.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


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

        User player1 = User.builder().name("Jacob").build();
        userRepository.save(player1);
        User player2 = User.builder().name("Cindy").build();
        userRepository.save(player2);
        Set<Piece> pieces = createPieces();
        GameModel game = GameModel.builder().player1(player1).player2(player2).build();
        game.setPieces(pieces);
        gameRepository.save(game);
        gameRepository.findAll().forEach(System.out::println);

    }
    public Set<Piece> createPieces() {
        Set<Piece> pieces = new HashSet<>();
        pieces.add(Piece.builder().type("ROOK").color("BLACK").x(0).y(0).build());
        pieces.add(Piece.builder().type("ROOK").color("BLACK").x(7).y(0).build());
        pieces.add(Piece.builder().type("KNIGHT").color("BLACK").x(1).y(0).build());
        pieces.add(Piece.builder().type("KNIGHT").color("BLACK").x(6).y(0).build());
        pieces.add(Piece.builder().type("BISHOP").color("BLACK").x(2).y(0).build());
        pieces.add(Piece.builder().type("BISHOP").color("BLACK").x(5).y(0).build());
        pieces.add(Piece.builder().type("QUEEN").color("BLACK").x(3).y(0).build());
        pieces.add(Piece.builder().type("KING").color("BLACK").x(4).y(0).build());
        for (int i = 0; i < 8; i++) {
            pieces.add(Piece.builder().type("PAWN").color("BLACK").x(i).y(1).build());
        }
        pieces.add(Piece.builder().type("ROOK").color("WHITE").x(0).y(7).build());
        pieces.add(Piece.builder().type("ROOK").color("WHITE").x(7).y(7).build());
        pieces.add(Piece.builder().type("KNIGHT").color("WHITE").x(1).y(7).build());
        pieces.add(Piece.builder().type("KNIGHT").color("WHITE").x(6).y(7).build());
        pieces.add(Piece.builder().type("BISHOP").color("WHITE").x(2).y(7).build());
        pieces.add(Piece.builder().type("BISHOP").color("WHITE").x(5).y(7).build());
        pieces.add(Piece.builder().type("QUEEN").color("WHITE").x(3).y(7).build());
        pieces.add(Piece.builder().type("KING").color("WHITE").x(4).y(7).build());
        for (int i = 0; i < 8; i++) {
            pieces.add(Piece.builder().type("PAWN").color("WHITE").x(i).y(6).build());
        }
        return pieces;
    }

}
