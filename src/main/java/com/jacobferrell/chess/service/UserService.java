package com.jacobferrell.chess.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.security.access.AccessDeniedException;
import org.webjars.NotFoundException;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

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

    public UserDTO addFriend(String email, HttpServletRequest request) {
        //TODO: Make adding friends go both ways, currently it only adds the friend being requested to the list of the user making the request due to concurrency errors
        UserDTO user = getCurrentUser(request);
        Optional<UserDTO> friend = userRepository.findByEmail(email);
        if (!friend.isPresent()) {
            throw new NotFoundException("User with email: " + email + " not found");
        }
        UserDTO foundFriend = friend.get();
        Set<UserDTO> userFriends = user.getFriends();
        userFriends.add(foundFriend);
        user.setFriends(userFriends);
        userRepository.save(user);
        return foundFriend;
        /* Set<User> friendFriends = friend.getFriends();
        friendFriends.add(user);
        friend.setFriends(friendFriends);
        userRepository.save(friend); */


    }

    public Set<UserDTO> getFriends(long userId, HttpServletRequest request) {
        UserDTO user = getCurrentUser(request);
        if (userId != user.getId()) {
            throw new AccessDeniedException("Access Denied");
        }
        return user.getFriends();
    }

}
