package com.scriptopia.demo.controller;

import com.scriptopia.demo.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<String> createAuction(
            @RequestBody com.scriptopia.demo.dto.auction.AuctionRequest requestDto,
            @RequestHeader("token") String userId) {   // 헤더에서 userId 가져오기 임시임

        return ResponseEntity.ok(auctionService.createAuction(requestDto, userId));
    }
}
