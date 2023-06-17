package com.jacobferrell.chess.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameDTO;
import com.jacobferrell.chess.model.NotificationDTO;
import com.jacobferrell.chess.model.UserDTO;
import com.jacobferrell.chess.repository.NotificationRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class NotificationService {

    @Autowired 
    private NotificationRepository notificationRepository;

    @Autowired 
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonService jsonService;

    public List<NotificationDTO> getUserNotifications(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        return notificationRepository.findByRecipient(user);
    }

    public NotificationDTO createNotification(UserDTO sender, GameDTO game) {
        UserDTO recipient = userService.getOtherPlayer(sender, game);
        NotificationDTO notification = NotificationDTO.builder().game(game).to(recipient).build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/user/" + recipient.getId(), jsonService.toJSON(notification));
        return notification;
    }

    public NotificationDTO updateNotification(long id, HttpServletRequest request) {
        Optional<NotificationDTO> notification = notificationRepository.findById(id);
        if (!notification.isPresent()) {
            throw new NotFoundException("Notification with id: " + id + " could not be found");
        }
        NotificationDTO foundNotification = notification.get();
        UserDTO user = jwtService.getUserFromRequest(request);
        if (!foundNotification.getTo().equals(user)) {
            throw new AccessDeniedException("Access Denied");
        }
        foundNotification.setRead(true);
        notificationRepository.save(foundNotification);
        return foundNotification;
    }

    public List<NotificationDTO> markAsReadForGame(GameDTO game, UserDTO user) {
        List<NotificationDTO> unreadNotifications = notificationRepository.findUnreadByGame(game, user);
        for (NotificationDTO n : unreadNotifications) {
            n.setRead(true);
            notificationRepository.save(n);
        }
        return unreadNotifications;
    }
}
