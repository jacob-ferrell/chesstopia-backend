package com.jacobferrell.chess.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.Friendship;
import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.FriendshipRepository;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private JsonService jsonService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private GameCreationService gameCreationService;

    public UserDTO getCurrentUser(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("Current user could not be found");
        }
        return user;
    }

    public UserDTO getOtherPlayer(UserDTO currentUser, GameDTO game) {
        Set<UserDTO> players = game.getPlayers();
        return players.stream().filter(p -> p.getId() != currentUser.getId()).findFirst().orElse(null);
    }

    public Friendship addFriend(String email, HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        Optional<UserDTO> friend = userRepository.findByEmail(email);
        if (!friend.isPresent()) {
            throw new NotFoundException("User with email: " + email + " not found");
        }
        UserDTO foundFriend = friend.get();
        Optional<Friendship> existingFriendship = friendshipRepository.findByUsers(user, foundFriend);
        if (existingFriendship.isPresent()) {
            return existingFriendship.get();
        }
        Set<UserDTO> users = new HashSet<>();
        users.add(user);
        users.add(foundFriend);
        Friendship friendship = Friendship.builder().users(users).build();
        friendshipRepository.save(friendship);
        return friendship;
    }

    public Set<UserDTO> getFriends(HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        List<Friendship> friendships = friendshipRepository.findByUser(user);
        Set<UserDTO> friends = new HashSet<>();
        for (Friendship f : friendships) {
            friends.add(f.getUsers().stream().filter(fr -> !fr.equals(user)).findFirst().get());
        }
        return friends;
    }

    public Object joinLobby(HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        Set<UserDTO> lobby = userRepository.findByInLobby();
        UserDTO otherPlayer = lobby.stream().filter(u -> !u.equals(user)).findFirst().orElse(null);
        if (otherPlayer == null) {
            user.setInLobby(true);
            userRepository.save(user);
            lobby.add(user);
            return lobby;
        }
        GameDTO newGame = gameCreationService.createGame(otherPlayer.getId(), request);
        otherPlayer.setInLobby(false);
        userRepository.save(otherPlayer);
        Map<String, Object> map = new HashMap<>();
        map.put("game", newGame.getId());
        messagingTemplate.convertAndSend("/topic/lobby", jsonService.toJSON(map));
        return newGame;  
    }
}
