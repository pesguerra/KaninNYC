package com.kaninnyc.service;

import com.kaninnyc.dto.AdminDtos.InventoryRequest;
import com.kaninnyc.dto.AdminDtos.InventoryResponse;
import com.kaninnyc.dto.AdminDtos.ManagedUserResponse;
import com.kaninnyc.dto.AdminDtos.UpdateUserRoleRequest;
import com.kaninnyc.model.AppUser;
import com.kaninnyc.model.InventoryItem;
import com.kaninnyc.repository.InventoryRepository;
import com.kaninnyc.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public AdminService(InventoryRepository inventoryRepository, UserRepository userRepository) {
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    public List<InventoryResponse> inventory() {
        return inventoryRepository.findAll().stream().map(this::toInventoryResponse).toList();
    }

    public InventoryResponse createInventoryItem(InventoryRequest request) {
        InventoryItem item = new InventoryItem();
        applyInventoryRequest(item, request);
        return toInventoryResponse(inventoryRepository.save(item));
    }

    public InventoryResponse updateInventoryItem(Integer id, InventoryRequest request) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found"));
        applyInventoryRequest(item, request);
        return toInventoryResponse(inventoryRepository.save(item));
    }

    public void deleteInventoryItem(Integer id) {
        inventoryRepository.deleteById(id);
    }

    public List<ManagedUserResponse> users() {
        return userRepository.findAll().stream().map(this::toManagedUserResponse).toList();
    }

    public ManagedUserResponse updateUserRole(Integer id, UpdateUserRoleRequest request) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(request.role());
        user.setApproved(request.approved());
        return toManagedUserResponse(userRepository.save(user));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    private void applyInventoryRequest(InventoryItem item, InventoryRequest request) {
        item.setName(request.name());
        item.setQuantity(request.quantity());
        item.setUnit(request.unit());
        item.setNotes(request.notes());
    }

    private InventoryResponse toInventoryResponse(InventoryItem item) {
        return new InventoryResponse(item.getId(), item.getName(), item.getQuantity(), item.getUnit(), item.getNotes());
    }

    private ManagedUserResponse toManagedUserResponse(AppUser user) {
        return new ManagedUserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isApproved(),
                user.getCreatedAt()
        );
    }
}
