package com.jacobferrell.chess.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private JsonService jsonService;

    public List<NotificationDTO> getUserNotifications(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        return notificationRepository.findByRecipient(user);
    }

    public List<NotificationDTO> getUnreadNotifications(HttpServletRequest request) {
        UserDTO user = jwtService.getUserFromRequest(request);
        return notificationRepository.findUnreadByUser(user);
    }

    public NotificationDTO createNotification(UserDTO sender, UserDTO recipient, String message) {
        NotificationDTO notification = NotificationDTO.builder().to(recipient).message(message).build();
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

    public void markAllAsRead(HttpServletRequest request) {
        List<NotificationDTO> unreadNotifications = getUnreadNotifications(request);
        markAsRead(unreadNotifications);
    }

    public void markAsRead(List<NotificationDTO> notifications) {
        for (NotificationDTO n : notifications) {
            n.setRead(true);
            notificationRepository.save(n);
        }
    }

    public List<NotificationDTO> markAsReadForGame(GameDTO game, UserDTO user) {
        List<NotificationDTO> unreadNotifications = notificationRepository.findUnreadByGame(game, user);
        markAsRead(unreadNotifications);
        return unreadNotifications;
    }
}
