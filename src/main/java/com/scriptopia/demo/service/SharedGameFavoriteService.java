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


@Service
@RequiredArgsConstructor
public class SharedGameFavoriteService {
    private final SharedGameFavoriteRepository sharedGameFavoriteRepository;
    private final SharedGameRepository sharedGameRepository;
    private final GameTagRepository gameTagRepository;
    private final UserRepository userRepository;
    private final SharedGameScoreRepository sharedGameScoreRepository;

    @Transactional
    public ResponseEntity<?> saveFavorite(Long userId, Long sharedGameId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_USER_NOT_FOUND));
        var game = sharedGameRepository.findById(sharedGameId)
                .orElseThrow(() -> new CustomException(ErrorCode.E_404_SHARED_GAME_NOT_FOUND));

        // 토글 처리
        var existing = sharedGameFavoriteRepository.findByUserIdAndSharedGameId(userId, sharedGameId);
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

        long likeCount = sharedGameFavoriteRepository.countBySharedGameId(sharedGameId);
        long playCount = sharedGameScoreRepository.countBySharedGameId(sharedGameId);
        Long maxScore  = sharedGameScoreRepository.maxScoreBySharedGameId(sharedGameId);

        // 태그 이름들
        var tagNames = gameTagRepository.findTagNamesBySharedGameId(sharedGameId);

        // DTO 구성
        var dto = new SharedGameFavoriteResponse();
        dto.setSharedGameId(sharedGameId);
        dto.setThumbnailUrl(game.getThumbnailUrl());
        dto.setLiked(liked);
        dto.setLikeCount(likeCount);
        dto.setTotalPlayCount(playCount);
        dto.setTitle(game.getTitle());
        dto.setTags(tagNames.isEmpty() ? null : tagNames.toArray(new String[0]));
        dto.setTopScore(maxScore == null ? null : maxScore.floatValue());

        return ResponseEntity.ok(dto);
    }
}
