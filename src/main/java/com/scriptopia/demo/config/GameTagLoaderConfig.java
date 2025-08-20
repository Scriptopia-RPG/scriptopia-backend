package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.TagDef;
import com.scriptopia.demo.repository.TagDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class GameTagLoaderConfig implements CommandLineRunner {
    private final TagDefRepository tagDefRepository;

    @Override
    public void run(String... args) {
        List<String> tags = List.of("남성 인기", "시뮬레이션", "로맨스", "SF", "좀비", "생존", "격투", "모험", "탐험",
                "전투", "판타지", "현대", "범죄", "아포칼립스", "드래곤", "던전");

        for (String tagName : tags) {
            if (!tagDefRepository.existsByTagName(tagName)) {
                TagDef tagDef = new TagDef();
                tagDef.setTagName(tagName);
                tagDefRepository.save(tagDef);
            }
        }
    }
}
