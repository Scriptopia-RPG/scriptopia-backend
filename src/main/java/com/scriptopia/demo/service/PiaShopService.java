package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import com.scriptopia.demo.dto.piashop.PiaItemUpdateRequest;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.PiaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiaShopService {

    private final PiaItemRepository piaItemRepository;

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

}
