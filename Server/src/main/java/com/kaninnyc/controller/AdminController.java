package com.kaninnyc.controller;

import com.kaninnyc.dto.AdminDtos.InventoryRequest;
import com.kaninnyc.dto.AdminDtos.InventoryResponse;
import com.kaninnyc.dto.AdminDtos.ManagedUserResponse;
import com.kaninnyc.dto.AdminDtos.UpdateUserRoleRequest;
import com.kaninnyc.model.UserRole;
import com.kaninnyc.service.AdminService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthorizationHelper authorizationHelper;

    public AdminController(AdminService adminService, AuthorizationHelper authorizationHelper) {
        this.adminService = adminService;
        this.authorizationHelper = authorizationHelper;
    }

    @GetMapping("/inventory")
    public ResponseEntity<Object> inventory(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(adminService.inventory());
    }

    @PostMapping("/inventory")
    public ResponseEntity<Object> createInventoryItem(
            @Valid @RequestBody InventoryRequest request,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        return new ResponseEntity<>(adminService.createInventoryItem(request), HttpStatus.CREATED);
    }

    @PutMapping("/inventory/{id}")
    public ResponseEntity<Object> updateInventoryItem(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryRequest request,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(adminService.updateInventoryItem(id, request));
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Object> deleteInventoryItem(
            @PathVariable Integer id,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        adminService.deleteInventoryItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> users(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(adminService.users());
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<Object> updateUserRole(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(adminService.updateUserRole(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(
            @PathVariable Integer id,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }
        adminService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
