package com.calculusmaster.difficultraids.util;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.raid.Raid;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum BonusRaidSpawnPreset
{
    PRESET_1(
            "Axe Party",
            new Spawn(Raid.RaiderType.VINDICATOR, 7)
    ),
    PRESET_2(
            "Crossbow Party",
            new Spawn(Raid.RaiderType.PILLAGER, 7)
    );

    private final String chatName;
    private final Map<Raid.RaiderType, Integer> bonusSpawns;

    BonusRaidSpawnPreset(String chatName, Spawn... spawns)
    {
        this.chatName = chatName;

        this.bonusSpawns = new HashMap<>();
        Arrays.stream(spawns).forEach(s -> this.bonusSpawns.put(s.type(), s.count()));
    }

    public static BonusRaidSpawnPreset getRandom()
    {
        return values()[new Random().nextInt(values().length)];
    }

    public String getChatName()
    {
        return this.chatName;
    }

    public int getBonusSpawnCount(Raid.RaiderType type, Difficulty worldDifficulty, RaidDifficulty raidDifficulty)
    {
        if(!this.bonusSpawns.containsKey(type)) return 0;

        int base = this.bonusSpawns.get(type);

        base += switch (worldDifficulty) {
            case PEACEFUL -> -base;
            case EASY -> -2;
            case NORMAL -> 1;
            case HARD -> 3;
        };

        base *= switch(raidDifficulty) {
            case DEFAULT -> 1.0;
            case HERO -> 1.25;
            case LEGEND -> 1.5;
            case MASTER -> 2.25;
            case APOCALYPSE -> 4.0;
            case DEBUG -> 0.0;
        };

        //Avoid negative spawn counts
        if(base < 0) base = 0;

        return base;
    }

    private record Spawn(Raid.RaiderType type, int count) {}
}
