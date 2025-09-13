package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.TagDef;
import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.TagDefRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagDefService {
    private final TagDefRepository tagDefRepository;

    @Transactional
    public ResponseEntity<?> addTagName(TagDefCreateRequest req) {
        String tagName = req.getTagName();

        if(!tagDefRepository.existsByTagName(tagName)) {        // 입력된 태그 이미 존재하는지 확인
            TagDef tagDef = new TagDef();
            tagDef.setTagName(tagName);
            tagDefRepository.save(tagDef);

            return ResponseEntity.ok(tagDef);
        }

        throw new CustomException(ErrorCode.E_400_TAG_DUPLICATED);
    }

    @Transactional
    public ResponseEntity<?> removeTagName(TagDefDeleteRequest req) {
        TagDef tag = tagDefRepository.findByTagName(req.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_Tag_NOT_FOUND));

        tagDefRepository.delete(tag);
        return ResponseEntity.ok("선택하신 태그가 삭제되었습니다.");
    }
}
