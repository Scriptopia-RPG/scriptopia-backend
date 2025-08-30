package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import com.scriptopia.demo.service.PiaShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PiaShopController {
    private final PiaShopService piaShopService;

    // admin → public 으로 테스트용 변경했음 나중에 수정 바람
    @PostMapping("/public/items/pia")
    public ResponseEntity<String> createPiaItem(@RequestBody PiaItemRequest request) {
        piaShopService.createPiaItem(request);
        return ResponseEntity.ok("PIA 아이템이 등록되었습니다.");
    }


}
