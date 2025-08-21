package com.scriptopia.demo;

import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.utils.service.TagDefService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TagDefController {
    private final TagDefService tagDefService;

    @PostMapping("/add-tag")
    public ResponseEntity<?> addTag(@RequestBody TagDefCreateRequest req, @RequestHeader("X-USER-ID")Long id) {
        return tagDefService.addTagName(req, id);
    }

    @DeleteMapping("/remove-tag")
    public ResponseEntity<?> removeTag(@RequestBody TagDefDeleteRequest req, @RequestHeader("X-USER-ID")Long id) {
        return tagDefService.removeTagName(req, id);
    }
}
