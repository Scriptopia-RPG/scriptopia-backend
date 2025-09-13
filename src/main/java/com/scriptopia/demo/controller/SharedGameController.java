package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.SharedGameFavorite;
import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.dto.sharedgame.SharedGameRequest;
import com.scriptopia.demo.service.SharedGameFavoriteService;
import com.scriptopia.demo.service.SharedGameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shared-games")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;
    private final SharedGameFavoriteService sharedGameFavoriteService;

    /*
    게임 공유 -> 게임 공유하기
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<?> share(Authentication authentication, @RequestBody SharedGameRequest req) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, req.getUuid());
    }

    /*
    게임 공유 -> 공유 게임 목록 조회
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

    /*
    게임공유 : 공유된 게임 상세 조회
     */
    @GetMapping("/{sharedGameId}")
    public ResponseEntity<?> getSharedGameDetail(@PathVariable("sharedGameId") UUID sharedGameId) {
        return sharedGameService.getDetailedSharedGame(sharedGameId);
    }

    /*
    게임공유 : 공유 게임 Like 요청
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("{sharedGameId}/like")
    public ResponseEntity<?> likeSharedGame(@PathVariable("sharedGameId") UUID sharedGameId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameFavoriteService.saveFavorite(userId, sharedGameId);
    }

    /*
    게임공유 : 공유된 게임 태그 조회
     */
    @GetMapping("/tags")
    public ResponseEntity<?> getSharedGameTags() {
        return sharedGameService.getTag();
    }

    /*
    게임 공유 -> 공유한 게임 삭제
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/shared-games")
    public ResponseEntity<?> delete(Authentication authentication, @RequestBody SharedGameRequest req) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deleteSharedGame(userId, req.getUuid());

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }

    /*
    게임 공유 -> 공유한 게임 조회(내가 공유한 게임 조회)
     */
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/me")
    public ResponseEntity<?> getMySharedGames(Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.getMySharedGames(userId);
    }
}
