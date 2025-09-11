package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.dto.sharedgame.*;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        dto.setTotalPlayed(game.getTotalPlayed());
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
    public ResponseEntity<CursorPage<PublicSharedGameResponse>> getPublicSharedGames(Long userId, Long lastId, int size,
                                                                                     List<Long> tagIds, String q) {

        PageRequest pr = PageRequest.of(0, size);
        Page<SharedGame> page;

        boolean hasQ = q != null && q.isBlank();
        boolean hasTags = tagIds != null && !tagIds.isEmpty();

        if(hasQ) {
            page = sharedGameRepository.pageSearchOnly(lastId, q.trim(), pr);
        }
        else if(hasTags) {
            page = sharedGameRepository.pageByAllTagsOnly(lastId, tagIds, tagIds.size(), pr);
        }
        else {
            page = sharedGameRepository.pageAll(lastId, pr);
        }

        var items = page.getContent().stream().map(g -> {
            var dto = new PublicSharedGameResponse();
            dto.setSharedGameId(g.getId());
            dto.setThumbnailUrl(g.getThumbnailUrl());
            dto.setTitle(g.getTitle());
            dto.setTopScore(sharedGameScoreRepository.maxScoreBySharedGameId(g.getId()));
            dto.setSharedAt(g.getSharedAt());

            dto.setTotalPlayCount(sharedGameScoreRepository.countBySharedGameId(g.getId()));
            dto.setLikeCount(sharedGameFavoriteRepository.countBySharedGameId(g.getId()));

            if(userId != null) {
                dto.setLiked(sharedGameFavoriteRepository.existsByUserIdAndSharedGameId(userId, g.getId()));
            }

            List<TagDto> tags = gameTagRepository.findTagDtosBySharedGameId(g.getId());
            dto.setTags(tags);

            return dto;
        }).toList();

        Long nextCursor = items.isEmpty() ? null : items.get(items.size() - 1).getSharedGameId();
        return ResponseEntity.ok(new CursorPage<>(items, nextCursor, page.hasNext()));
    }
}
