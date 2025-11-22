package com.jacobferrell.chess.service.game.user;

import java.util.*;

import com.jacobferrell.chess.service.JsonService;
import com.jacobferrell.chess.service.JwtService;
import com.jacobferrell.chess.service.game.GameCreationService;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.FriendshipRepository;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final JsonService jsonService;

    private final SimpMessagingTemplate messagingTemplate;

    private final GameCreationService gameCreationService;

    private final NotificationService notificationService;

    public User getCurrentUser(HttpServletRequest request) {
        User user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("Current user could not be found");
        }
        return user;
    }

    public static User getOtherPlayer(User currentUser, GameEntity gameEntity) {
        List<User> players = gameEntity.getPlayers();
        return players.stream().filter(p -> !Objects.equals(p.getId(), currentUser.getId())).findFirst().orElse(null);
    }

    public FriendshipEntity addFriend(String email, HttpServletRequest request) {
        User user = getCurrentUser(request);
        var friend = userRepository.findByEmail(email).orElseThrow();
        FriendshipEntity friendshipEntity = buildFriendship(user, friend);
        String message = user.getFirstName() + "(" + user.getEmail() + ")" + " added you to their friends list";
        notificationService.createNotification(user, friend, message);  
        return friendshipEntity;
    }

    public FriendshipEntity buildFriendship(User user, User friend) {
        Optional<FriendshipEntity> existingFriendship = friendshipRepository.findByUsers(user, friend);
        if (existingFriendship.isPresent()) {
            return existingFriendship.get();
        }
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(friend);
        FriendshipEntity friendshipEntity = FriendshipEntity.builder().users(users).build();
        friendshipRepository.save(friendshipEntity);
        return friendshipEntity;
    }

    public Set<User> getFriends(HttpServletRequest request) {
        User user = getCurrentUser(request);
        List<FriendshipEntity> friendshipEntities = friendshipRepository.findByUser(user);
        Set<User> friends = new HashSet<>();
        for (FriendshipEntity f : friendshipEntities) {
            friends.add(f.getUsers().stream().filter(fr -> !fr.equals(user)).findFirst().get());
        }
        return friends;
    }

    public Object joinLobby(HttpServletRequest request) {
        User user = getCurrentUser(request);
        Set<User> lobby = userRepository.findByInLobby();
        User otherPlayer = lobby.stream().filter(u -> !u.equals(user)).findFirst().orElse(null);
        if (otherPlayer == null) {
            user.setInLobby(true);
            userRepository.save(user);
            lobby.add(user);
            return lobby;
        }
        GameEntity newGameEntity = gameCreationService.createGame(otherPlayer.getId());
        otherPlayer.setInLobby(false);
        user.setInLobby(false);
        userRepository.save(otherPlayer);
        Map<String, Object> map = new HashMap<>();
        map.put("game", newGameEntity.getId());
        messagingTemplate.convertAndSend("/topic/lobby", jsonService.toJSON(map));
        return newGameEntity;
    }
}
