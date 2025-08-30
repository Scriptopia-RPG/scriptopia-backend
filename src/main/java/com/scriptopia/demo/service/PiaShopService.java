package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.dto.piashop.PiaItemRequest;
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
            throw new CustomException(ErrorCode.E_400_MISSING_NICKNAME); // 이름 필수 체크
        }
        if(request.getPrice() == null || request.getPrice() <= 0) {
            throw new CustomException(ErrorCode.E_400_INVALID_AMOUNT);
        }

        // 2. 중복 이름 체크
        if(piaItemRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.E_409_ALREADY_CONFIRMED); // 적절한 오류 코드 선택
        }

        // 3. PiaItem 생성
        PiaItem piaItem = new PiaItem();
        piaItem.setName(request.getName());
        piaItem.setPrice(String.valueOf(request.getPrice()));
        piaItem.setDescription(request.getDescription());

        piaItemRepository.save(piaItem);

        return "PIA 아이템이 성공적으로 생성되었습니다.";
    }
}
