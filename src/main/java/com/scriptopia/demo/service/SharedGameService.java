package com.scriptopia.demo.service;

import com.scriptopia.demo.domain.History;
import com.scriptopia.demo.domain.SharedGame;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.sharedgame.SharedGameRequest;
import com.scriptopia.demo.repository.HistoryRepository;
import com.scriptopia.demo.repository.SharedGameRepository;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.utils.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SharedGameService {
    private final SharedGameRepository sharedGameRepository;
    private final HistoryRepository historyRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> saveSharedGame(String header, Long historyId) {
        Long userId = jwtProvider.getUserId(header);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        History history = historyRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        if(!history.getUser().getId().equals(userId)) {
            return ResponseEntity.status(403).body("not your history");
        }

        SharedGame sharedGame = SharedGame.from(user, history);
        return ResponseEntity.ok(sharedGameRepository.save(sharedGame));
    }

    @Transactional
    public void deletesharedGame(String header, Long sharedId) {
        Long userId = jwtProvider.getUserId(header);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SharedGame game = sharedGameRepository.findById(sharedId)
                .orElseThrow(() -> new RuntimeException("Shared game not found"));

        if(!game.getUser().getId().equals(userId)) {        // 공유된 게임과 로그인한 사용자가 아닌 경우
            new RuntimeException("User not your history");
        }

        sharedGameRepository.delete(game);
    }
}
