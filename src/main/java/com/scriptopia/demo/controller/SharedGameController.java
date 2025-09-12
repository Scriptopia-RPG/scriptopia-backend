package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/games/shared")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;

    /*
    게임공유 : 공유된 게임 상세 조회
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<?> getSharedGameDetail(@PathVariable UUID uuid) {
        return sharedGameService.getDetailedSharedGame(uuid);
    }

    /*
    게임공유 : 공유된 게임 태그 조회
     */
    @GetMapping("/tags")
    public ResponseEntity<?> getSharedGameTags() {
        return sharedGameService.getTag();
    }

    /*
    게임공유 : 공유된 게임 목록 조회
     */
    @GetMapping
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(Authentication authentication,
                                                                                     @RequestParam(required = false) Long lastId,
                                                                                     @RequestParam(defaultValue = "20") int size,
                                                                                     @RequestParam(required = false) List<Long> tagIds,
                                                                                     @RequestParam(required = false) String query) {
        Long viewerId = (authentication == null) ? null : Long.valueOf(authentication.getName());
        return sharedGameService.getPublicSharedGames(viewerId, lastId, size, tagIds, query);
    }
}
