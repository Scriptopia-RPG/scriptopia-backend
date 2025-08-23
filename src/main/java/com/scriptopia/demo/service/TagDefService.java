package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.TagDef;
import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.repository.TagDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagDefService {
    private final TagDefRepository tagDefRepository;

    @Transactional
    public ResponseEntity<?> addTagName(TagDefCreateRequest req, Long id) {
        // TODO - 들어온 토큰으로 관리자 인증 해야함
        String tagName = req.getTagName();

        if(!tagDefRepository.existsByTagName(tagName)) {        // 입력된 태그 이미 존재하는지 확인
            TagDef tagDef = new TagDef();
            tagDef.setTagName(tagName);
            tagDefRepository.save(tagDef);

            return ResponseEntity.ok(tagDef);
        }

        return ResponseEntity.ok("이미 존재하는 태그입니다.");
    }

    @Transactional
    public ResponseEntity<?> removeTagName(TagDefDeleteRequest req, Long id) {
        // TODO - 들어온 토큰으로 관리자 인증 해야함

        // TODO - 이미 사용중인 태그는 삭제못하도록 막아야함

        Long ids = req.getId();

        tagDefRepository.deleteById(ids);
        return ResponseEntity.ok("선택하신 태그가 삭제되었습니다.");
    }
}
