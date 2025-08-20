package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.auction.AuctionRequestDto;
import com.scriptopia.demo.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class AuctionController {

    private final AuctionService auctionService;

    // 판매 아이템 등록
    @PostMapping
    public ResponseEntity<?> createAuction(@RequestBody com.scriptopia.demo.dto.auction.AuctionRequestDto requestDto) {
        return ResponseEntity.ok(auctionService.createAuction(requestDto));

    }
}
