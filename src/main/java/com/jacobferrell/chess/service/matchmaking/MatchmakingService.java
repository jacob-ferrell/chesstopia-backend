package com.jacobferrell.chess.service.matchmaking;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.service.game.GameCreationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final GameCreationService gameCreationService;

    private final SimpMessagingTemplate messagingTemplate;

    private final ConcurrentHashMap<Long, User> lobby = new ConcurrentHashMap<>();

    public synchronized void tryMatch(User user) {
        User opponent = lobby.entrySet().stream()
                .filter(e -> !e.getKey().equals(user.getId()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);

        if (opponent == null) {
            lobby.put(user.getId(), user);
            return;
        }

        GameEntity newGame = gameCreationService.createGame(user, opponent);

        var messageBody = Map.of("gameId", newGame.getId());

        List.of(user, opponent).forEach(u ->
                messagingTemplate.convertAndSend(
                        "/topic/matchmaking/%d".formatted(u.getId()),
                        messageBody
        ));

        lobby.remove(opponent.getId());
    }

    public void removeFromQueue(long userId) {
        lobby.remove(userId);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = accessor.getUser();
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof User user) {
            removeFromQueue(user.getId());
        }
    }

}
