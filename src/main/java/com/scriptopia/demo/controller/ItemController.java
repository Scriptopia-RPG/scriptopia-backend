package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.items.ItemDTO;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ItemDTO> createItem(
            Authentication authentication,
            @RequestBody ItemDefRequest request
    ) {
        String userId = authentication.getName();
        ItemDTO itemInWeb = itemService.createItemInWeb(userId, request);
        return ResponseEntity.ok(itemInWeb);
    }




}