package com.jacobferrell.chess.service.game.notification;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.service.JsonService;
import com.jacobferrell.chess.service.JwtService;
import com.jacobferrell.chess.service.game.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.Notification;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final JwtService jwtService;

    private final JsonService jsonService;

    @Transactional
    public void sendMessageAndNotification(User user, GameEntity gameEntityData) {
        long gameId = gameEntityData.getId();
        String message = user.getFirstName() + " made a move in game " + gameEntityData.getId();
        Notification notification = createNotification(user, UserService.getOtherPlayer(user, gameEntityData), message);
        notification.setGame(gameEntityData);
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/game/" + gameId,
                jsonService.toJSON(getMessageBody(gameEntityData, notification)));
        showPlayerIsConnectedToGame(gameId, true);

    }

    public void showPlayerIsConnectedToGame(long gameId, boolean isConnected) {
        Map<String, Object> map = new HashMap<>();
        map.put("connected", isConnected);
        map.put("player", SecurityUtils.getCurrentUser().getId());
        messagingTemplate.convertAndSend("/topic/game/" + gameId, jsonService.toJSON(map));
    }


    public Map<String, Object> getMessageBody(GameEntity gameEntity, Notification notification) {
        Map<String, Object> body = new HashMap<>();
        body.put("game", gameEntity);
        body.put("notification", notification);
        return body;
    }


    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications() {
        User user = SecurityUtils.getCurrentUser();
        return notificationRepository.findByRecipient(user);
    }

    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications() {
        User user = SecurityUtils.getCurrentUser();
        return notificationRepository.findUnreadByUser(user);
    }

    @Transactional
    public Notification createNotification(User sender, User recipient, String message) {
        Notification notification = Notification.builder().to(recipient).message(message).build();
        notificationRepository.save(notification);
        messagingTemplate.convertAndSend("/topic/user/" + recipient.getId(), jsonService.toJSON(notification));
        return notification;
    }

    @Transactional
    public Notification updateNotification(long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        "Notification with id: " + id + " could not be found"));

        User user = SecurityUtils.getCurrentUser();

        if (!notification.getTo().equals(user)) {
            throw new AccessDeniedException("Access Denied");
        }

        notification.setRead(true);

        notificationRepository.save(notification);

        return notification;
    }

    @Transactional
    public void markAllAsReadForUser() {
        List<Notification> unreadNotifications = getUnreadNotifications();
        markAsRead(unreadNotifications);
    }

    @Transactional
    public void markAsRead(Collection<Notification> notifications) {
        notifications.forEach(this::markAsRead);
    }

    public void markAsRead(Notification notification) {
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsReadForGame(GameEntity gameEntity, User user) {
        notificationRepository.findUnreadByGame(gameEntity, user)
                .forEach(this::markAsRead);
    }
}
