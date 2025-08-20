package com.scriptopia.demo.service;

import com.scriptopia.demo.dto.auction.AuctionRequestDto;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {

    public String createAuction(AuctionRequestDto requestDto) {
        // requestDto에서 itemDefsId( uuid -> long)
        // 위의 long으로 UserItem을 가져오고 그 안에 있는 유저 아이디가 해당 유저와 같은지 체크

        // 모두 완료 후 auction에 추가



        return "등록 완료: " + requestDto.getItemDefsId() + " / 가격: " + requestDto.getPrice();
    }
}
