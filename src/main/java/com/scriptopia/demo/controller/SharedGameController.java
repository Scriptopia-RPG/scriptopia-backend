package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;

    @PostMapping("/share/{hid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable Long hid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, hid);
    }

    @GetMapping("/games/shared")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }

    @DeleteMapping("/share/{gameid}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable Long gameid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deletesharedGame(userId, gameid);

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }

    @GetMapping("/public/shared")
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(Authentication authentication,
                                                                                     @RequestParam(required = false) Long lastId,
                                                                                     @RequestParam(defaultValue = "20") int size,
                                                                                     @RequestParam(required = false) List<Long> tagIds,
                                                                                     @RequestParam(required = false) String q) {
        Long viewerId = (authentication == null) ? null : Long.valueOf(authentication.getName());
        return sharedGameService.getPublicSharedGames(viewerId, lastId, size, tagIds, q);
    }
}
