package com.scriptopia.demo.controller;


import com.scriptopia.demo.dto.auction.*;
import com.scriptopia.demo.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "거래 API", description = "경매장 관련 거래 API 입니다.")
@RequestMapping("/trades")
public class AuctionController {

    private final AuctionService auctionService;

    @Operation(summary = "보유 장비 아이템 조회")
    @GetMapping
    public ResponseEntity<TradeResponse> getTrades(
            @RequestBody TradeFilterRequest requestDto) {

        TradeResponse response = auctionService.getTrades(requestDto);
        return ResponseEntity.ok(response);

    }

    @Operation(summary = "경매장 아이템 구매")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{auctionId}/purchase")
    public ResponseEntity<String> purchaseItem(
            @PathVariable String auctionId,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.purchaseItem(auctionId, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내가 등록한 판매 아이템 조회")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<MySaleItemResponse> mySaleItems(
            @RequestBody MySaleItemRequest requestDto,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        MySaleItemResponse result = auctionService.getMySaleItems(userId, requestDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "경매장 아이템 판매 등록")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<String> createAuction(@RequestBody AuctionRequest dto,
                                                Authentication authentication ){

        Long userId = Long.valueOf(authentication.getName());
        return ResponseEntity.ok(auctionService.createAuction(dto, userId));
    }

    @Operation(summary = "판매 중인 아이템 등록 취소")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<String> cancelMySaleItem(
            @PathVariable String auctionId,
            Authentication authentication) {

        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.cancelMySaleItem(userId, auctionId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 거래 기록 조회(정산 테이블 조회)")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @GetMapping("/me/history")
    public ResponseEntity<SettlementHistoryResponse> settlementHistory(
            @RequestBody SettlementHistoryRequest requestDto,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        SettlementHistoryResponse result = auctionService.settlementHistory(userId, requestDto);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "구매 아이템/판매 대금 수령")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @PostMapping("/{settlementId}/confirm")
    public ResponseEntity<String> confirmItem(
            @PathVariable String settlementId,
            Authentication authentication) {


        Long userId = Long.valueOf(authentication.getName());
        String result = auctionService.confirmItem(settlementId, userId);
        return ResponseEntity.ok(result);
    }

}