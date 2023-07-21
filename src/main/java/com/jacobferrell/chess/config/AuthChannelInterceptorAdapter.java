package com.jacobferrell.chess.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import com.jacobferrell.chess.service.JwtService;

@Component
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {

        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            printStompHeaders(accessor);
            String token = null;
            final String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7, bearerToken.length());
            }
            System.out.println("!!!!!!!!!!\n\n\n\n\n\n\n\n\n\n" + token);
            final String userEmail = jwtService.extractUsername(token);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (token != null && jwtService.isTokenValid(token, userDetails)) {
                final Authentication auth = getAuthentication(userDetails);
                accessor.setUser(auth);
            }
        }

        return message;
    }

    public Authentication getAuthentication(final UserDetails userDetails) {
        PreAuthenticatedAuthenticationToken authenticationToken = null;

        final List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority("USER"));

        final org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                userDetails.getUsername(), "", grantedAuthorities);
        authenticationToken = new PreAuthenticatedAuthenticationToken(user, "", user.getAuthorities());
        return authenticationToken;
    }

    public void printStompHeaders(StompHeaderAccessor accessor) {
        Map<String, List<String>> headers = accessor.toNativeHeaderMap();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            System.out.println("\n" + headerName + ": " + headerValues + "\n");
        }
    }
}
