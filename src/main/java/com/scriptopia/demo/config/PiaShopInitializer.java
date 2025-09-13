package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.PiaItem;
import com.scriptopia.demo.repository.PiaItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PiaShopInitializer implements ApplicationRunner {

    private final PiaItemRepository piaItemRepository;

    @Override
    public void run(ApplicationArguments args) {

        if (!(piaItemRepository.existsByName("아이템 모루"))) {
            PiaItem piaItem = initializePiaItem(
                    "아이템 모루",
                    300L,
                    "이세계 포털에서 랜덤한 아이템을 한 개 꺼내온다. 무엇이 들어있을 지는 아무도 모른다.."
            );

            piaItemRepository.save(piaItem);
        }
    }





    private PiaItem initializePiaItem(String name, Long price, String desc) {
        PiaItem piaItem = new PiaItem();
        piaItem.setName(name);
        piaItem.setPrice(price);
        piaItem.setDescription(desc);
        return piaItem;
    }
}
