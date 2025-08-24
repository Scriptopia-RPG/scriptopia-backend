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
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
        Document hi = new Document(Map.ofEntries(
                Map.entry("title", "임시 여정 제목"),
                Map.entry("world_prompt", "임시 세계관 프롬프트"),
                Map.entry("background_story", "AI가 생성한 배경 이야기"),
                Map.entry("world_view", "AI가 생성한 세계관"),
                Map.entry("epilogue_1_title", "엔딩A"),
                Map.entry("epilogue_1_content", "엔딩A 내용"),
                Map.entry("epilogue_2_title", "엔딩B"),
                Map.entry("epilogue_2_content", "엔딩B 내용"),
                Map.entry("epilogue_3_title", "엔딩C"),
                Map.entry("epilogue_3_content", "엔딩C 내용"),
                Map.entry("score", 1234)
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

    @Transactional
    public ResponseEntity<?> createFromMongoLatest(Long userId) {
        Query q = Query.query(Criteria.where("user_id").is(userId))
                .with(Sort.by(Sort.Direction.DESC, "updated_at"))
                .limit(1);

        q.fields().include("user_id").include("updated_at").include("background").include("history_info");
        Document doc = mongoTemplate.findOne(q, Document.class, COLL);
        if(doc == null) return ResponseEntity.badRequest().body("해당 유저의 Mongo 세션이 없습니다.");

        HistoryRequest req = mapMongoToHistoryRequest(doc);
        return createhistory(userId, req);
    }

    @Transactional
    public ResponseEntity<?> createFromMongoSession(Long userId, String sessionIdHex) {
        Document doc = mongoTemplate.findById(new ObjectId(sessionIdHex), Document.class, COLL);
        if(doc == null) return ResponseEntity.badRequest().body("세션 없음");

        JsonNode root = asJson(doc);
        long owner = root.path("user_id").asLong(-1);
        if(owner != userId) return ResponseEntity.status(403).body("본인 세션만 저장 가능");

        HistoryRequest req = mapMongoToHistoryRequest(doc);
        return createhistory(userId, req);
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
        req.setBackgroundStory(hi.path("background_story").asText(null));
        req.setWorldView(hi.path("world_view").asText(null)); // 또는 req.setWorldView(worldPrompt);
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
