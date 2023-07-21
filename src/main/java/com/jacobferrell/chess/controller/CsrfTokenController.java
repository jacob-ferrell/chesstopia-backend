package com.jacobferrell.chess.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CsrfTokenController {

    @RequestMapping("/csrf")
    public CsrfToken csrf(final CsrfToken token) {
        return token;
    }
}
