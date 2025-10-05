package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.sharedgame.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharedGameService {
    private final SharedGameRepository sharedGameRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final SharedGameScoreRepository sharedGameScoreRepository;
    private final SharedGameFavoriteRepository sharedGameFavoriteRepository;
    private final GameTagRepository gameTagRepository;
    private final TagDefRepository tagDefRepository;

    @Transactional
    public ResponseEntity<?> saveSharedGame(Long Id, UUID uuid) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        History history = historyRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        if(!history.getUser().getId().equals(Id)) {
            throw new CustomException(ErrorCode.E_401_NOT_EQUAL_SHARED_GAME);
        }

        history.setIsShared(true);

        SharedGame sharedGame = SharedGame.from(user, history);
        sharedGameRepository.save(sharedGame);

        SharedGameSaveDto dto = new SharedGameSaveDto();
        dto.setSharedGameUuid(sharedGame.getUuid().toString());
        dto.setThumbnailUrl(sharedGame.getThumbnailUrl());
        dto.setRecommand(sharedGameFavoriteRepository.countBySharedGameId(sharedGame.getId()));
        dto.setPlayCount(sharedGameScoreRepository.countBySharedGameId(sharedGame.getId()));
        dto.setTitle(sharedGame.getTitle());
        dto.setWorldView(sharedGame.getWorldView());
        dto.setBackgroundStory(sharedGame.getBackgroundStory());
        dto.setSharedAt(sharedGame.getSharedAt());

        return ResponseEntity.ok(dto);
    }

    @Transactional
    public void deleteSharedGame(Long id, UUID uuid) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        SharedGame game = sharedGameRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_GAME_SESSION_NOT_FOUND));

        if(!game.getUser().getId().equals(user.getId())) {        // 공유된 게임과 로그인한 사용자가 아닌 경우
            throw new CustomException(ErrorCode.E_401_NOT_EQUAL_SHARED_GAME);
        }

        sharedGameRepository.delete(game);
    }

    public ResponseEntity<?> getDetailedSharedGame(Long userId, UUID uuid) {
        SharedGame game = sharedGameRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SHARED_GAME_NOT_FOUND));

        List<com.scriptopia.demo.dto.sharedgame.TagDto> tagDtos = gameTagRepository.findTagDtosBySharedGameId(game.getId());

        List<SharedGameScore> score = sharedGameScoreRepository.findAllBySharedGameIdOrderByScoreDescCreatedAtDesc(game.getId());
        boolean isLiked = (userId != null) && sharedGameFavoriteRepository.existsByUserIdAndSharedGameId(userId, game.getId());

        PublicSharedGameDetailResponse dto = new PublicSharedGameDetailResponse();
        dto.setSharedGameUuID(game.getUuid());
        dto.setPosterUrl(game.getThumbnailUrl());
        dto.setTitle(game.getTitle());
        dto.setWorldView(game.getWorldView());
        dto.setBackgroundStory(game.getBackgroundStory());
        dto.setCreator(game.getUser().getNickname());
        dto.setPlayCount(sharedGameScoreRepository.countBySharedGameId(game.getId()));
        dto.setLikeCount(sharedGameFavoriteRepository.countBySharedGameId(game.getId()));
        dto.setSharedAt(game.getSharedAt());
        dto.setLiked(isLiked);

        List<PublicSharedGameDetailResponse.TagDto> tagarray = new ArrayList<>();
        List<PublicSharedGameDetailResponse.TopScoreDto> topscorearray = new ArrayList<>();

        for(var tagDto : tagDtos) {
            tagarray.add(new PublicSharedGameDetailResponse.TagDto(tagDto.getTagId(), tagDto.getTagName()));
        }

        dto.setTags(tagarray);

        for(var topScoreInfo : score) {
            PublicSharedGameDetailResponse.TopScoreDto topscore = new PublicSharedGameDetailResponse.TopScoreDto();
            topscore.setNickname(topScoreInfo.getUser().getNickname());
            topscore.setScore(topScoreInfo.getScore());
            topscore.setProfileUrl(topScoreInfo.getUser().getProfileImgUrl());
            topscore.setCreatedAt(topScoreInfo.getCreatedAt());
            topscorearray.add(topscore);
        }

        dto.setTopScores(topscorearray);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<?> getTag() {
        List<TagDef> tag = tagDefRepository.findAll();

        List<PublicTagDefResponse> dtoList = tag.stream()
                .map(t -> new PublicTagDefResponse(t.getId(), t.getTagName()))
                .toList();

        return ResponseEntity.ok(dtoList);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(
                                                                               UUID lastUuid,
                                                                               int size,
                                                                               List<Long> tagIds,
                                                                               String q,
                                                                               SharedGameSort sort) {
        String raw = (q == null) ? "" : q;
        String trimmed = raw.strip();
        boolean qBlank = trimmed.isEmpty();
        String qLike = "%" + trimmed.toLowerCase() + "%";

        // 2) 태그/커서/정렬 전처리
        boolean tagEmpty = (tagIds == null || tagIds.isEmpty());
        SharedGameSort effectiveSort = qBlank ? sort : SharedGameSort.POPULAR;

        boolean useCursor = (lastUuid != null);
        Long lastId = null;
        LocalDateTime lastSharedAt = null;
        Long lastPlayCount = null;
        Long lastTopScore = null;

        if (useCursor) {
            SharedGame pivot = sharedGameRepository.findByUuid(lastUuid)
                    .orElseThrow(() -> new CustomException(ErrorCode.E_404_PAGE_NOT_FOUND));
            lastId = pivot.getId();

            switch (effectiveSort) {
                case LATEST -> lastSharedAt = pivot.getSharedAt();
                case POPULAR -> lastPlayCount = sharedGameScoreRepository.countBySharedGameId(lastId);
                case TOP_SCORE -> {
                    Long max = sharedGameScoreRepository.maxScoreBySharedGameId(lastId);
                    lastTopScore = (max == null) ? 0L : max;
                }
            }
        }

        // 3) 페이지 사이즈/페이징
        Pageable pageable = PageRequest.of(0, Math.max(1, size));

        // 4) 정렬 스위치별 슬라이스 조회 (qLike/qBlank 사용)
        List<SharedGame> rows = switch (effectiveSort) {
            case LATEST -> sharedGameRepository.sliceLatest(
                    tagEmpty ? List.of(-1L) : tagIds, tagEmpty,
                    qLike, qBlank,
                    useCursor, lastSharedAt, lastId,
                    pageable
            );
            case POPULAR -> sharedGameRepository.slicePopular(
                    tagEmpty ? List.of(-1L) : tagIds, tagEmpty,
                    qLike, qBlank,
                    useCursor, lastPlayCount, lastId,
                    pageable
            );
            case TOP_SCORE -> sharedGameRepository.sliceTopScore(
                    tagEmpty ? List.of(-1L) : tagIds, tagEmpty,
                    qLike, qBlank,
                    useCursor, lastTopScore, lastId,
                    pageable
            );
        };

        // 5) DTO 매핑 (집계 일원화)
        List<PublicSharedGameResponse> items = rows.stream().map(g -> {
            PublicSharedGameResponse dto = new PublicSharedGameResponse();
            dto.setSharedGameUuid(g.getUuid());
            dto.setThumbnailUrl(g.getThumbnailUrl());
            dto.setTitle(g.getTitle());

            // 집계
            dto.setPlayCount(sharedGameScoreRepository.countBySharedGameId(g.getId()));

            // 태그
            dto.setTags(gameTagRepository.findTagDtosBySharedGameId(g.getId()));
            return dto;
        }).toList();

        // 6) 커서/hasNext
        UUID nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).getSharedGameUuid();
        boolean hasNext = rows.size() == Math.max(1, size);

        return ResponseEntity.ok(new CursorPage<>(items, nextCursor, hasNext));
    }
}
