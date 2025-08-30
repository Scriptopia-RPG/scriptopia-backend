package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/items")
public class PiaShopController {
    private final P

    @PostMapping("/pia")
    public ResponseEntity<String> createPiaItem(@RequestBody PiaItemRequest request) {



        return ResponseEntity.ok("PIA 아이템이 등록되었습니다.");
    }
}
