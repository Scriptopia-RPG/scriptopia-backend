package com.scriptopia.demo.controller;


import com.scriptopia.demo.dto.auction.AuctionRequest;
import com.scriptopia.demo.dto.auction.TradeResponse;
import com.scriptopia.demo.dto.auction.TradeFilterRequest;
import com.scriptopia.demo.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;


    @PostMapping("/user/trades")
    public ResponseEntity<String> createAuction(@RequestBody AuctionRequest dto,
                                                @RequestHeader("token") String userId ){   // 헤더에서 userId 가져오기 임시임

        return ResponseEntity.ok(auctionService.createAuction(dto, userId));
    }


    @GetMapping("/public/trades")
    public ResponseEntity<TradeResponse> getTrades(
            @RequestBody TradeFilterRequest requestDto) {

        TradeResponse response = auctionService.getTrades(requestDto);
        return ResponseEntity.ok(response);

    }


    @PostMapping("/user/{auctionId}/purchase")
    public ResponseEntity<String> purchaseItem(
            @PathVariable String auctionId,
            @RequestHeader("token") String userId) {

        String result = auctionService.purchaseItem(auctionId, userId);
        return ResponseEntity.ok(result);

    }

}