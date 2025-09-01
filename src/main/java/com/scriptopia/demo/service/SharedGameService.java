package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.History;
import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.sharedgame.CursorPage;
import com.scriptopia.demo.dto.sharedgame.MySharedGameResponse;
import com.scriptopia.demo.dto.sharedgame.PublicSharedGameResponse;
import com.scriptopia.demo.dto.sharedgame.TagDto;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SharedGameService {
    private final SharedGameRepository sharedGameRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final SharedGameScoreRepository sharedGameScoreRepository;
    private final SharedGameFavoriteRepository sharedGameFavoriteRepository;
    private final GameTagRepository gameTagRepository;

    @Transactional
    public ResponseEntity<?> saveSharedGame(Long Id, Long historyId) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        History history = historyRepository.findById(historyId)
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

        List<SharedGame> games = sharedGameRepository.findAllByUserId(user.getId());

        List<MySharedGameResponse> responses = games.stream().map(game -> {
            MySharedGameResponse dto = new MySharedGameResponse();
            dto.setThumbnailUrl(game.getThumbnailUrl());
            dto.setTotalPlayed(sharedGameScoreRepository.countBySharedGameId(game.getId()));
            dto.setTitle(game.getTitle());
            dto.setWorldView(game.getWorldView());
            dto.setBackgroundStory(game.getBackgroundStory());
            dto.setSharedAt(game.getSharedAt());

            boolean liked = sharedGameFavoriteRepository.existsByUserIdAndSharedGameId(user.getId(), game.getId());
            dto.setRecommand(liked);

            List<String> names = gameTagRepository.findTagNamesBySharedGameId(game.getId());
            dto.setTags(
                    names.stream()
                            .map(MySharedGameResponse.TagDto::new)
                            .toList()
            );
            return dto;
                }).toList();

        return ResponseEntity.ok(responses);
    }

    @Transactional
    public void deletesharedGame(Long id, Long sharedId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));

        SharedGame game = sharedGameRepository.findById(sharedId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SHARED_GAME_NOT_FOUND));

        if(!game.getUser().getId().equals(user.getId())) {        // 공유된 게임과 로그인한 사용자가 아닌 경우
            throw new CustomException(ErrorCode.E_401_NOT_EQUAL_SHARED_GAME);
        }

        sharedGameRepository.delete(game);
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
