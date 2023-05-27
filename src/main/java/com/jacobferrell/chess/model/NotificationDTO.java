package com.jacobferrell.chess.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "notifications")
public class NotificationDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinTable(name = "notification_game", joinColumns = @JoinColumn(name = "notification_id"), inverseJoinColumns = @JoinColumn(name = "game_id"))
    private GameDTO game;

   /*  @ManyToOne
    @JoinTable(name = "notification_from", joinColumns = @JoinColumn(name = "notification_id"), inverseJoinColumns = @JoinColumn(name = "from_id"))
    private UserDTO from; */

    @ManyToOne
    @JoinTable(name = "notification_to", joinColumns = @JoinColumn(name = "notification_id"), inverseJoinColumns = @JoinColumn(name = "to_id"))
    private UserDTO to;

    @Builder.Default
    private boolean read = false;
    
}
