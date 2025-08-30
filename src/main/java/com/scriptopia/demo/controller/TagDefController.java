package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.service.TagDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController("/admin/tag")
@RequiredArgsConstructor
public class TagDefController {
    private final TagDefService tagDefService;

    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody TagDefCreateRequest req, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return tagDefService.addTagName(req, userId);
    }

    @DeleteMapping
    public ResponseEntity<?> removeTag(@RequestBody TagDefDeleteRequest req, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return tagDefService.removeTagName(req, userId);
    }
}
