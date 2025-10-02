package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.items.ItemDTO;
import com.scriptopia.demo.dto.items.ItemDefRequest;
import com.scriptopia.demo.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@Tag(name = "아이템 관련 API", description = "아이템 관련 API 입니다.")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "어드민 테스트용 아이템 생성")
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