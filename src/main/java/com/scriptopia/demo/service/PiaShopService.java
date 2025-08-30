package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.domain.PiaItemPurchaseLog;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.domain.UserPiaItem;
import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import com.scriptopia.demo.dto.piashop.PiaItemResponse;
import com.scriptopia.demo.dto.piashop.PiaItemUpdateRequest;
import com.scriptopia.demo.dto.piashop.PurchasePiaItemRequest;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.PiaItemRepository;
import com.scriptopia.demo.repository.UserPiaItemRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiaShopService {

    private final PiaItemRepository piaItemRepository;
    private final UserRepository userRepository;
    private final UserPiaItemRepository userPiaItemRepository;

    @Transactional
    public String createPiaItem(PiaItemRequest request) {

        // 1. 필수 값 확인
        if(request.getName() == null || request.getName().isBlank()) {
            throw new CustomException(ErrorCode.E_400_INVALID_REQUEST); // 이름 필수 체크
        }

        if(request.getPrice() == null || request.getPrice() <= 0) {
            throw new CustomException(ErrorCode.E_400_INVALID_REQUEST); // 금액 유효성 체크
        }

        // 2. 중복 이름 체크
        if(piaItemRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.E_400_PIA_ITEM_DUPLICATE); // 중복 이름 오류
        }

        // 3. PiaItem 생성
        PiaItem piaItem = new PiaItem();
        piaItem.setName(request.getName());
        piaItem.setPrice(request.getPrice());
        piaItem.setDescription(request.getDescription());

        piaItemRepository.save(piaItem);

        return "PIA 아이템이 성공적으로 생성되었습니다.";
    }


    @Transactional
    public String updatePiaItem(String itemsIdStr, PiaItemUpdateRequest request) {

        // uuid 사용 시 변경 (임시임)
        Long itemsId = Long.valueOf(itemsIdStr);


        // 1. 필수 값 체크
        if (request.getName() == null || request.getName().isBlank()) {
            throw new CustomException(ErrorCode.E_400_MISSING_NICKNAME); // 이름 필수
        }
        if (request.getPrice() == null || request.getPrice() <= 0) {
            throw new CustomException(ErrorCode.E_400_INVALID_AMOUNT); // 가격 필수
        }

        // 2. 아이템 존재 확인
        PiaItem piaItem = piaItemRepository.findById(itemsId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_AUCTION_NOT_FOUND)); // 존재하지 않는 아이템

        // 3. 이름 중복 체크 (자기 자신 제외)
        if (piaItemRepository.existsByNameAndIdNot(request.getName(),itemsId)){
            throw new CustomException(ErrorCode.E_409_ALREADY_CONFIRMED); // 이름 중복
        }

        // 4. 정보 업데이트
        piaItem.setName(request.getName());
        piaItem.setPrice(request.getPrice());
        piaItem.setDescription(request.getDescription());

        piaItemRepository.save(piaItem);

        return "PIA 아이템이 성공적으로 수정되었습니다.";
    }



    public List<PiaItemResponse> getPiaItems() {
        return piaItemRepository.findAll().stream()
                .map(PiaItemResponse::fromEntity)
                .collect(Collectors.toList());
    }


    public void purchasePiaItem(Long userId, PurchasePiaItemRequest request) {


        // 1. 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        // 2. 아이템 조회
        Long piaItemId = Long.parseLong(request.getItemId());
        PiaItem piaItem = piaItemRepository.findById(piaItemId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_AUCTION_NOT_FOUND));

        // 3. 총 구매 금액 계산
        long totalPrice = piaItem.getPrice() * request.getQuantity();
        if (user.getPia() < totalPrice) {
            throw new CustomException(ErrorCode.E_400_INSUFFICIENT_PIA);
        }

        // 4. UserPiaItem 조회 및 수량 업데이트
        UserPiaItem userPiaItem = userPiaItemRepository.findByUserAndPiaItem(user, piaItem)
                .orElseGet(() -> {
                    UserPiaItem newItem = new UserPiaItem();
                    newItem.setUser(user);
                    newItem.setPiaItem(piaItem);
                    newItem.setQuantity(0L);
                    return newItem;
                });

        userPiaItem.setQuantity(userPiaItem.getQuantity() + request.getQuantity());
        userPiaItemRepository.save(userPiaItem);

        // 5. 유저 금액 차감
        user.setPia(user.getPia() - totalPrice);
        userRepository.save(user);

        // 6. 구매 로그 기록
        PiaItemPurchaseLog log = new PiaItemPurchaseLog();
        log.setUser(user);
        log.setPiaItem(piaItem);
        log.setPrice(totalPrice);
        purchaseLogRepository.save(log);
    }








}
