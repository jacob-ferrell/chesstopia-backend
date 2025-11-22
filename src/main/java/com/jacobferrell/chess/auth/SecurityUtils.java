package com.jacobferrell.chess.auth;

import com.jacobferrell.chess.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {} // prevent instantiation

    /**
     * Returns the currently authenticated User entity, or null if unauthenticated.
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof User user)) {
            throw new IllegalStateException("Current user could not be resolved");
        }

        return user;

    }

    /**
     * Returns the username/email of the currently authenticated user, or null if unauthenticated.
     */
    public static String getCurrentUsername() {
        User user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
}

