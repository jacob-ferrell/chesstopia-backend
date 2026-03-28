package com.jacobferrell.chess.config;

import com.jacobferrell.chess.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
            String token = null;
            final String bearerToken = accessor.getFirstNativeHeader("Authorization");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7, bearerToken.length());
            }
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

        authenticationToken = new PreAuthenticatedAuthenticationToken(userDetails, "", userDetails.getAuthorities());

        return authenticationToken;
    }

}
