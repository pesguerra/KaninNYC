package com.kaninnyc.dto;

import com.kaninnyc.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank @Size(min = 8) String password
    ) {
    }

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String user,
            UserSummary userSummary,
            List<String> features
    ) {
    }

    public static class UserWithoutPasswordDto {
        private Integer id;
        private String email;
        private UserRole role;
        private boolean approved;

        public static UserWithoutPasswordDto fromUser(com.kaninnyc.model.AppUser user) {
            UserWithoutPasswordDto dto = new UserWithoutPasswordDto();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setRole(user.getRole());
            dto.setApproved(user.isApproved());
            return dto;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public UserRole getRole() {
            return role;
        }

        public void setRole(UserRole role) {
            this.role = role;
        }

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }
    }

    public record MeResponse(
            UserSummary user,
            List<String> features
    ) {
    }

    public record UserSummary(
            Integer id,
            String email,
            UserRole role,
            boolean approved
    ) {
    }
}
