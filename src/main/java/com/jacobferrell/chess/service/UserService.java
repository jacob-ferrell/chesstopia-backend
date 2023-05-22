package com.jacobferrell.chess.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.webjars.NotFoundException;
import org.springframework.stereotype.Service;

import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser(HttpServletRequest request) {
        User user = jwtService.getUserFromRequest(request);
        if (user == null) {
            throw new NotFoundException("Current user could not be authenticated");
        }
        return user;
    }

    public User addFriend(String email, HttpServletRequest request) {
        //TODO: Make adding friends go both ways, currently it only adds the friend being requested to the list of the user making the request due to concurrency errors
        User user = getCurrentUser(request);
        Optional<User> friend = userRepository.findByEmail(email);
        if (!friend.isPresent()) {
            throw new NotFoundException("User with email: " + email + " not found");
        }
        User foundFriend = friend.get();
        Set<User> userFriends = user.getFriends();
        userFriends.add(foundFriend);
        user.setFriends(userFriends);
        userRepository.save(user);
        return foundFriend;
        /* Set<User> friendFriends = friend.getFriends();
        friendFriends.add(user);
        friend.setFriends(friendFriends);
        userRepository.save(friend); */


    }

}
