package com.kaninnyc.service;

import com.kaninnyc.dto.MenuItemResponse;
import com.kaninnyc.repository.MenuItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MenuService {
    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItemResponse> menuItems() {
        return menuItemRepository.findAll().stream()
                .map(item -> new MenuItemResponse(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getDescription()
                ))
                .toList();
    }
}
