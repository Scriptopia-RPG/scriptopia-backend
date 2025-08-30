package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.auction.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
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
    public String createAuction(AuctionRequest requestDto, Long userId) {



        // UUID(String) → Long 변환 (임시)
        long userItemId = Long.parseLong(requestDto.getItemDefId());


        // UserItem 조회
        UserItem userItem = userItemRepository.findById(userItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        // 유저 소유 여부 확인
        if (!userItem.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.E_400_ITEM_NOT_OWNED);
        }

        // 거래 상태 확인
        if (userItem.getTradeStatus() != TradeStatus.OWNED) {
            throw new CustomException(ErrorCode.E_400_ITEM_NOT_TRADEABLE);
        }

        // 이미 경매장에 등록되어 있는지 확인
        if (auctionRepository.existsByUserItem(userItem)) {
            throw new CustomException(ErrorCode.E_400_ITEM_ALREADY_REGISTERED);
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
    public String purchaseItem(String auctionIdStr, Long userId) {
        Long auctionId = Long.parseLong(auctionIdStr);

        // 1. 거래소 정보 조회
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() ->  new CustomException(ErrorCode.E_404_AUCTION_NOT_FOUND));

        User buyer = userRepository.findById(userId)
                .orElseThrow(() ->  new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        User seller = auction.getUserItem().getUser();

        // 2. 자기 물건 구매 금지
        if (buyer.getId().equals(seller.getId())) {
            throw new CustomException(ErrorCode.E_400_SELF_PURCHASE);
        }

        // 3. 금액 확인
        if (buyer.getPia() < auction.getPrice()) {
            throw new CustomException(ErrorCode.E_400_INSUFFICIENT_PIA);
        }

        // 4. 금액 처리
        buyer.subtractPia(auction.getPrice());
        userRepository.save(buyer);

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


    @Transactional
    public String confirmItem(String settlementIdStr, Long userId) {
        Long settlementId = Long.parseLong(settlementIdStr);

        Settlement settlement = settlementRepository.findById(settlementId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SETTLEMENT_NOT_FOUND));

        // 정산 대상 유저 확인
        if (!settlement.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.E_403_ROLE_FORBIDDEN);
        }

        // 이미 정산 완료 여부 확인
        if (settlement.getSettledAt() != null) {
            throw new CustomException(ErrorCode.E_409_ALREADY_CONFIRMED);
        }

        if (settlement.getTradeType() == TradeType.BUY) {
            // 구매자 → 기존 UserItem 소유권 이전 및 상태 변경
            User buyer = settlement.getUser();

            UserItem userItem = userItemRepository
                    .findByItemDefAndTradeStatus(settlement.getItemDef(), TradeStatus.SOLD)
                    .orElseThrow(() -> new CustomException(ErrorCode.E_404_AUCTION_NOT_FOUND));

            userItem.setUser(settlement.getUser());    // 구매자로 소유권 이전
            userItem.setTradeStatus(TradeStatus.OWNED); // 거래 가능 상태로 변경
            userItemRepository.save(userItem);

        } else if (settlement.getTradeType() == TradeType.SELL) {
            // 판매자 → 금액 지급
            User seller = settlement.getUser();
            seller.addPia(settlement.getPrice()); // user domain 에서 관리
            userRepository.save(seller);
        }

        settlement.setSettledAt(LocalDateTime.now());
        settlementRepository.save(settlement);

        return "정산이 완료되었습니다.";
    }



    public SettlementHistoryResponse settlementHistory(Long userId, SettlementHistoryRequest requestDto) {
        int page = requestDto.getPageIndex().intValue();
        int size = requestDto.getPageSize().intValue();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 1. userId 으로 Settlement 조회
        Page<Settlement> settlements = settlementRepository.findByUserId(userId, pageable);


        // 2. Settlement → SettlementHistoryResponseItem 변환
        List<SettlementHistoryResponseItem> content = settlements.stream()
                .map(s -> new SettlementHistoryResponseItem(
                        s.getId(),
                        s.getItemDef().getName(),
                        s.getItemDef().getItemType().name(),
                        s.getItemDef().getItemGradeDef().getGrade().name(),
                        s.getPrice(),
                        s.getTradeType().name(),
                        s.getSettledAt()
                ))
                .toList();

        // 3. 페이지 정보 구성
        SettlementHistoryResponse.PageInfo pageInfo = new SettlementHistoryResponse.PageInfo();
        pageInfo.setCurrentPage(settlements.getNumber());
        pageInfo.setPageSize(settlements.getSize());

        // 4. Response DTO 생성
        return new SettlementHistoryResponse(content, pageInfo);

    }



    public MySaleItemResponse getMySaleItems(Long userId, MySaleItemRequest requestDto) {
        int page = requestDto.getPageIndex().intValue();
        int size = requestDto.getPageSize().intValue();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));


        // 현재 판매중(LISTED)인 아이템만 조회
        Page<Auction> auctions = auctionRepository.findByUserItem_User_IdAndUserItem_TradeStatus(
                userId,
                TradeStatus.LISTED,
                pageable
        );

        // 응답 content 변환
        List<MySaleItemResponseItem> content = auctions.stream()
                .map(auction -> new MySaleItemResponseItem(
                        auction.getId(),
                        auction.getPrice(),
                        auction.getCreatedAt(),
                        new MySaleItemResponseItem.ItemDto(
                                auction.getUserItem().getItemDef().getId(),
                                auction.getUserItem().getItemDef().getName(),
                                auction.getUserItem().getItemDef().getItemGradeDef().getGrade().name(),
                                auction.getUserItem().getItemDef().getItemType().name(),
                                auction.getUserItem().getItemDef().getMainStat().name(),
                                auction.getUserItem().getItemDef().getPicSrc()
                        )
                )).toList();



        // 페이지 정보
        MySaleItemResponse.PageInfo pageInfo = new MySaleItemResponse.PageInfo(page, size);
        return new MySaleItemResponse(content, pageInfo);
    }



    @Transactional
    public String cancelMySaleItem(Long userId, String auctionIdStr) {
        
        //임시 uuid를 사용한다면 추후 변형하는 메소드로 변경바람
        Long auctionId = Long.parseLong(auctionIdStr);

        
        // 1. 경매 정보 조회
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_AUCTION_NOT_FOUND));

        UserItem userItem = auction.getUserItem();

        // 2. 본인 검증
        if (!userItem.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.E_403_ROLE_FORBIDDEN);
        }


        // 3. UserItem 상태 원복
        userItem.setTradeStatus(TradeStatus.OWNED);
        userItemRepository.save(userItem);


        // 4. 경매장에서 삭제
        auctionRepository.delete(auction);

        return "판매 등록이 취소되었습니다.";
    }

}