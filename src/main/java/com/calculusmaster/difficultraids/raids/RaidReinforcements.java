package com.calculusmaster.difficultraids.raids;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum RaidReinforcements
{
    PRESET_1(
            "Axe Party",
            List.of(new RaiderSpawnData(Raid.RaiderType.VINDICATOR, 7)),
            List.of()
    ),
    PRESET_2(
            "Bow Party",
            List.of(new RaiderSpawnData(Raid.RaiderType.PILLAGER, 7)),
            List.of(new EntitySpawnData(EntityType.SKELETON, 5))
    ),
    PRESET_3(
            "Explosives Party",
            List.of(new RaiderSpawnData(Raid.RaiderType.RAVAGER, 2)),
            List.of(new EntitySpawnData(EntityType.CREEPER, 10))
    ),
    PRESET_4(
            "Zombie Horde",
            List.of(),
            List.of(
                    new EntitySpawnData(EntityType.ZOMBIE, 20),
                    new EntitySpawnData(EntityType.HUSK, 5)
            )
    );

    private final String chatName;
    private final List<RaiderSpawnData> raiders;
    private final List<EntitySpawnData> entities;

    RaidReinforcements(String chatName, List<RaiderSpawnData> raiders, List<EntitySpawnData> entities)
    {
        this.chatName = chatName;

        this.raiders = raiders;
        this.entities = entities;
    }

    public static RaidReinforcements getRandom()
    {
        return values()[new Random().nextInt(values().length)];
    }

    public String getChatName()
    {
        return this.chatName;
    }

    public int getRaiderReinforcementCount(Raid.RaiderType type, Difficulty worldDifficulty, RaidDifficulty raidDifficulty)
    {
        if(this.raiders.stream().noneMatch(r -> r.type.equals(type))) return 0;
        return Math.max(0, this.modifyCount(this.raiders.stream().filter(r -> r.type.equals(type)).findFirst().orElseThrow().count(), worldDifficulty, raidDifficulty));
    }

    public Map<EntityType<?>, Integer> getGenericReinforcements(Difficulty worldDifficulty, RaidDifficulty raidDifficulty)
    {
        Map<EntityType<?>, Integer> r = new HashMap<>();
        for(EntitySpawnData s : this.entities) r.put(s.type(), this.modifyCount(s.count(), worldDifficulty, raidDifficulty));
        return r;
    }

    private int modifyCount(int base, Difficulty world, RaidDifficulty raid)
    {
        base += switch (world) {
            case PEACEFUL -> -base;
            case EASY -> -2;
            case NORMAL -> 1;
            case HARD -> 3;
        };

        base *= switch(raid) {
            case DEFAULT -> 1.0;
            case HERO -> 1.25;
            case LEGEND -> 1.5;
            case MASTER -> 2.25;
            case APOCALYPSE -> 4.0;
            case DEBUG -> 0.0;
        };

        return base;
    }

    private record RaiderSpawnData(Raid.RaiderType type, int count) {}
    private record EntitySpawnData(EntityType<?> type, int count) {}
}
