package com.jacobferrell.chess.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.persistence.*;
import java.util.Set;
import java.util.Collection;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class UserDTO implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;

    private String lastName;

    @Column(nullable = false, length = 50, unique = true)
    private String email;
      
    @JsonIgnore
    @Column(nullable = false, length = 64)
    private String password;

    @JsonIgnore
    @Enumerated(EnumType.STRING)
    private Role role;

    @JsonIgnore
    @Builder.Default
    private boolean inLobby = false;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<UserDTO> friends;

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
 
    @Override
    public String getUsername() {
        return email;
    }
 
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public boolean equals(UserDTO otherUser) {
        return otherUser.getEmail() == this.getEmail();
    }

}
