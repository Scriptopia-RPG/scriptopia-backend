package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.ItemDef;
import com.scriptopia.demo.dto.auction.AuctionRequest;
import com.scriptopia.demo.dto.devlop.ItemDefResponse;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.service.AuctionService;
import com.scriptopia.demo.service.ItemDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemDefService itemDefService;

    @PostMapping
    public ResponseEntity<ItemDefResponse> createItem(@RequestBody ItemDefRequest dto) {
        ItemDefResponse savedItem = itemDefService.createItem(dto);
        return ResponseEntity.ok(savedItem);
    }




}