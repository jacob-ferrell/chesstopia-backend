package com.jacobferrell.chess.controller.matchmaking;

import com.jacobferrell.chess.auth.SecurityUtils;
import com.jacobferrell.chess.service.matchmaking.MatchmakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MatchmakingController {

    private final MatchmakingService matchmakingService;

    @MessageMapping("/matchmaking/join")
    public void join() {
        matchmakingService.tryMatch(SecurityUtils.getCurrentUser());
    }

    @MessageMapping("/matchmaking/leave")
    public void leave() {
        matchmakingService.removeFromQueue(SecurityUtils.getCurrentUser().getId());
    }

}
