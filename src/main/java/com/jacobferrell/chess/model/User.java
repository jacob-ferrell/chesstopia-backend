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

import com.jacobferrell.chess.model.Role;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    @Column(nullable = false, length = 50, unique = true)
    private String email;
      
    @Column(nullable = false, length = 64)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany
    @JoinTable(
        name = "friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }
 
    @Override
    public String getUsername() {
        return email;
    }
 
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
 
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
 
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
 
    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean equals(User otherUser) {
        return otherUser.getEmail() == this.getEmail();
    }

}
