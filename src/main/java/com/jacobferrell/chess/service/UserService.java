package com.jacobferrell.chess.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.webjars.NotFoundException;
import org.springframework.stereotype.Service;

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

    public UserDTO getCurrentUser(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("Current user could not be authenticated");
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

    public List<Friendship> getFriends(HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        return friendshipRepository.findByUser(user);
    }

}
