package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.service.TagDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/tags")
@RequiredArgsConstructor
public class TagDefController {
    private final TagDefService tagDefService;

    @PostMapping
    public ResponseEntity<?> addTag(@RequestBody TagDefCreateRequest req, @RequestHeader("X-USER-ID")Long id) {
        return tagDefService.addTagName(req, id);
    }

    @DeleteMapping
    public ResponseEntity<?> removeTag(@RequestBody TagDefDeleteRequest req, @RequestHeader("X-USER-ID")Long id) {
        return tagDefService.removeTagName(req, id);
    }
}
