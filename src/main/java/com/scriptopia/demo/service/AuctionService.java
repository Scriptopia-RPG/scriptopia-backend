package com.scriptopia.demo.utils.service;

import com.scriptopia.demo.domain.Auction;
import com.scriptopia.demo.domain.TradeStatus;
import com.scriptopia.demo.domain.UserItem;
import com.scriptopia.demo.dto.auction.AuctionRequest;
import com.scriptopia.demo.repository.AuctionRepository;
import com.scriptopia.demo.repository.UserItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserItemRepository userItemRepository;

    @Transactional
    public String createAuction(AuctionRequest requestDto, String userId) {

        // UUID(String) → Long 변환 (임시)
        long userItemId;
        try {
            userItemId = Long.parseLong(requestDto.getItemDefsId());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid UserItem UUID");
        }

        // UserItem 조회
        UserItem userItem = userItemRepository.findById(userItemId)
                .orElseThrow(() -> new IllegalArgumentException("UserItem not found"));

        // 유저 소유 여부 확인
        if (!userItem.getUser().getId().equals(Long.parseLong(userId))) {
            throw new IllegalStateException("해당 아이템은 사용자가 소유하지 않았습니다.");
        }

        // 거래 상태 확인
        if (userItem.getTradeStatus() != TradeStatus.OWNED) {
            throw new IllegalStateException(
                    "해당 아이템은 현재 경매장에 올릴 수 없습니다. 상태: " + userItem.getTradeStatus());
        }

        // 이미 경매장에 등록되어 있는지 확인
        if (auctionRepository.existsByUserItem(userItem)) {
            throw new IllegalStateException("이미 경매장에 등록된 아이템입니다.");
        }

        // Auction 등록
        Auction auction = new Auction();
        auction.setUserItem(userItem);
        auction.setPrice(requestDto.getPrice());
        auction.setCreatedAt(LocalDateTime.now());
        auctionRepository.save(auction);

        // UserItem 상태 업데이트
        userItem.setTradeStatus(TradeStatus.LISTED);
        userItemRepository.save(userItem);

        return "등록 완료: 가격=" + requestDto.getPrice();
    }
}
