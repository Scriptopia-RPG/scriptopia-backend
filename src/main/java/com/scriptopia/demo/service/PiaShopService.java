package com.scriptopia.demo.service;


import com.scriptopia.demo.dto.piashop.PiaItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PiaShopService {


    @Transactional
    public String createPiaItem(PiaItemRequest request){



    }

}
