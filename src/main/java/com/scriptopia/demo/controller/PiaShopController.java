package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import com.scriptopia.demo.dto.piashop.PiaItemResponse;
import com.scriptopia.demo.dto.piashop.PiaItemUpdateRequest;
import com.scriptopia.demo.dto.piashop.PurchasePiaItemRequest;
import com.scriptopia.demo.service.PiaShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PiaShopController {
    private final PiaShopService piaShopService;

    // admin → public 으로 테스트용 변경했음 나중에 수정 바람
    @PostMapping("/public/shops/items/pia")
    public ResponseEntity<String> createPiaItem(@RequestBody PiaItemRequest request) {
        piaShopService.createPiaItem(request);
        return ResponseEntity.ok("PIA 아이템이 등록되었습니다.");
    }


    // admin → public 으로 테스트용 변경했음 나중에 수정 바람
    @PutMapping("/public/shops/items/pia/{itemId}")
    public ResponseEntity<String> updatePiaItem(
            @PathVariable String itemId,
            @RequestBody PiaItemUpdateRequest requestDto) {


        String result = piaShopService.updatePiaItem(itemId, requestDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/shops/pia/items")
    public ResponseEntity<List<PiaItemResponse>> getPiaItems() {
        return ResponseEntity.ok(piaShopService.getPiaItems());
    }


    @PostMapping("/user/shops/pia/item/purchase")
    public ResponseEntity<String> purchasePiaItem(
            @RequestBody PurchasePiaItemRequest requestDto,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        piaShopService.purchasePiaItem(userId, requestDto);
        return ResponseEntity.ok("PIA 아이템을 구매했습니다.");
    }


}
