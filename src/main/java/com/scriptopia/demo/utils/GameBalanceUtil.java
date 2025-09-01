package com.scriptopia.demo.utils;

import com.scriptopia.demo.domain.Grade;
import com.scriptopia.demo.domain.ItemType;
import com.scriptopia.demo.dto.gamesession.ExternalGameResponse;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameBalanceUtil {
    static SecureRandom secureRandom = new SecureRandom();

    // 착용 무기 적용 후 캐릭터 스탯 및 combat_point 계산
    public static void applyEquippedWeaponStatsAndCombatPoint(ExternalGameResponse game) {
        ExternalGameResponse.PlayerInfo player = game.getPlayerInfo();
        List<ExternalGameResponse.ItemDef> itemDefs = game.getItemDef();
        List<ExternalGameResponse.InventoryItem> inventory = game.getInventory();

        // item_def_id 기준 Map 생성
        Map<Long, ExternalGameResponse.ItemDef> itemDefMap = itemDefs.stream()
                .collect(Collectors.toMap(ExternalGameResponse.ItemDef::getItemDefId, item -> item));

        // 착용 중인 무기 하나 찾기
        ExternalGameResponse.ItemDef equippedWeapon = inventory.stream()
                .filter(ExternalGameResponse.InventoryItem::isEquipped)
                .map(inv -> itemDefMap.get(inv.getItemDefId()))
                .filter(item -> item != null && item.getCategory() == ItemType.WEAPON)
                .findFirst()
                .orElse(null);

        if (equippedWeapon != null) {
            // 1. 무기 스탯 합산
            player.setStrength(player.getStrength() + equippedWeapon.getStrength());
            player.setAgility(player.getAgility() + equippedWeapon.getAgility());
            player.setIntelligence(player.getIntelligence() + equippedWeapon.getIntelligence());
            player.setLuck(player.getLuck() + equippedWeapon.getLuck());

            // 2. combat_point 계산
            double effectMultiplier = equippedWeapon.getItemEffect().stream()
                    .mapToDouble(e -> getEffectGradeMultiplier(e.getGrade()))
                    .sum();

            int mainStatValue = switch (equippedWeapon.getMainStat()) {
                case STRENGTH -> player.getStrength();
                case AGILITY -> player.getAgility();
                case INTELLIGENCE -> player.getIntelligence();
                case LUCK -> player.getLuck();
                default -> 0;
            };

            double mainStatMultiplier = getMainStatMultiplier(mainStatValue);

            int combatPoint = (int) (equippedWeapon.getBaseStat() * (1 + effectMultiplier) * mainStatMultiplier);

            player.setCombatPoint(combatPoint);
        } else {
            // 무기 미착용 시: 스탯 합 * 가장 높은 스탯 배율
            int totalStat = player.getStrength() + player.getAgility() + player.getIntelligence() + player.getLuck();
            int maxStat = Math.max(Math.max(player.getStrength(), player.getAgility()),
                    Math.max(player.getIntelligence(), player.getLuck()));
            double mainStatMultiplier = getMainStatMultiplier(maxStat);
            int combatPoint = (int) (totalStat * mainStatMultiplier);
            player.setCombatPoint(combatPoint);
        }
    }

    // 착용 방어구 적용 후 캐릭터 스탯 및 health_point 계산
    public static void applyEquippedArmorStatsAndHealthPoint(ExternalGameResponse game) {
        ExternalGameResponse.PlayerInfo player = game.getPlayerInfo();
        List<ExternalGameResponse.ItemDef> itemDefs = game.getItemDef();
        List<ExternalGameResponse.InventoryItem> inventory = game.getInventory();

        // item_def_id 기준 Map 생성
        Map<Long, ExternalGameResponse.ItemDef> itemDefMap = itemDefs.stream()
                .collect(Collectors.toMap(ExternalGameResponse.ItemDef::getItemDefId, item -> item));

        // 착용 중인 방어구 하나 찾기
        ExternalGameResponse.ItemDef equippedArmor = inventory.stream()
                .filter(ExternalGameResponse.InventoryItem::isEquipped)
                .map(inv -> itemDefMap.get(inv.getItemDefId()))
                .filter(item -> item != null && item.getCategory() == ItemType.ARMOR)
                .findFirst()
                .orElse(null);

        if (equippedArmor != null) {
            // 1. 방어구 스탯 합산
            player.setStrength(player.getStrength() + equippedArmor.getStrength());
            player.setAgility(player.getAgility() + equippedArmor.getAgility());
            player.setIntelligence(player.getIntelligence() + equippedArmor.getIntelligence());
            player.setLuck(player.getLuck() + equippedArmor.getLuck());

            // 2. health_point 계산
            double effectMultiplier = equippedArmor.getItemEffect().stream()
                    .mapToDouble(e -> getEffectGradeMultiplier(e.getGrade()))
                    .sum();

            int baseHealth = equippedArmor.getBaseStat();
            int healthPoint = (int) (baseHealth * (1 + effectMultiplier));
            player.setHealthPoint(healthPoint);
        } else {
            // 방어구 미착용 시: 전체 스탯 합 * 3
            int totalStat = player.getStrength() + player.getAgility() + player.getIntelligence() + player.getLuck();
            int healthPoint = totalStat * 3;
            player.setHealthPoint(healthPoint);
        }
    }


    // 착용 아티팩트 적용 후 캐릭터 스탯 계산 (전투력/체력 보정은 추후)
    public static void applyEquippedArtifactStats(ExternalGameResponse game) {
        ExternalGameResponse.PlayerInfo player = game.getPlayerInfo();
        List<ExternalGameResponse.ItemDef> itemDefs = game.getItemDef();
        List<ExternalGameResponse.InventoryItem> inventory = game.getInventory();

        // item_def_id 기준 Map 생성
        Map<Long, ExternalGameResponse.ItemDef> itemDefMap = itemDefs.stream()
                .collect(Collectors.toMap(ExternalGameResponse.ItemDef::getItemDefId, item -> item));

        // 착용 중인 아티팩트 하나 찾기
        ExternalGameResponse.ItemDef equippedArtifact = inventory.stream()
                .filter(ExternalGameResponse.InventoryItem::isEquipped)
                .map(inv -> itemDefMap.get(inv.getItemDefId()))
                .filter(item -> item != null && item.getCategory() == ItemType.ARTIFACT)
                .findFirst()
                .orElse(null);

        if (equippedArtifact != null) {
            // 아티팩트 스탯 합산
            player.setStrength(player.getStrength() + equippedArtifact.getStrength());
            player.setAgility(player.getAgility() + equippedArtifact.getAgility());
            player.setIntelligence(player.getIntelligence() + equippedArtifact.getIntelligence());
            player.setLuck(player.getLuck() + equippedArtifact.getLuck());
        }
    }


    // 아이템 효과 등급 배율
    private static double getEffectGradeMultiplier(Grade grade) {
        return switch (grade) {
            case COMMON -> 0.1;
            case UNCOMMON -> 0.15;
            case RARE -> 0.2;
            case EPIC -> 0.25;
            case LEGENDARY -> 0.3;
        };
    }

    // 메인 스탯 값 구간 배율
    private static double getMainStatMultiplier(int statValue) {
        if (statValue <= 5) return 1.1;
        if (statValue <= 10) return 1.2;
        if (statValue <= 15) return 1.3;
        if (statValue <= 20) return 1.4;
        if (statValue <= 25) return 1.5;
        if (statValue <= 30) return 1.6;
        if (statValue <= 35) return 1.7;
        if (statValue <= 40) return 1.8;
        if (statValue <= 45) return 1.9;
        return 2.0; // 46 이상
    }


    public static int[] initItemStat(Grade grade) {
        int[] stats = new int[4]; // str,agi,int,luk
        int itemBaseStat = 0;

        switch (grade) {
            case COMMON -> itemBaseStat = secureRandom.nextInt(4);
            case UNCOMMON -> itemBaseStat = secureRandom.nextInt(4) + 1;
            case RARE -> itemBaseStat = secureRandom.nextInt(4) + 2;
            case EPIC -> itemBaseStat = secureRandom.nextInt(4) + 4;
            case LEGENDARY -> itemBaseStat = secureRandom.nextInt(4) + 5;
        }

        for(int i = 0; i < itemBaseStat; i++){
            stats[secureRandom.nextInt(4)] += 1;
        }
        return stats;
    }
}
