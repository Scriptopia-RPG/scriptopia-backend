package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.service.SharedGameFavoriteService;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shared-games")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;
    private final SharedGameFavoriteService sharedGameFavoriteService;

    @GetMapping("/{sharedGameId}")
    public ResponseEntity<?> getSharedGameDetail(@PathVariable("sharedGameId") Long sharedGameId) {
        return sharedGameService.getDetailedSharedGame(sharedGameId);
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getSharedGameTags() {
        return sharedGameService.getTag();
    }

    @GetMapping
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(
            Authentication authentication,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(required = false) String q) {
        Long viewerId = (authentication == null) ? null : Long.valueOf(authentication.getName());
        return sharedGameService.getPublicSharedGames(viewerId, lastId, size, tagIds, q);
    }

    @PostMapping("/{sharedGameId}/like")
    public ResponseEntity<?> likeSharedGame(@PathVariable("sharedGameId") Long sharedGameId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        return sharedGameFavoriteService.saveFavorite(userId, sharedGameId);
    }


}
