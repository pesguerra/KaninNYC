package com.kaninnyc.dto;

import com.kaninnyc.model.UserRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public final class AdminDtos {
    private AdminDtos() {
    }

    public record InventoryRequest(
            @NotBlank String name,
            @NotNull @Min(0) Integer quantity,
            @NotBlank String unit,
            String notes
    ) {
    }

    public record InventoryResponse(
            Integer id,
            String name,
            Integer quantity,
            String unit,
            String notes
    ) {
    }

    public record UpdateUserRoleRequest(
            @NotNull UserRole role,
            boolean approved
    ) {
    }

    public record ManagedUserResponse(
            Integer id,
            String email,
            UserRole role,
            boolean approved,
            LocalDateTime createdAt
    ) {
    }
}
