package com.jacobferrell.chess.model;

import jakarta.persistence.*;

import java.util.Date;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private GameEntity game;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User to;

    @Builder.Default
    private boolean read = false;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Date createdAt = new Date();

}
