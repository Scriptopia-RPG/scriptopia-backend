package com.scriptopia.demo.utils;

import com.scriptopia.demo.dto.gamesession.ExternalGameResponse;
import com.scriptopia.demo.dto.gamesession.ExternalGameResponse.ItemDef;
import com.scriptopia.demo.dto.gamesession.ExternalGameResponse.ItemDef.ItemEffect;

import java.util.List;

public class GameBalanceUtil {

    // 효과 등급별 공격력 증가 배율
    private static double getEffectGradeMultiplier(String grade) {
        return switch (grade) {
            case "C" -> 0.1;
            case "U" -> 0.15;
            case "R" -> 0.2;
            case "E" -> 0.25;
            case "L" -> 0.3;
            default -> 0.0;
        };
    }

    // 주 스탯 구간별 배율
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
        return 2.0;
    }

    // 캐릭터 스탯 + 장착 아이템 스탯 합산
    public static void applyItemStatsAndCombatPoint(ExternalGameResponse game) {
        ExternalGameResponse.PlayerInfo player = game.getPlayer_info();
        List<ItemDef> items = game.getItem_def();

        int combatPoint = 0;

        for (ItemDef item : items) {
            // 기본 스탯 합산
            player.setStrength(player.getStrength() + item.getStrength());
            player.setAgility(player.getAgility() + item.getAgility());
            player.setIntelligence(player.getIntelligence() + item.getIntelligence());
            player.setLuck(player.getLuck() + item.getLuck());

            // 무기라면 combat_point 계산
            if ("WEAPON".equals(item.getCategory())) {
                double effectMultiplier = item.getItem_effect().stream()
                        .mapToDouble(e -> getEffectGradeMultiplier(e.getGrade()))
                        .sum();

                int mainStatValue = switch (item.getMain_stat()) {
                    case "strength" -> player.getStrength();
                    case "agility" -> player.getAgility();
                    case "intelligence" -> player.getIntelligence();
                    case "luck" -> player.getLuck();
                    default -> 0;
                };

                double mainStatMultiplier = getMainStatMultiplier(mainStatValue);
                combatPoint += (int) (item.getBase_stat() * (1 + effectMultiplier) * mainStatMultiplier);
            }
        }

        player.setCombat_point(combatPoint);
    }
}