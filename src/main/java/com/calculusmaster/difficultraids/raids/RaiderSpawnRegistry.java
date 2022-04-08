package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;

import java.util.HashMap;
import java.util.Map;

public class RaiderSpawnRegistry
{
    public static final Map<String, RaiderSpawnRegistry> SPAWNS = new HashMap<>();

    //TODO: Move this to DifficultRaidsConfig
    public static void init()
    {
        //TODO: Look into using Reflection instead
        //ObfuscationReflectionHelper.findField(Raid.RaiderType.class, "f_37815_").set(Raid.RaiderType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5});

        RaiderSpawnRegistry.createFor("VINDICATOR")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 2, 0, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 2, 2, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 4, 2, 2, 4, 4, 6})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 4, 4, 2, 6, 5, 10})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 20, 25, 30, 34, 45})
                .register();

        RaiderSpawnRegistry.createFor("EVOKER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 0, 1, 1, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 0, 2, 2, 2, 3, 4})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 2, 4, 4, 5, 5, 8})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 8, 10, 12, 16, 20, 25})
                .register();

        RaiderSpawnRegistry.createFor("PILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 4, 3, 3, 4, 4, 4, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 5, 4, 4, 6, 6, 6, 6})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 5, 5, 5, 7, 8, 8, 8})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 5, 3, 6, 7, 7, 12, 13})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 23, 28, 32, 45, 50})
                .register();

        RaiderSpawnRegistry.createFor("WITCH")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 3, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 1, 3, 0, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 2, 2, 3, 3, 3, 5})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 4, 4, 4, 2, 2, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 7, 10, 30, 20, 23, 30})
                .register();

        RaiderSpawnRegistry.createFor("RAVAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 1, 0, 1, 0, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 1, 0, 2, 0, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 0, 0, 1, 0, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 2, 0, 1, 3, 0, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 5, 8, 8, 10, 10, 20})
                .register();
    }

    public static void registerNewRaiders()
    {
        Raid.RaiderType.create("ILLUSIONER", EntityType.ILLUSIONER, new int[]{0, 0, 0, 0, 1, 0, 2, 2});

        RaiderSpawnRegistry.createFor("ILLUSIONER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 1, 0, 2, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 1, 2, 0, 1, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 1, 2, 2, 3, 2, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 1, 2, 2, 4, 3, 5})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 8, 10, 11, 15, 18, 22})
                .register();

        Raid.RaiderType.create("WARRIOR_ILLAGER", DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), new int[]{0, 0, 3, 1, 2, 4, 2, 6});

        RaiderSpawnRegistry.createFor("WARRIOR_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 3, 1, 2, 4, 2, 6})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 2, 4, 2, 3, 4, 3, 8})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 3, 3, 4, 3, 7})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 5, 1, 1, 6, 7, 7})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 16, 16, 25, 25, 30, 40})
                .register();

        Raid.RaiderType.create("DART_ILLAGER", DifficultRaidsEntityTypes.DART_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 1, 1, 2});

        RaiderSpawnRegistry.createFor("DART_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 2, 2, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 1, 1, 3, 4, 5})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 2, 1, 6, 6, 4, 8})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 8, 8, 10, 12, 16, 20, 30})
                .register();

        Raid.RaiderType.create("ELECTRO_ILLAGER", DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 1, 1, 2});

        RaiderSpawnRegistry.createFor("ELECTRO_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 1, 1, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 2, 1, 1, 1, 2, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 0, 3, 1, 5, 2, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 6, 7, 8, 9, 10, 11})
                .register();

        Raid.RaiderType.create("NECROMANCER_ILLAGER", DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), new int[]{0, 0, 0, 0, 1, 0, 0, 1});

        RaiderSpawnRegistry.createFor("NECROMANCER_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 1, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 0, 1, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 1, 1, 1, 0, 2})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 1, 2, 0, 1, 1, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 2, 2, 3, 5, 2, 4, 8})
                .register();

        Raid.RaiderType.create("SHAMAN_ILLAGER", DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get(), new int[]{0, 0, 1, 0, 1, 0, 0, 1});

        RaiderSpawnRegistry.createFor("SHAMAN_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 1, 0, 1, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 1, 1, 1, 1, 0, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 2, 1, 1, 3, 0, 3, 1})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 2, 4, 2, 0, 4, 2})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 4, 4, 3, 1, 6, 6, 6})
                .register();

        Raid.RaiderType.create("TANK_ILLAGER", DifficultRaidsEntityTypes.TANK_ILLAGER.get(), new int[]{0, 0, 2, 1, 2, 4, 2, 1});

        RaiderSpawnRegistry.createFor("TANK_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 2, 1, 2, 4, 2, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 2, 3, 2, 1, 1, 3, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 3, 3, 4, 3, 4})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 2, 1, 1, 3, 4, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 16, 16, 25, 25, 30, 40})
                .register();

        Raid.RaiderType.create("ASSASSIN_ILLAGER", DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get(), new int[]{0, 0, 1, 0, 1, 0, 0, 1});

        RaiderSpawnRegistry.createFor("ASSASSIN_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 1, 0, 1, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 1, 1, 1, 1, 1, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 2, 2, 0, 0, 0, 3, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 0, 3, 0, 5, 5, 5})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 6, 3, 1, 6, 6, 6})
                .register();

        Raid.RaiderType.create("FROST_ILLAGER", DifficultRaidsEntityTypes.FROST_ILLAGER.get(), new int[]{0, 0, 1, 0, 1, 0, 0, 1});

        RaiderSpawnRegistry.createFor("FROST_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 1, 0, 1, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 0, 1, 1, 1, 0, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 2, 2, 4, 2, 1})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 1, 3, 2, 1, 3, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 6, 6, 4, 6, 6, 6})
                .register();
    }

    public static int[] getDefaultSpawns(String raiderType, RaidDifficulty difficulty)
    {
        return SPAWNS
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(raiderType))
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing RaiderDefaultSpawns entry for RaiderType " + raiderType)).getValue()
                .defaults
                .get(difficulty);
    }

    private String raiderType;
    private Map<RaidDifficulty, int[]> defaults;

    public static RaiderSpawnRegistry createFor(String raider)
    {
        RaiderSpawnRegistry spawns = new RaiderSpawnRegistry();
        spawns.raiderType = raider;
        spawns.defaults = new HashMap<>();
        return spawns;
    }

    public RaiderSpawnRegistry withDifficulty(RaidDifficulty difficulty, int[] defaults)
    {
        if(defaults.length != 8) throw new IllegalArgumentException("Raider Default Spawn Count Array must be of length 8!");

        this.defaults.put(difficulty, defaults);
        return this;
    }

    public void register()
    {
        SPAWNS.put(this.raiderType, this);
    }
}
