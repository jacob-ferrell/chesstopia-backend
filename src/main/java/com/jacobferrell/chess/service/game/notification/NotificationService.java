package com.jacobferrell.chess.service.game.notification;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.model.GameEntity;
import com.jacobferrell.chess.model.Notification;
import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.repository.NotificationRepository;
import com.jacobferrell.chess.service.JsonService;
import com.jacobferrell.chess.service.game.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.webjars.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final SimpMessagingTemplate messagingTemplate;

    private final SimpUserRegistry userRegistry;

    private final JsonService jsonService;

    @Transactional
    public void sendMessageAndNotification(User user, GameEntity gameEntityData) {
        long gameId = gameEntityData.getId();
        User recipient = UserService.getOtherPlayer(user, gameEntityData);
        boolean recipientIsWatching = isUserSubscribedToGame(recipient, gameId);

        if (!recipientIsWatching) {
            String message = user.getFirstName() + " made a move in game " + gameId;
            Notification notification = createNotification(user, recipient, message);
            notification.setGame(gameEntityData);
            notificationRepository.save(notification);
        }

        Map<String, Object> messageBody = Map.of("game", gameEntityData);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                messagingTemplate.convertAndSend("/topic/game/" + gameId, jsonService.toJSON(messageBody));
            }
        });
    }

    public boolean isUserSubscribedToGame(User user, long gameId) {
        var simpUser = userRegistry.getUser(user.getUsername());
        if (simpUser == null) return false;
        String destination = "/topic/game/" + gameId;
        return simpUser.getSessions().stream()
                .flatMap(s -> s.getSubscriptions().stream())
                .anyMatch(sub -> sub.getDestination().equals(destination));
    }

    public void showPlayerIsConnectedToGame(long gameId, User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("connected", true);
        map.put("player", user.getId());
        messagingTemplate.convertAndSend("/topic/game/" + gameId, jsonService.toJSON(map));
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
