package com.scriptopia.demo.controller;

import com.scriptopia.demo.dto.history.HistoryRequest;
import com.scriptopia.demo.repository.UserRepository;
import com.scriptopia.demo.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    /**
     * 하나의 엔드포인트로 3가지 모드 지원:
     * 1) 바디 저장:           POST /games/{id}/history                   (Body = HistoryRequest)
     * 2) 특정 세션 저장:       POST /games/{id}/history?sessionId=...     (Query)
     * 3) 최신 세션 저장:       POST /games/{id}/history?latest=true       (Query)
     */
    @PostMapping("/{id}/history")
    public ResponseEntity<?> addHistory(@PathVariable Long id,
                                        @RequestParam(required = false) String sessionId,
                                        @RequestParam(required = false, defaultValue = "false") boolean latest,
                                        @RequestBody(required = false) HistoryRequest body) {
        if (sessionId != null && !sessionId.isBlank()) {
            // Mongo 특정 세션(ObjectId)에서 읽어 HistoryRequest로 변환 후 저장
            return historyService.createFromMongoSession(id, sessionId);
        }
        if (latest) {
            // Mongo 최신(updated_at DESC) 세션에서 읽어 HistoryRequest로 변환 후 저장
            return historyService.createFromMongoLatest(id);
        }
        if (body != null) {
            // 프론트가 보낸 HistoryRequest 바디로 저장
            return historyService.createhistory(id, body);
        }
        return ResponseEntity.badRequest().body("body 또는 sessionId/latest 파라미터를 제공하세요.");
    }

    /** 개발용: 로컬 MongoDB에 더미 세션 한 건 심어서 테스트용 ObjectId 반환 */
    @PostMapping("/{id}/history/seed")
    public ResponseEntity<?> seed(@PathVariable Long id) {
        return historyService.seedDummySession(id);
    }
}
