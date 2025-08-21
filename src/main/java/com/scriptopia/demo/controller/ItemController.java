package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.utils.service.ItemDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemDefService itemDefService;

    @PostMapping
    public ResponseEntity<ItemDef> createItem(@RequestBody ItemDefRequest dto) {
        ItemDef savedItem = itemDefService.createItem(dto);
        return ResponseEntity.ok(savedItem);
    }


}