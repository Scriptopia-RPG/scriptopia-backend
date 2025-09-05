package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.develop.ItemDefResponse;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.service.ItemDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemDefService itemDefService;


    @PostMapping
    public ResponseEntity<ItemDefResponse> createItem(@RequestBody ItemDefRequest request) {
        ItemDefResponse savedItem = itemDefService.createItem(request);
        return ResponseEntity.ok(savedItem);
    }




}