package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import com.scriptopia.demo.dto.piashop.PiaItemResponse;
import com.scriptopia.demo.dto.piashop.PiaItemUpdateRequest;
import com.scriptopia.demo.dto.piashop.PurchasePiaItemRequest;
import com.scriptopia.demo.service.PiaShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class PiaShopController {
    private final PiaShopService piaShopService;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/items/pia")
    public ResponseEntity<String> createPiaItem(@RequestBody PiaItemRequest request) {
        piaShopService.createPiaItem(request);
        return ResponseEntity.ok("PIA 아이템이 등록되었습니다.");
    }


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping("/items/pia/{itemId}")
    public ResponseEntity<String> updatePiaItem(
            @PathVariable("itemId") String itemId,
            @RequestBody PiaItemUpdateRequest requestDto) {


        String result = piaShopService.updatePiaItem(itemId, requestDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/pia/items")
    public ResponseEntity<List<PiaItemResponse>> getPiaItems() {
        return ResponseEntity.ok(piaShopService.getPiaItems());
    }

    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/pia/item/purchase")
    public ResponseEntity<String> purchasePiaItem(
            @RequestBody PurchasePiaItemRequest requestDto,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        piaShopService.purchasePiaItem(userId, requestDto);
        return ResponseEntity.ok("PIA 아이템을 구매했습니다.");
    }


}
