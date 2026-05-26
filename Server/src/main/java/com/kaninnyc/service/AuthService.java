package com.kaninnyc.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaninnyc.dto.AuthDtos.AuthResponse;
import com.kaninnyc.dto.AuthDtos.LoginRequest;
import com.kaninnyc.dto.AuthDtos.MeResponse;
import com.kaninnyc.dto.AuthDtos.RegisterRequest;
import com.kaninnyc.dto.AuthDtos.UserSummary;
import com.kaninnyc.dto.AuthDtos.UserWithoutPasswordDto;
import com.kaninnyc.model.AppUser;
import com.kaninnyc.model.UserRole;
import com.kaninnyc.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final String authSecret;

    public AuthService(
            UserRepository userRepository,
            @Value("${app.auth.secret}") String authSecret
    ) {
        this.userRepository = userRepository;
        this.authSecret = authSecret;
    }

    public UserSummary register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        AppUser user = new AppUser();
        user.setEmail(request.email().toLowerCase());
        user.setPassword(request.password());
        user.setRole(UserRole.PENDING);
        user.setApproved(false);
        AppUser saved = userRepository.save(user);
        return toSummary(saved);
    }

    public AuthResponse login(LoginRequest request) throws JsonProcessingException {
        AppUser user = userRepository.findByEmail(request.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("Invalid login"));
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("Incorrect password");
        }
        if (!user.isApproved() || user.getRole() == UserRole.PENDING) {
            throw new IllegalStateException("Your account is waiting for admin approval");
        }
        UserWithoutPasswordDto userDto = UserWithoutPasswordDto.fromUser(user);
        String userJson = new ObjectMapper().writeValueAsString(userDto);
        String diyAuth = userJson + "|" + Objects.hash(userJson + authSecret);
        return new AuthResponse(diyAuth, toSummary(user), featuresFor(user.getRole()));
    }

    public MeResponse me(AppUser user) {
        return new MeResponse(toSummary(user), featuresFor(user.getRole()));
    }

    public UserSummary toSummary(AppUser user) {
        return new UserSummary(user.getId(), user.getEmail(), user.getRole(), user.isApproved());
    }

    public List<String> featuresFor(UserRole role) {
        return switch (role) {
            case CASHIER -> List.of("POS");
            case CHEF -> List.of("KITCHEN_ORDERS");
            case ADMIN -> List.of("INVENTORY", "USER_ROLES");
            case PENDING -> List.of();
        };
    }
}
