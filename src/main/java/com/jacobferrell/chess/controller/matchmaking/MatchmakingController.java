package com.jacobferrell.chess.controller.matchmaking;

import com.jacobferrell.chess.model.User;
import com.jacobferrell.chess.service.matchmaking.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @MessageMapping("/matchmaking/join")
    public void join(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        matchmakingService.tryMatch(user);
    }

    @MessageMapping("/matchmaking/leave")
    public void leave(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        matchmakingService.removeFromQueue(user.getId());
    }

}
