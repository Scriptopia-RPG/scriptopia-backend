package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.SharedGameFavorite;
import com.scriptopia.demo.dto.sharedgamefavorite.SharedGameFavoriteResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class SharedGameFavoriteService {
    private final SharedGameFavoriteRepository sharedGameFavoriteRepository;
    private final SharedGameRepository sharedGameRepository;
    private final GameTagRepository gameTagRepository;
    private final UserRepository userRepository;
    private final SharedGameScoreRepository sharedGameScoreRepository;

    @Transactional
    public ResponseEntity<?> saveFavorite(Long userId, UUID uuid) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));
        var game = sharedGameRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SHARED_GAME_NOT_FOUND));

        // 토글 처리
        var existing = sharedGameFavoriteRepository.findByUserIdAndSharedGameId(userId, game.getId());
        boolean liked;
        if (existing.isPresent()) {
            sharedGameFavoriteRepository.delete(existing.get());
            liked = false;
        } else {
            var fav = new SharedGameFavorite();
            fav.setUser(user);
            fav.setSharedGame(game);
            sharedGameFavoriteRepository.save(fav);
            liked = true;
        }

        long likeCount = sharedGameFavoriteRepository.countBySharedGameId(game.getId());
        long playCount = sharedGameScoreRepository.countBySharedGameId(game.getId());
        Long maxScore  = sharedGameScoreRepository.maxScoreBySharedGameId(game.getId());

        // 태그 이름들
        var tags = gameTagRepository.findTagDtosBySharedGameId(game.getId());

        var dto = SharedGameFavoriteResponse.builder()
                .sharedGameUuid(game.getUuid().toString())
                .thumbnailUrl(game.getThumbnailUrl())
                .isLiked(liked)
                .likeCount(likeCount)
                .totalPlayCount(playCount)
                .title(game.getTitle())
                .tags(tags)
                .topScore(maxScore)
                .build();

        return ResponseEntity.ok(dto);
    }
}
