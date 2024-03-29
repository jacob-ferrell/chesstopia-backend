package com.jacobferrell.chess.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    @JsonProperty
    private boolean isDemoUser;
}
