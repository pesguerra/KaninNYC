package com.kaninnyc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaninnyc.dto.AuthDtos.UserWithoutPasswordDto;
import com.kaninnyc.model.AppUser;
import com.kaninnyc.model.UserRole;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationHelper {
    private final String secret;

    public AuthorizationHelper(@Value("${app.auth.secret}") String secret) {
        this.secret = secret;
    }

    public AuthorizationResult getUserFromHeaders(Map<String, String> headers) {
        AuthorizationResult result = new AuthorizationResult();
        String diyAuth = headers.get("authorization");

        if (diyAuth == null || diyAuth.isBlank()) {
            result.setResponseEntity(new ResponseEntity<>(List.of("You must be authorized to do that"), HttpStatus.UNAUTHORIZED));
            return result;
        }

        String[] halves = diyAuth.split("\\|");
        if (halves.length != 2) {
            result.setResponseEntity(new ResponseEntity<>(List.of("Invalid authorization header"), HttpStatus.UNAUTHORIZED));
            return result;
        }

        try {
            String userJson = halves[0];
            int expectedHash = Objects.hash(userJson + secret);
            int providedHash = Integer.parseInt(halves[1]);
            if (expectedHash != providedHash) {
                result.setResponseEntity(new ResponseEntity<>(List.of("Detected tampering with authorization header"), HttpStatus.UNAUTHORIZED));
                return result;
            }

            UserWithoutPasswordDto userDto = new ObjectMapper().readValue(userJson, UserWithoutPasswordDto.class);
            AppUser user = new AppUser();
            user.setId(userDto.getId());
            user.setEmail(userDto.getEmail());
            user.setRole(userDto.getRole());
            user.setApproved(userDto.isApproved());
            result.setUser(user);
        } catch (JsonProcessingException | NumberFormatException ex) {
            result.setResponseEntity(new ResponseEntity<>(List.of("Invalid user"), HttpStatus.UNAUTHORIZED));
        }

        return result;
    }

    public ResponseEntity<Object> requireRole(Map<String, String> headers, UserRole role) {
        AuthorizationResult result = getUserFromHeaders(headers);
        if (!result.isSuccess()) {
            return result.getResponseEntity();
        }
        if (result.getUser().getRole() != role) {
            return new ResponseEntity<>(List.of("You do not have permission to do that"), HttpStatus.FORBIDDEN);
        }
        return null;
    }
}
