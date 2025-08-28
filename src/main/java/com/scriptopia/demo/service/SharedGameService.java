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
