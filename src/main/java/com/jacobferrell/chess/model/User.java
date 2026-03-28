package com.jacobferrell.chess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Data
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String firstName;

    private String lastName;

    @Column(nullable = false, length = 50, unique = true)
    @NonNull
    private String email;
      
    @JsonIgnore
    @Column(nullable = false, length = 64)
    @NonNull
    private String password;

    @JsonIgnore
    @Enumerated
    private Role role;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany
    @JoinTable(
        name = "friends",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private Set<User> friends;

    @OneToMany(mappedBy = "whitePlayer", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<GameEntity> gamesAsWhite = new ArrayList<>();

    @OneToMany(mappedBy = "blackPlayer", orphanRemoval = true, cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    @Builder.Default
    private List<GameEntity> gamesAsBlack = new ArrayList<>();

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

    public boolean equals(User otherUser) {
        return otherUser.getEmail().equals(this.getEmail());
    }

}
