package com.jacobferrell.chess.service.game.user;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.model.FriendshipEntity;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.FriendshipRepository;
import com.jacobferrell.chess.repository.UserRepository;
import com.jacobferrell.chess.service.game.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final FriendshipRepository friendshipRepository;

    private final NotificationService notificationService;

    public static User getOtherPlayer(User currentUser, GameEntity gameEntity) {
        List<User> players = gameEntity.getPlayers();
        return players.stream().filter(p -> !Objects.equals(p.getId(), currentUser.getId())).findFirst().orElse(null);
    }

    public FriendshipEntity addFriend(String email) {
        User user = SecurityUtils.getCurrentUser();
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

    public Set<User> getFriends() {
        User user = SecurityUtils.getCurrentUser();
        List<FriendshipEntity> friendshipEntities = friendshipRepository.findByUser(user);
        Set<User> friends = new HashSet<>();
        for (FriendshipEntity f : friendshipEntities) {
            friends.add(f.getUsers().stream().filter(fr -> !fr.equals(user)).findFirst().get());
        }
        return friends;
    }

}
