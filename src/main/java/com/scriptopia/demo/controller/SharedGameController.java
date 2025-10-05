package com.scriptopia.demo.controller;

import com.scriptopia.demo.domain.SharedGameSort;
import com.scriptopia.demo.dto.TagDef.TagDefCreateRequest;
import com.scriptopia.demo.dto.TagDef.TagDefDeleteRequest;
import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.service.SharedGameFavoriteService;
import com.scriptopia.demo.service.SharedGameService;
import com.scriptopia.demo.service.TagDefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/shared-games")
@Tag(name = "게임 공유 관련 API", description = "게임 공유 관련 API 입니다.")
@RequiredArgsConstructor
public class SharedGameController {
    private final SharedGameService sharedGameService;
    private final SharedGameFavoriteService sharedGameFavoriteService;
    private final TagDefService tagDefService;

    /*
    게임 공유 -> 게임 공유하기
     */

    @Operation(summary = "게임 공유하기")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("/{sharedGameUuid}")
    public ResponseEntity<?> share(Authentication authentication, @PathVariable("sharedGameUuid") UUID sharedGameUuid) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameService.saveSharedGame(userId, sharedGameUuid);
    }

    /*
    게임 공유 -> 공유 게임 목록 조회
     */
    @Operation(summary = "공유 게임 목록 조회")
    @GetMapping
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(@RequestParam(value = "lastUUID", required = false) UUID lastUUID,
                                                                                     @RequestParam(value = "size", defaultValue = "20") int size,
                                                                                     @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
                                                                                     @RequestParam(value = "query", required = false) String query,
                                                                                     @RequestParam(value = "sort", defaultValue = "POPULAR") SharedGameSort sort) {
        return sharedGameService.getPublicSharedGames(lastUUID, size, tagIds, query, sort);
    }

    /*
    게임공유 : 공유된 게임 상세 조회
     */
    @Operation(summary = "공유 게임 상세 조회")
    @GetMapping("/{sharedGameUuid}")
    public ResponseEntity<?> getSharedGameDetail(Authentication authentication, @PathVariable("sharedGameUuid") UUID sharedGameUuid) {
        Long userId = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getName() != null) {
            try {
                userId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException ignored) {
            }
        }
        System.out.println(userId);

        return sharedGameService.getDetailedSharedGame(userId, sharedGameUuid);
    }

    /*
    게임공유 : 공유 게임 Like 요청
     */
    @Operation(summary = "공유 게임 Like 요청")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @PostMapping("{sharedGameUuId}/like")
    public ResponseEntity<?> likeSharedGame(@PathVariable("sharedGameUuId") UUID sharedGameId, Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());

        return sharedGameFavoriteService.saveFavorite(userId, sharedGameId);
    }

    /*
    게임공유 : 공유된 게임 태그 조회
     */
    @Operation(summary = "게임 태그 조회")
    @GetMapping("/tags")
    public ResponseEntity<?> getSharedGameTags() {
        return sharedGameService.getTag();
    }

    /*
    게임 공유 -> 공유한 게임 삭제
     */
    @Operation(summary = "공유한 게임 삭제")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @DeleteMapping("/{sharedGameUuid}")
    public ResponseEntity<?> delete(Authentication authentication, @PathVariable("sharedGameUuid") UUID sharedGameUuid) {
        Long userId = Long.valueOf(authentication.getName());

        sharedGameService.deleteSharedGame(userId, sharedGameUuid);

        return ResponseEntity.ok("게임이 삭제되었습니다.");
    }

    @Operation(summary = "게임 태그 생성")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping("/tags")
    public ResponseEntity<?> addTag(@RequestBody TagDefCreateRequest req) {

        return tagDefService.addTagName(req);
    }

    @Operation(summary = "게임 태그 삭제 ")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @DeleteMapping("/tags")
    public ResponseEntity<?> removeTag(@RequestBody TagDefDeleteRequest req) {
        return tagDefService.removeTagName(req);
    }
}
