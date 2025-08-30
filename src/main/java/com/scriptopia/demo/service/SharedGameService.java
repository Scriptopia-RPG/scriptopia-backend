package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.History;
import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.sharedgame.MySharedGameResponse;
import com.scriptopia.demo.exception.CustomException;
import com.scriptopia.demo.exception.ErrorCode;
import com.scriptopia.demo.repository.*;
import lombok.RequiredArgsConstructor;
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
    private final GameTagRepository gameTagRepository;

    @Transactional
    public ResponseEntity<?> saveSharedGame(Long Id, Long historyId) {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        if(!history.getUser().getId().equals(Id)) {
            return ResponseEntity.status(403).body("not your history");
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        SharedGame game = sharedGameRepository.findById(sharedId)
                .orElseThrow(() -> new RuntimeException("Shared game not found"));

        if(!game.getUser().getId().equals(user.getId())) {        // 공유된 게임과 로그인한 사용자가 아닌 경우
            throw new RuntimeException("User not your history");
        }

        sharedGameRepository.delete(game);
    }
}
