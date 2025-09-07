package com.scriptopia.demo.controller;


import com.scriptopia.demo.dto.auction.*;
import com.scriptopia.demo.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trades")
public class AuctionController {

    private final AuctionService auctionService;


    @GetMapping("/trades")
    public ResponseEntity<TradeResponse> getTrades(
            @RequestBody TradeFilterRequest requestDto) {

        TradeResponse response = auctionService.getTrades(requestDto);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/trades/{auctionId}/purchase")
    public ResponseEntity<String> purchaseItem(
            @PathVariable String auctionId,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.purchaseItem(auctionId, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/trades/me")
    public ResponseEntity<MySaleItemResponse> mySaleItems(
            @RequestBody MySaleItemRequest requestDto,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        MySaleItemResponse result = auctionService.getMySaleItems(userId, requestDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/trades")
    public ResponseEntity<String> createAuction(@RequestBody AuctionRequest dto,
                                                Authentication authentication ){

        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(auctionService.createAuction(dto, userId));
    }

    @DeleteMapping("/trades/{auctionId}")
    public ResponseEntity<String> cancelMySaleItem(
            @PathVariable String auctionId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.cancelMySaleItem(userId, auctionId);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/trades/me/history")
    public ResponseEntity<SettlementHistoryResponse> settlementHistory(
            @RequestBody SettlementHistoryRequest requestDto,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        SettlementHistoryResponse result = auctionService.settlementHistory(userId, requestDto);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/trades/{settlementId}/confirm")
    public ResponseEntity<String> confirmItem(
            @PathVariable String settlementId,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.confirmItem(settlementId, userId);
        return ResponseEntity.ok(result);
    }

}