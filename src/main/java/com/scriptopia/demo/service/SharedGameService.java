package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.sharedgame.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

        SharedGame sharedGame = SharedGame.from(user, history);
        return ResponseEntity.ok(sharedGameRepository.save(sharedGame));
    }

    public ResponseEntity<?> getMySharedGames(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        List<SharedGame> games = sharedGameRepository.findAllByUserid(user.getId());

        List<MySharedGameResponse> dtos = new ArrayList<>();

        for(SharedGame game : games) {
            MySharedGameResponse dto = new MySharedGameResponse();
            dto.setShared_game_uuid(game.getUuid());
            dto.setThumbnailUrl(game.getThumbnailUrl());
            dto.setTotalPlayed(sharedGameScoreRepository.countBySharedGameId(game.getId()));
            dto.setTitle(game.getTitle());
            dto.setWorldView(game.getWorldView());
            dto.setSharedAt(game.getSharedAt());
            dto.setBackgroundStory(game.getBackgroundStory());

            boolean liked = sharedGameFavoriteRepository.existsLikeSharedGame(user.getId(), game.getId());
            dto.setRecommand(liked);

            List<String> tagdto = gameTagRepository.findTagNamesBySharedGameId(game.getId());
            List<MySharedGameResponse.TagDto> tags = new ArrayList<>();

            for(String tagName : tagdto) {
                tags.add(new MySharedGameResponse.TagDto(tagName));
            }

            dto.setTags(tags);
            dtos.add(dto);
        }

        return ResponseEntity.ok(dtos);
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

    public ResponseEntity<?> getDetailedSharedGame(UUID uuid) {
        SharedGame game = sharedGameRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SHARED_GAME_NOT_FOUND));

        List<String> tagName = gameTagRepository.findTagNamesBySharedGameId(game.getId());

        List<SharedGameScore> score = sharedGameScoreRepository.findAllBySharedGameIdOrderByScoreDescCreatedAtDesc(game.getId());

        PublicSharedGameDetailResponse dto = new PublicSharedGameDetailResponse();
        dto.setSharedGameUUID(game.getUuid());
        dto.setNickname(game.getUser().getNickname());
        dto.setThumbnailUrl(game.getThumbnailUrl());
        dto.setTotalPlayed(sharedGameScoreRepository.countBySharedGameId(game.getId()));
        dto.setTitle(game.getTitle());
        dto.setWorldView(game.getWorldView());
        dto.setBackgroundStory(game.getBackgroundStory());
        dto.setSharedAt(game.getSharedAt());

        List<PublicSharedGameDetailResponse.TagDto> tagarray = new ArrayList<>();
        List<PublicSharedGameDetailResponse.TopScoreDto> topscorearray = new ArrayList<>();

        for(var tagNames : tagName) {
            tagarray.add(new PublicSharedGameDetailResponse.TagDto(tagNames));
        }

        dto.setTags(tagarray);

        for(var topScoreInfo : score) {
            PublicSharedGameDetailResponse.TopScoreDto topscore = new PublicSharedGameDetailResponse.TopScoreDto();
            topscore.setNickname(topScoreInfo.getUser().getNickname());
            topscore.setScore(topScoreInfo.getScore());
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
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(Long userId,
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
        SharedGameSort effectiveSort = qBlank ? sort : SharedGameSort.LATEST;

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
            dto.setSharedGameId(g.getUuid());
            dto.setThumbnailUrl(g.getThumbnailUrl());
            dto.setTitle(g.getTitle());
            dto.setSharedAt(g.getSharedAt());

            // 집계
            dto.setTotalPlayCount(sharedGameScoreRepository.countBySharedGameId(g.getId()));
            dto.setLikeCount(sharedGameFavoriteRepository.countBySharedGameId(g.getId()));

            Long topScore = sharedGameScoreRepository.maxScoreBySharedGameId(g.getId());
            dto.setTopScore(topScore == null ? 0L : topScore);

            // 좋아요 여부
            if (userId != null) {
                boolean liked = sharedGameFavoriteRepository.existsByUserIdAndSharedGameId(userId, g.getId());
                dto.setLiked(liked);
            }

            // 태그
            dto.setTags(gameTagRepository.findTagDtosBySharedGameId(g.getId()));
            return dto;
        }).toList();

        // 6) 커서/hasNext
        UUID nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).getSharedGameId();
        boolean hasNext = rows.size() == Math.max(1, size);

        return ResponseEntity.ok(new CursorPage<>(items, nextCursor, hasNext));
    }
}
