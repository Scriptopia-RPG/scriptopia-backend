package com.scriptopia.demo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scriptopia.demo.domain.History;
import com.scriptopia.demo.domain.User;
import com.scriptopia.demo.dto.history.HistoryRequest;
import com.scriptopia.demo.repository.HistoryRepository;
import com.scriptopia.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final MongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    private static final String COLL = "game_session";

    @Transactional
    public ResponseEntity<?> createhistory(Long id, HistoryRequest req) {
        // TODO 유저 인증 구현해야함
        User user = userRepository.findById(id).get();
        History history = new History(user, req);

        return ResponseEntity.ok(historyRepository.save(history));
    }

    @Transactional
    public ResponseEntity<?> seedDummySession(Long userId) {
        Document hi = new Document(Map.of(
                "title", "임시 여정 제목",
                "world_prompt", "임시 세계관 프롬프트",
                "epilogue_1_title", "엔딩A",
                "epilogue_1_content", "엔딩A 내용",
                "epilogue_2_title", "엔딩B",
                "epilogue_2_content", "엔딩B 내용",
                "epilogue_3_title", "엔딩C",
                "epilogue_3_content", "엔딩C 내용",
                "score", 1234
        ));

        Document doc = new Document();
        doc.put("user_id", userId);
        doc.put("scene_type", "done");
        doc.put("started_at", Instant.now());
        doc.put("updated_at", Instant.now());
        doc.put("background", "https://cdn.example.com/bg/temp.png"); // 썸네일로 매핑할 예정
        doc.put("progress", 100);
        doc.put("stage", List.of(1,2,3));
        doc.put("history_info", hi);

        Document saved = mongoTemplate.insert(doc, COLL);
        return ResponseEntity.ok(saved.getObjectId("_id").toHexString());
    }

    private HistoryRequest mapMongoToHistoryRequest(Document doc) {
        JsonNode root = asJson(doc);
        JsonNode hi   = root.path("history_info");

        // 필수값: title, world_prompt, score
        String title       = hi.path("title").asText("");
        String worldPrompt = hi.path("world_prompt").asText("");
        Integer score      = hi.path("score").isNumber() ? hi.path("score").asInt() : null;
        if (title.isBlank() || worldPrompt.isBlank() || score == null) {
            throw new IllegalArgumentException("history_info의 필수값(title, world_prompt, score)이 누락되었습니다.");
        }

        HistoryRequest req = new HistoryRequest();
        // thumbnailUrl: Mongo의 background를 임시 썸네일로 사용
        req.setThumbnailUrl(root.path("background").isTextual() ? root.get("background").asText() : null);

        req.setTitle(title);
        // 정책에 맞게 매핑: worldView는 비워두거나 world_prompt로 대체 가능
        req.setWorldView(null); // 또는 req.setWorldView(worldPrompt);
        req.setBackgroundStory(null); // 필요 시 done_info.story 등에서 요약해 채우기
        req.setWorldPrompt(worldPrompt);

        req.setEpilogue1Title(hi.path("epilogue_1_title").asText(null));
        req.setEpilogue1Content(hi.path("epilogue_1_content").asText(null));
        req.setEpilogue2Title(hi.path("epilogue_2_title").asText(null));
        req.setEpilogue2Content(hi.path("epilogue_2_content").asText(null));
        req.setEpilogue3Title(hi.path("epilogue_3_title").asText(null));
        req.setEpilogue3Content(hi.path("epilogue_3_content").asText(null));

        req.setScore(score.longValue());
        return req;
    }

    private JsonNode asJson(Document doc) {
        try { return objectMapper.readTree(doc.toJson()); }
        catch (Exception e) { throw new RuntimeException("Mongo Document → JsonNode 변환 실패", e); }
    }
}
