package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.auction.AuctionRequest;
import com.scriptopia.demo.dto.auction.AuctionItemResponse;
import com.scriptopia.demo.dto.auction.TradeResponse;
import com.scriptopia.demo.dto.auction.TradeFilterRequest;
import com.scriptopia.demo.exception.auction.AuctionNotFoundException;
import com.scriptopia.demo.exception.auction.InsufficientPiaException;
import com.scriptopia.demo.exception.auction.SelfPurchaseException;
import com.scriptopia.demo.repository.AuctionRepository;
import com.scriptopia.demo.repository.SettlementRepository;
import com.scriptopia.demo.repository.UserItemRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;
    private final SettlementRepository settlementRepository;

    @Transactional
    public String createAuction(AuctionRequest requestDto, String userId) {

        // UUID(String) → Long 변환 (임시)
        long userItemId;
        try {
            userItemId = Long.parseLong(requestDto.getItemDefId());
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




    public TradeResponse getTrades(TradeFilterRequest request) {
        int page = request.getPageIndex().intValue();
        int size = request.getPageSize().intValue();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Auction> auctionPage;
        if (request.getItemName() != null && !request.getItemName().isEmpty()) {
            // itemName으로 검색, 연관 테이블까지 fetch
            auctionPage = auctionRepository.findByItemName(request.getItemName(), pageable);
        } else {
            // 이름이 없으면 다른 필터 조건으로 검색
            auctionPage = auctionRepository.findByFilters(
                    request.getCategory(),
                    request.getGrade(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getMainStat(),
                    request.getEffectGrades(),
                    pageable
            );
        }


        List<AuctionItemResponse> items = auctionPage.stream()
                .map(a -> {
                    AuctionItemResponse dto = new AuctionItemResponse();
                    dto.setAuctionId(a.getId());
                    dto.setPrice(a.getPrice());
                    dto.setCreatedAt(a.getCreatedAt());

                    // seller 정보
                    AuctionItemResponse.UserDto userDto = new AuctionItemResponse.UserDto();
                    userDto.setUserId(a.getUserItem().getUser().getId());
                    userDto.setNickname(a.getUserItem().getUser().getNickname());
                    dto.setSeller(userDto);

                    // item 정보
                    AuctionItemResponse.ItemDto itemDto = new AuctionItemResponse.ItemDto();
                    itemDto.setUserItemId(a.getUserItem().getId());
                    itemDto.setItemDefId(a.getUserItem().getItemDef().getId());
                    itemDto.setName(a.getUserItem().getItemDef().getName());
                    itemDto.setDescription(a.getUserItem().getItemDef().getDescription());
                    itemDto.setPicSrc(a.getUserItem().getItemDef().getPicSrc());
                    itemDto.setRemainingUses(a.getUserItem().getRemainingUses());
                    itemDto.setTradeStatus(a.getUserItem().getTradeStatus());
                    itemDto.setGrade(String.valueOf(a.getUserItem().getItemDef().getItemGradeDef().getGrade()));
                    itemDto.setBaseStat(a.getUserItem().getItemDef().getBaseStat());
                    itemDto.setStrength(a.getUserItem().getItemDef().getStrength());
                    itemDto.setAgility(a.getUserItem().getItemDef().getAgility());
                    itemDto.setIntelligence(a.getUserItem().getItemDef().getIntelligence());
                    itemDto.setLuck(a.getUserItem().getItemDef().getLuck());

                    // 효과 리스트 → 조건과 관계없이 "모든 효과"를 포함
                    // (JPQL에서 한 개만 남았더라도, Hibernate 엔티티를 통해 전체 컬렉션 다시 로딩)
                    a.getUserItem().getItemDef().getItemEffects().size(); // lazy 초기화 강제

                    List<AuctionItemResponse.ItemEffectDto> effects =
                            a.getUserItem().getItemDef().getItemEffects().stream()
                                    .map(e -> {
                                        AuctionItemResponse.ItemEffectDto effDto = new AuctionItemResponse.ItemEffectDto();
                                        effDto.setEffectName(e.getEffectName());
                                        effDto.setEffectDescription(e.getEffect_description());
                                        effDto.setGrade(e.getEffectGradeDef().getGrade().name());
                                        return effDto;
                                    })
                                    .toList();

                    itemDto.setEffects(effects);
                    dto.setItem(itemDto);

                    return dto;
                })
                .toList();


        TradeResponse response = new TradeResponse();
        response.setContent(items);

        TradeResponse.PageInfo pageInfo = new TradeResponse.PageInfo();
        pageInfo.setCurrentPage(auctionPage.getNumber());        // 현재 페이지
        pageInfo.setPageSize(auctionPage.getSize());            // 페이지 당 항목 수
        pageInfo.setTotalPages(auctionPage.getTotalPages());    // 전체 페이지 수
        pageInfo.setTotalItems(auctionPage.getTotalElements()); // 전체 아이템 수

        response.setPageInfo(pageInfo);

        return response;

    }



    @Transactional
    public String purchaseItem(String auctionIdStr, String userIdStr) {
        Long auctionId = Long.parseLong(auctionIdStr);
        Long userId = Long.parseLong(userIdStr);

        // 1. 거래소 정보 조회
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(AuctionNotFoundException::new);

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        User seller = auction.getUserItem().getUser();

        // 2. 자기 물건 구매 금지
        if (buyer.getId().equals(seller.getId())) {
            throw new SelfPurchaseException();
        }

        // 3. 금액 확인
        if (buyer.getPia() < auction.getPrice()) {
            throw new InsufficientPiaException();
        }

        // 4. 금액 처리
        buyer.setPia(buyer.getPia() - auction.getPrice());

        // 5. UserItem 상태 변경
        UserItem userItem = auction.getUserItem();
        userItem.setTradeStatus(TradeStatus.SOLD);

        // 6. Settlement 기록 추가
        Settlement buyerSettlement = new Settlement();
        buyerSettlement.setUser(buyer);
        buyerSettlement.setItemDef(userItem.getItemDef());
        buyerSettlement.setPrice(auction.getPrice());
        buyerSettlement.setTradeType(TradeType.BUY);
        buyerSettlement.setCreatedAt(LocalDateTime.now());
        buyerSettlement.setSettledAt(null);
        settlementRepository.save(buyerSettlement);

        Settlement sellerSettlement = new Settlement();
        sellerSettlement.setUser(seller);
        sellerSettlement.setItemDef(userItem.getItemDef());
        sellerSettlement.setPrice(auction.getPrice());
        sellerSettlement.setTradeType(TradeType.SELL);
        sellerSettlement.setCreatedAt(LocalDateTime.now());
        sellerSettlement.setSettledAt(null);
        settlementRepository.save(sellerSettlement);

        // 7. 경매 테이블에서 삭제
        auctionRepository.delete(auction);

        return "구매 완료";
    }


}
