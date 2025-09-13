package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody ItemDefRequest request) {
        String savedItem = itemService.createMongoItem(request);
        return ResponseEntity.ok(savedItem);
    }




}