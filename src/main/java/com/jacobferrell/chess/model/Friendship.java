package com.jacobferrell.chess.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "friendships")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
        name = "friendship", 
        joinColumns = @JoinColumn(name = "friendship_id"), 
        inverseJoinColumns = @JoinColumn(name = "user_id") 
    )
    Set<UserDTO> users;



}
