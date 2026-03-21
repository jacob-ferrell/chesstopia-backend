package com.jacobferrell.chess.model;

import com.jacobferrell.chess.game.chessboard.ChessBoard;
import com.jacobferrell.chess.game.pieces.ChessPiece;
import com.jacobferrell.chess.game.pieces.PieceColor;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "game")
@ToString
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @ToString.Exclude
    @JoinColumn(nullable = false)
    private User whitePlayer;

    @ManyToOne(optional = false)
    @ToString.Exclude
    @JoinColumn(nullable = false)
    private User blackPlayer;

    @ManyToOne(optional = false)
    @ToString.Exclude
    private User currentTurn;

    private PieceColor playerInCheck;

    @ManyToOne
    private User winner;

    @Builder.Default
    private Boolean gameOver = false;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "game")
    private List<MoveEntity> moves = new ArrayList<>();

    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL,
            mappedBy = "game",
            orphanRemoval = true
    )
    @ToString.Exclude
    private List<GamePiecePosition> pieces;

    public void setWinnerFromLoser(User loser) {
        if (whitePlayer.equals(loser)) winner = blackPlayer;
        if (blackPlayer.equals(loser)) winner = whitePlayer;
    }

    public static GameEntity newGame(User white, User black) {
        GameEntity game = GameEntity.builder()
                .whitePlayer(white)
                .blackPlayer(black)
                .currentTurn(white)
                .build();

        List<GamePiecePosition> mappings = new ChessBoard().toGamePieceMappings();
        mappings.forEach(mapping -> mapping.setGame(game));

        game.setPieces(mappings);

        return game;
    }

    public void overwritePieces(Collection<GamePiecePosition> livePieces) {
        if (this.pieces == null) this.pieces = new ArrayList<>();
        // Collect the IDs of live pieces (positions already updated in-place by copyToGamePieceMapping)
        Set<Long> liveIds = livePieces.stream()
                .map(GamePiecePosition::getId)
                .collect(Collectors.toSet());
        // Only remove captured pieces — never clear() the whole list, as that
        // triggers Hibernate orphanRemoval on re-added entities (known Hibernate bug).
        this.pieces.removeIf(p -> p.getId() != null && !liveIds.contains(p.getId()));
    }

    public void switchTurns() {
        this.currentTurn = getOpponent(currentTurn);
    }

    public List<User> getPlayers() {
        return List.of(whitePlayer, blackPlayer);
    }

    public User getOpponent(User user) {
        if (user.equals(whitePlayer)) return blackPlayer;
        if (user.equals(blackPlayer)) return whitePlayer;
        throw new AccessDeniedException("Current user does not have access to this game");
    }

    public void validatePlayer(User user) {
        if (!whitePlayer.equals(user) && !blackPlayer.equals(user)) {
            throw new AccessDeniedException("Current user does not have access to this game");
        }
    }

    public void validateIsPlayersTurn(User user) {
        if(!currentTurn.equals(user)) {
            throw new AccessDeniedException("It is not the current user's turn.");
        }
    }

    public PieceColor getPlayersColor(User user) {
        if (user.equals(whitePlayer)) return PieceColor.WHITE;
        if (user.equals(blackPlayer)) return PieceColor.BLACK;
        throw new AccessDeniedException("User is not a player in this game");
    }

    public GamePiecePosition getGamePieceMapping(ChessPiece piece) {
        return pieces.stream()
                .filter(pieceMapping -> pieceMapping.equals(piece))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Could not find GamePieceMapping matching piece %s".formatted(piece)));
    }

}
