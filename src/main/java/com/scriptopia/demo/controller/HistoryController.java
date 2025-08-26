package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryPageResponse;
import com.scriptopia.demo.dto.history.HistoryRequest;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    /**
     * 현재는 userId, sessionId를 통해 저장하는데
     * 인증 관리 부분 끝나면 header에 token 꺼내오고 requestparameter session_id로 저장하게 수정
     */
    @PostMapping("/{id}/history/{sid}")
    public ResponseEntity<?> addHistory(@PathVariable Long id, @PathVariable String sid) {
        return historyService.createHistory(id, sid);
    }

    /** 개발용: 로컬 MongoDB에 더미 세션 한 건 심어서 테스트용 ObjectId 반환 */
    @PostMapping("/{id}/history/seed")
    public ResponseEntity<?> seed(@PathVariable Long id) {
        return historyService.seedDummySession(id);
    }
}
