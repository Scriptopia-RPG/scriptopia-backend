package com.scriptopia.demo.config;

import com.scriptopia.demo.domain.*;
import com.scriptopia.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 로컬 개발용 초기 데이터 시드
 * - Pia 캐시템 3개 (100~300)
 * - 로컬 계정 유저 2명(userA, userB) + UserSetting 초기값 + PIA 2000
 * - 각 유저: 랜덤 아이템 20개 보유, 그 중 15개 경매장에 등록
 * - 각 유저: 캐릭터 이미지 5개
 * - 각 유저: 히스토리 4개 (그중 2개는 공유, 태그 매핑, 평점/즐겨찾기 약간)
 * - 태그 10개 생성
 * - A↔B 상호 거래 5건 완료(정산 Settlement 기록 포함)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocalDataSeeder implements ApplicationRunner {

    private final ItemDefRepository itemDefRepository;
    private final ItemGradeDefRepository itemGradeDefRepository;
    private final EffectGradeDefRepository effectGradeDefRepository;

    private final UserRepository userRepository;
    private final LocalAccountRepository localAccountRepository;
    private final UserSettingRepository userSettingRepository;

    private final PiaItemRepository piaItemRepository;
    private final UserItemRepository userItemRepository;
    private final AuctionRepository auctionRepository;

    private final TagDefRepository tagDefRepository;
    private final SharedGameRepository sharedGameRepository;
    private final HistoryRepository historyRepository;
    private final GameTagRepository gameTagRepository;
    private final SharedGameScoreRepository sharedGameScoreRepository;
    private final SharedGameFavoriteRepository sharedGameFavoriteRepository;
    private final UserCharacterImgRepository userCharacterImgRepository;

    private final SettlementRepository settlementRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        // 0) 이미 데이터가 있으면 중복 시드 방지
        if (userRepository.count() > 1) {
            return;
        }

        // 1) 태그 10개
        List<TagDef> tags = ensureTags();

        // 2) Pia 캐시 아이템 3개
        ensurePiaItems();

        // 3) 정의 테이블(등급, 효과 등급)
        Map<Grade, ItemGradeDef> gradeMap = ensureItemGradeDefs();
        Map<EffectProbability, EffectGradeDef> effectGradeMap = ensureEffectGradeDefs();

        // 4) 아이템 카탈로그 + 아이템 효과
        ensureItemCatalog(gradeMap, effectGradeMap);

        // 5) 유저 2명 + 로컬계정 + 설정 + pia=2000
        User userA = createUserWithLocal("userA", "userA@example.com", "userA!234");
        User userB = createUserWithLocal("userB", "userB@example.com", "userB!234");
        setUserSettingDefaults(userA);
        setUserSettingDefaults(userB);
        setPia(userA, 2000);
        setPia(userB, 2000);

        // 6) 캐릭터 이미지 5장씩
        addCharacterImages(userA, 5);
        addCharacterImages(userB, 5);

        // 7) 유저 인벤토리 20개(그중 15개 경매 등록)
        List<UserItem> invA = createRandomInventory(userA, 20);
        List<UserItem> invB = createRandomInventory(userB, 20);
        List<Auction> aucA = listFirstNOnAuction(invA, 15); // A가 올린 경매
        List<Auction> aucB = listFirstNOnAuction(invB, 15); // B가 올린 경매

        // 8) 히스토리 4개(그중 2개 공유+태그/평점/즐겨찾기)
        createHistoriesAndShared(userA, tags);
        createHistoriesAndShared(userB, tags);

        // 9) A↔B 상호 거래 5건 완료(정산 기록 포함)
        createTradeLogs(userA, userB, aucA, aucB, 5);

        log.info("[seed] done");
    }

    /* =====================================================================================
       태그 / PIA (정의 테이블)
       ===================================================================================== */

    private List<TagDef> ensureTags() {
        if (tagDefRepository.count() >= 10) {
            return tagDefRepository.findAll();
        }
        List<String> names = List.of("로맨스","판타지","추리","던전","해적","학교물","느와르","우주","요리","타임리프");
        List<TagDef> list = new ArrayList<>();
        for (String n : names) {
            TagDef t = new TagDef();
            t.setTagName(n);
            list.add(t);
        }
        return tagDefRepository.saveAll(list);
    }

    private void ensurePiaItems() {
        if (piaItemRepository.count() >= 3) return;
        PiaItem p1 = new PiaItem(); p1.setName("아이템 모루"); p1.setPrice(200L); p1.setDescription("장비 강화용");
        PiaItem p2 = new PiaItem(); p2.setName("연마석");     p2.setPrice(randL(120, 180)); p2.setDescription("날카로움 보정");
        PiaItem p3 = new PiaItem(); p3.setName("수선 키트");   p3.setPrice(randL(100, 160)); p3.setDescription("내구도 회복");
        piaItemRepository.saveAll(List.of(p1,p2,p3));
    }

    /* =====================================================================================
       ItemGradeDef / EffectGradeDef (정의 테이블)
       ===================================================================================== */

    private Map<Grade, ItemGradeDef> ensureItemGradeDefs() {
        List<ItemGradeDef> existing = itemGradeDefRepository.findAll();
        Map<Grade, ItemGradeDef> byEnum = existing.stream()
                .collect(Collectors.toMap(ItemGradeDef::getGrade, x -> x));

        List<ItemGradeDef> toSave = new ArrayList<>();
        for (Grade g : Grade.values()) {
            if (byEnum.containsKey(g)) continue;
            ItemGradeDef def = new ItemGradeDef();
            def.setGrade(g);
            def.setWeight(g.getDropRate());
            long base = Math.round((g.getAttackPower() + g.getDefensePower()) / 2.0);
            def.setPrice(base * 10L);
            toSave.add(def);
        }
        if (!toSave.isEmpty()) {
            itemGradeDefRepository.saveAll(toSave);
            toSave.forEach(d -> byEnum.put(d.getGrade(), d));
        }
        return byEnum;
    }

    private Map<EffectProbability, EffectGradeDef> ensureEffectGradeDefs() {
        List<EffectGradeDef> existing = effectGradeDefRepository.findAll();
        Map<EffectProbability, EffectGradeDef> byEnum = existing.stream()
                .collect(Collectors.toMap(EffectGradeDef::getEffectProbability, x -> x));

        List<EffectGradeDef> toSave = new ArrayList<>();
        for (EffectProbability p : EffectProbability.values()) {
            if (byEnum.containsKey(p)) continue;
            EffectGradeDef def = new EffectGradeDef();
            def.setEffectProbability(p);
            def.setWeight(
                    switch (p) {
                        case COMMON -> 50d; case UNCOMMON -> 30d; case RARE -> 12d; case EPIC -> 6d; case LEGENDARY -> 2d;
                    }
            );
            def.setPrice(
                    switch (p) {
                        case COMMON -> 20L; case UNCOMMON -> 60L; case RARE -> 150L; case EPIC -> 400L; case LEGENDARY -> 1000L;
                    }
            );
            toSave.add(def);
        }
        if (!toSave.isEmpty()) {
            effectGradeDefRepository.saveAll(toSave);
            toSave.forEach(d -> byEnum.put(d.getEffectProbability(), d));
        }
        return byEnum;
    }

    /* =====================================================================================
       ItemDef + ItemEffect (연관관계 제대로 연결) — 컨셉 기반
       ===================================================================================== */

    private void ensureItemCatalog(Map<Grade, ItemGradeDef> gradeMap,
                                   Map<EffectProbability, EffectGradeDef> effectGradeMap) {
        if (itemDefRepository.count() >= 32) return; // 컨셉 다양화라 넉넉히

        List<ItemDef> toSave = new ArrayList<>();

        for (ItemType type : ItemType.values()) {
            for (Grade g : Grade.values()) {
                int perCombo = 2; // 컨셉 섞어 2개씩 생성
                for (int i = 0; i < perCombo; i++) {
                    String concept = pickConcept();

                    ItemDef d = new ItemDef();
                    d.setItemGradeDef(gradeMap.get(g));
                    d.setItemType(type);
                    d.setMainStat(Stat.getRandomMainStat());

                    // 이름/설명/이미지
                    d.setName(buildItemName(concept, type, g, d.getMainStat()));
                    d.setDescription(buildItemDescription(concept, type, g, d.getMainStat()));
                    d.setPicSrc(picsumWithSeed(300, 400, concept + "-" + type + "-" + g));

                    // 능력치: 등급 기반 + 컨셉/타입 보정
                    int base = Grade.getRandomBaseStat(type, g);
                    int conceptBonus = conceptBaseBonus(concept, type, d.getMainStat());
                    d.setBaseStat(Math.max(1, base + conceptBonus));
                    d.setStrength(rand(0, 10));
                    d.setAgility(rand(0, 10));
                    d.setIntelligence(rand(0, 10));
                    d.setLuck(rand(0, 10));

                    d.setCreatedAt(LocalDateTime.now());

                    long basePrice = Optional.ofNullable(d.getItemGradeDef().getPrice()).orElse(100L);
                    long optSum = nz(d.getStrength()) + nz(d.getAgility()) + nz(d.getIntelligence()) + nz(d.getLuck());
                    long conceptPremium = conceptPricePremium(concept, type, g);
                    d.setPrice(basePrice + optSum * 5 + conceptPremium);

                    // 효과 0~3개
                    int effectCnt = rand(0, 3);
                    for (int k = 0; k < effectCnt; k++) {
                        EffectProbability picked = EffectProbability.getRandomEffectGradeByWeaponGrade(g);
                        if (picked == null) continue;
                        EffectGradeDef egd = effectGradeMap.get(picked);
                        if (egd == null) continue;

                        ItemEffect ef = new ItemEffect();
                        ef.setItemDef(d);
                        ef.setEffectGradeDef(egd);
                        ef.setEffectName(randomEffectName(concept, d.getItemType(), d.getMainStat(), picked));
                        ef.setEffectDescription("[" + picked.name() + "] " + conceptTagline(concept));
                        d.getItemEffects().add(ef);
                    }

                    toSave.add(d);
                }
            }
        }

        itemDefRepository.saveAll(toSave);
    }

    /* =====================================================================================
       유저/설정/PIA/캐릭터이미지/인벤토리/경매/히스토리/공유/정산
       ===================================================================================== */

    private User createUserWithLocal(String nickname, String email, String rawPw) {
        User u = new User();
        u.setNickname(nickname);
        u.setRole(Role.USER);
        u.setLoginType(LoginType.LOCAL);
        u.setCreatedAt(LocalDateTime.now());
        u.setLastLoginAt(LocalDateTime.now());
        u.setProfileImgUrl(picsum(256,256));
        userRepository.save(u);

        LocalAccount acc = new LocalAccount();
        acc.setUser(u);
        acc.setEmail(email);
        acc.setPassword(passwordEncoder.encode(rawPw));
        acc.setStatus(UserStatus.VERIFIED);
        acc.setUpdatedAt(LocalDateTime.now());
        localAccountRepository.save(acc);

        return u;
    }

    private void setUserSettingDefaults(User user) {
        UserSetting s = new UserSetting();
        s.setUser(user);
        s.setTheme(Theme.DARK);
        s.setFontType(FontType.PretendardVariable);
        s.setFontSize(16);
        s.setLineHeight(1);
        s.setWordSpacing(1);
        s.setUpdatedAt(LocalDateTime.now());
        userSettingRepository.save(s);
    }

    private void setPia(User user, long amount) {
        user.setPia(amount);
        userRepository.save(user);
    }

    private void addCharacterImages(User user, int count) {
        List<UserCharacterImg> imgs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserCharacterImg img = new UserCharacterImg();
            img.setUser(user);
            img.setImgUrl("https://picsum.photos/seed/" + user.getId() + "-" + i + "/256/256");
            imgs.add(img);
        }
        userCharacterImgRepository.saveAll(imgs);
    }

    private List<UserItem> createRandomInventory(User owner, int count) {
        List<ItemDef> catalog = itemDefRepository.findAll();
        if (catalog.isEmpty()) throw new IllegalStateException("Item catalog empty");
        List<UserItem> items = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ItemDef base = catalog.get(rand(0, catalog.size()-1));
            UserItem ui = new UserItem();
            ui.setUser(owner);
            ui.setItemDef(base);
            ui.setTradeStatus(TradeStatus.OWNED);
            ui.setRemainingUses(rand(0,10));
            items.add(ui);
        }
        return userItemRepository.saveAll(items);
    }

    private List<Auction> listFirstNOnAuction(List<UserItem> items, int n) {
        List<Auction> created = new ArrayList<>();
        items.stream().limit(n).forEach(ui -> {
            ui.setTradeStatus(TradeStatus.LISTED);
            userItemRepository.save(ui);

            Auction a = new Auction();
            a.setUserItem(ui);
            a.setPrice(randL(200, 1800));
            a.setCreatedAt(LocalDateTime.now().minusHours(rand(0,48)));
            // 미완료 상태(tradedAt=null)
            created.add(auctionRepository.save(a));
        });
        return created;
    }

    private void createHistoriesAndShared(User user, List<TagDef> tags) {
        // 히스토리 4개
        List<History> histories = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            History h = new History();
            h.setUser(user);
            h.setUuid(UUID.randomUUID());
            h.setThumbnailUrl(picsum(300, 400));
            h.setTitle(user.getNickname() + "의 모험 #" + (i+1));
            h.setScore((long) rand(60,98));
            h.setCreatedAt(LocalDateTime.now().minusDays(rand(0,10)));
            h.setIsShared(false);
            histories.add(h);
        }
        historyRepository.saveAll(histories);

        // 그중 2개 공유
        for (int i = 0; i < 2; i++) {
            History h = histories.get(i);
            h.setIsShared(true);
            historyRepository.save(h);

            SharedGame sg = new SharedGame();
            sg.setUser(user);
            sg.setUuid(h.getUuid());
            sg.setThumbnailUrl(h.getThumbnailUrl());
            sg.setTitle(h.getTitle());
            sg.setWorldView("임시 세계관");
            sg.setBackgroundStory("임시 배경");
            sg.setSharedAt(LocalDateTime.now());
            sharedGameRepository.save(sg);

            // 태그 2~5개
            Collections.shuffle(tags);
            int tagCount = rand(2,5);
            for (int k=0; k<tagCount; k++) {
                GameTag gt = new GameTag();
                gt.setSharedGame(sg);
                gt.setTagDef(tags.get(k));
                gameTagRepository.save(gt);
            }

            // 평점 1~2개
            int scoreN = rand(1,2);
            for (int s=0; s<scoreN; s++) {
                SharedGameScore sgs = new SharedGameScore();
                sgs.setSharedGame(sg);
                sgs.setUser(user);
                sgs.setScore((long) rand(3,5));
                sgs.setCreatedAt(LocalDateTime.now());
                sharedGameScoreRepository.save(sgs);
            }

            // 즐겨찾기 0~1개
            if (randBool()) {
                SharedGameFavorite fav = new SharedGameFavorite();
                fav.setSharedGame(sg);
                fav.setUser(user);
                sharedGameFavoriteRepository.save(fav);
            }
        }
    }

    /* ============================ 거래 완료 + 정산 기록 ============================ */

    /**
     * A와 B가 서로의 경매품을 사는 거래를 totalTrades 만큼 완료 처리한다.
     * - Auction.tradedAt 설정 → "완료 로그"
     * - UserItem 소유권 구매자에게 이전, 상태 OWNED
     * - PIA 정산(구매자 차감, 판매자 증가)
     * - Settlement 2건 생성(BUY/SELL)
     */
    private void createTradeLogs(User userA, User userB,
                                 List<Auction> aucA, List<Auction> aucB,
                                 int totalTrades) {
        Collections.shuffle(aucA);
        Collections.shuffle(aucB);

        int byB = Math.min(totalTrades / 2, aucA.size());                 // B가 A 물건 구매
        int byA = Math.min(totalTrades - byB, aucB.size());               // A가 B 물건 구매

        for (int i = 0; i < byB; i++) finalizeTrade(userA, userB, aucA.get(i));
        for (int i = 0; i < byA; i++) finalizeTrade(userB, userA, aucB.get(i));
    }

    /** 단일 거래 완료 처리 */
    private void finalizeTrade(User seller, User buyer, Auction auction) {
        if (auction.getTradedAt() != null) return; // 이미 완료된 거래

        UserItem ui = auction.getUserItem();
        long price = Optional.ofNullable(auction.getPrice()).orElse(0L);

        // 구매자 잔액 확인(부족하면 스킵)
        if (buyer.getPia() == null || buyer.getPia() < price) return;

        // 정산
        buyer.setPia(buyer.getPia() - price);
        seller.setPia(Optional.ofNullable(seller.getPia()).orElse(0L) + price);
        userRepository.saveAll(List.of(buyer, seller));

        // 소유권 이전 + 상태 변경
        ui.setUser(buyer);
        ui.setTradeStatus(TradeStatus.OWNED);
        userItemRepository.save(ui);

        // 경매 완료 시간 기록
        auction.setTradedAt(LocalDateTime.now());
        auctionRepository.save(auction);

        ItemDef itemDef = ui.getItemDef();

        // Settlement: 판매자(SELL)
        Settlement sellSettle = new Settlement();
        sellSettle.setUser(seller);
        sellSettle.setItemDef(itemDef);
        sellSettle.setTradeType(TradeType.SELL);  // enum: SELL/BUY 필요
        sellSettle.setPrice(price);
        sellSettle.setCreatedAt(LocalDateTime.now());
        sellSettle.setSettledAt(LocalDateTime.now());
        settlementRepository.save(sellSettle);

        // Settlement: 구매자(BUY)
        Settlement buySettle = new Settlement();
        buySettle.setUser(buyer);
        buySettle.setItemDef(itemDef);
        buySettle.setTradeType(TradeType.BUY);
        buySettle.setPrice(price);
        buySettle.setCreatedAt(LocalDateTime.now());
        buySettle.setSettledAt(LocalDateTime.now());
        settlementRepository.save(buySettle);
    }

    /* =====================================================================================
       유틸
       ===================================================================================== */

    // ======================= 컨셉 사전 =======================
    private static final List<String> CONCEPTS = List.of(
            "스팀펑크", "사이버네온", "암흑 판타지", "동양 무협", "우주 SF",
            "요리 배틀", "해적 시대", "포스트 아포칼립스", "중세 마법학원", "바이오펑크"
    );

    private static String pickConcept() { return CONCEPTS.get(rand(0, CONCEPTS.size()-1)); }

    private static String conceptTagline(String concept) {
        return switch (concept) {
            case "스팀펑크" -> "황동과 기어의 울림";
            case "사이버네온" -> "빛번짐 속 프로토콜";
            case "암흑 판타지" -> "어둠이 가르는 맹세";
            case "동양 무협" -> "내공과 검기";
            case "우주 SF" -> "진공 너머 특이점";
            case "요리 배틀" -> "칼끝에서 피어나는 풍미";
            case "해적 시대" -> "검과 파도, 검은 깃발";
            case "포스트 아포칼립스" -> "폐허 속 생존 기술";
            case "중세 마법학원" -> "룬과 마력회로";
            case "바이오펑크" -> "세포 공학적 변이";
            default -> "특별한 콘셉트";
        };
    }

    private static String buildItemName(String concept, ItemType type, Grade g, Stat main) {
        String gradePrefix = switch (g) {
            case LEGENDARY -> "전설의 ";
            case EPIC -> "에픽 ";
            case RARE -> "희귀 ";
            case UNCOMMON -> "고급 ";
            case COMMON -> "";
        };

        String noun = switch (type) {
            case WEAPON -> pickOne(List.of("기어블레이드", "광자검", "혈문도", "룬스태프", "해적커틀러스", "강철장도", "마나활", "열압권총"));
            case ARMOR -> pickOne(List.of("기계갑옷", "네온코트", "어둠의 흉갑", "비단갑", "우주복", "셰프앞치마", "해골흉갑", "겐지로브"));
            case ARTIFACT -> pickOne(List.of("증기코어", "신경임플란트", "어비스 보주", "학원 배지", "항성 파편", "미각 토템"));
            case POTION -> pickOne(List.of("촉매 엘릭서", "신경강화제", "밤피의 혈약", "기혈단", "중력완화제", "풍미증폭 소스"));
        };

        String conceptAdj = switch (concept) {
            case "스팀펑크" -> pickOne(List.of("황동제", "증기식", "기어식"));
            case "사이버네온" -> pickOne(List.of("네온-튜닝", "양자", "신경망"));
            case "암흑 판타지" -> pickOne(List.of("그림자", "혈문", "망령"));
            case "동양 무협" -> pickOne(List.of("벽력", "천검", "비연"));
            case "우주 SF" -> pickOne(List.of("중성자", "쿼크", "항성"));
            case "요리 배틀" -> pickOne(List.of("주방장", "풍미", "향신"));
            case "해적 시대" -> pickOne(List.of("검은깃발", "해골", "산호"));
            case "포스트 아포칼립스" -> pickOne(List.of("폐허산", "방사", "고철"));
            case "중세 마법학원" -> pickOne(List.of("룬각", "비전", "원소"));
            case "바이오펑크" -> pickOne(List.of("유전자", "세포", "점액질"));
            default -> "";
        };

        String mainHint = switch (main) {
            case STRENGTH -> "괴력";
            case AGILITY -> "신속";
            case INTELLIGENCE -> "지성";
            case LUCK -> "포츈";
        };

        return gradePrefix + conceptAdj + " " + noun + " • " + mainHint;
    }

    private static String buildItemDescription(String concept, ItemType type, Grade g, Stat main) {
        String line1 = "세계관: " + concept + " | 주 스탯: " + main;
        String line2 = switch (type) {
            case WEAPON -> "공격기반 무기. " + conceptTagline(concept);
            case ARMOR -> "방어/생존 특화. " + conceptTagline(concept);
            case ARTIFACT -> "특수 패시브/효과 중심. " + conceptTagline(concept);
            case POTION -> "일시적 버프/회복. " + conceptTagline(concept);
        };
        String line3 = switch (g) {
            case LEGENDARY -> "희귀한 제작법이 전해진다.";
            case EPIC -> "베테랑 장인들의 정수가 담겼다.";
            case RARE -> "전투에서 검증된 성능.";
            case UNCOMMON -> "균형 잡힌 성능.";
            case COMMON -> "보급형 표준 모델.";
        };
        return line1 + "\n" + line2 + "\n" + line3;
    }

    private static int conceptBaseBonus(String concept, ItemType type, Stat main) {
        int bias = 0;
        if (concept.equals("스팀펑크") && type == ItemType.WEAPON) bias += 6;
        if (concept.equals("사이버네온") && (main == Stat.AGILITY || main == Stat.INTELLIGENCE)) bias += 8;
        if (concept.equals("암흑 판타지") && type == ItemType.ARMOR) bias += 5;
        if (concept.equals("동양 무협") && (type == ItemType.WEAPON || main == Stat.STRENGTH)) bias += 7;
        if (concept.equals("우주 SF") && type == ItemType.ARTIFACT) bias += 9;
        if (concept.equals("요리 배틀") && type == ItemType.POTION) bias += 10;
        if (concept.equals("해적 시대") && main == Stat.LUCK) bias += 6;
        if (concept.equals("포스트 아포칼립스") && type == ItemType.ARMOR) bias += 4;
        if (concept.equals("중세 마법학원") && main == Stat.INTELLIGENCE) bias += 8;
        if (concept.equals("바이오펑크") && type == ItemType.ARTIFACT) bias += 6;

        bias += rand(-3, 3);
        return bias;
    }

    private static long conceptPricePremium(String concept, ItemType type, Grade g) {
        int tier = switch (g) {
            case LEGENDARY -> 5; case EPIC -> 4; case RARE -> 3; case UNCOMMON -> 2; case COMMON -> 1;
        };
        int base = switch (type) {
            case WEAPON -> 60; case ARMOR -> 45; case ARTIFACT -> 80; case POTION -> 25;
        };
        int conceptFactor = switch (concept) {
            case "우주 SF", "바이오펑크" -> 50;
            case "사이버네온", "중세 마법학원" -> 40;
            case "스팀펑크", "암흑 판타지" -> 35;
            case "해적 시대" -> 30;
            case "포스트 아포칼립스" -> 28;
            case "동양 무협" -> 32;
            case "요리 배틀" -> 20;
            default -> 25;
        };
        return (long) ((base + conceptFactor) * tier);
    }

    private static String pickOne(List<String> list) { return list.get(rand(0, list.size()-1)); }
    private static String picsumWithSeed(int w, int h, String seed) {
        return "https://picsum.photos/seed/" + seed.replaceAll("\\s+","_") + "/" + w + "/" + h;
    }

    private static int rand(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    private static long randL(int min, int max) { return rand(min, max); }
    private static boolean randBool() { return ThreadLocalRandom.current().nextBoolean(); }
    private static String token() { String a="ABCDEFGHJKLMNPQRSTUVWXYZ"; return ""+a.charAt(rand(0,a.length()-1))+rand(0,999); }
    private static String picsum(int w, int h) { return "https://picsum.photos/" + w + "/" + h + "?random=" + UUID.randomUUID(); }
    private static String lorem(int words) {
        String base="An ancient relic hums with latent power as shadows gather over Scriptopia ";
        String[] arr=base.split("\\s+"); StringBuilder sb=new StringBuilder();
        for(int i=0;i<words;i++) sb.append(arr[i%arr.length]).append(' ');
        return sb.toString().trim();
    }
    private static int nz(Integer v){ return v==null?0:v; }

    private static String randomEffectName(String concept, ItemType type, Stat mainStat, EffectProbability grade) {
        List<String> pool = new ArrayList<>();

        // 컨셉 시그니처
        switch (concept) {
            case "스팀펑크" -> pool.addAll(List.of("증기 분출", "기어 과부하", "압력 누적", "피스톤 충격"));
            case "사이버네온" -> pool.addAll(List.of("신경 가속", "패킷 주입", "광자 잔상", "방화벽 관통"));
            case "암흑 판타지" -> pool.addAll(List.of("그림자 맹세", "피의 저주", "망령 서약", "어비스의 응시"));
            case "동양 무협" -> pool.addAll(List.of("검기 방출", "경공술", "내공 폭진", "기혈 순환"));
            case "우주 SF" -> pool.addAll(List.of("중력 왜곡", "차원 흔들림", "항성열 방사", "양자 난류"));
            case "요리 배틀" -> pool.addAll(List.of("풍미 증폭", "감칠맛 폭격", "식감 강화", "향신료 분사"));
            case "해적 시대" -> pool.addAll(List.of("대포 사격", "돛바람 가속", "해안 급습", "검은 파도"));
            case "포스트 아포칼립스" -> pool.addAll(List.of("방사 저항", "고철 방패", "연료 분사", "황폐의 굴레"));
            case "중세 마법학원" -> pool.addAll(List.of("룬 각성", "비전 증폭", "소환 공명", "원소 가호"));
            case "바이오펑크" -> pool.addAll(List.of("세포 재생", "신경 동조", "점액질 보호막", "유전자 각성"));
        }

        // 타입 기반 추가
        switch (type) {
            case WEAPON -> pool.addAll(List.of("치명타 확률", "관통", "연격", "출혈", "화염 부여"));
            case ARMOR -> pool.addAll(List.of("피해 감소", "빙결 저항", "화염 저항", "보호막", "재생"));
            case ARTIFACT -> pool.addAll(List.of("축복", "가속", "행운의 일격", "마력 증폭", "집중"));
            case POTION -> pool.addAll(List.of("즉시 회복", "해제", "광폭화", "은신", "정화"));
        }

        // 메인 스탯 보정
        switch (mainStat) {
            case STRENGTH -> pool.addAll(List.of("분쇄", "분노", "괴력"));
            case AGILITY -> pool.addAll(List.of("신속", "회피", "날렵함"));
            case INTELLIGENCE -> pool.addAll(List.of("지성 증폭", "집중", "분석"));
            case LUCK -> pool.addAll(List.of("포춘", "크리 운빨", "은총"));
        }

        // 등급 접두사
        String prefix = switch (grade) {
            case LEGENDARY -> "전설의 ";
            case EPIC -> "에픽 ";
            case RARE -> "희귀 ";
            case UNCOMMON -> "고급 ";
            case COMMON -> "";
        };

        if (pool.isEmpty()) pool = List.of("특수 효과");
        return prefix + pool.get(rand(0, pool.size() - 1));
    }
}
