package com.kaninnyc.controller;

import com.kaninnyc.dto.AuthDtos.AuthResponse;
import com.kaninnyc.dto.AuthDtos.LoginRequest;
import com.kaninnyc.dto.AuthDtos.MeResponse;
import com.kaninnyc.dto.AuthDtos.RegisterRequest;
import com.kaninnyc.dto.AuthDtos.UserSummary;
import com.kaninnyc.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthorizationHelper authorizationHelper;

    public AuthController(AuthService authService, AuthorizationHelper authorizationHelper) {
        this.authService = authService;
        this.authorizationHelper = authorizationHelper;
    }

    @PostMapping("/register")
    public UserSummary register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) throws JsonProcessingException {
        return authService.login(request);
    }

    @GetMapping("/me")
    public ResponseEntity<Object> me(@RequestHeader Map<String, String> headers) {
        AuthorizationResult authorizationResult = authorizationHelper.getUserFromHeaders(headers);
        if (!authorizationResult.isSuccess()) {
            return authorizationResult.getResponseEntity();
        }
        MeResponse response = authService.me(authorizationResult.getUser());
        return ResponseEntity.ok(response);
    }
}
