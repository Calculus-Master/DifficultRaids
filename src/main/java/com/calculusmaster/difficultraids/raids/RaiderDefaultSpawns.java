package com.calculusmaster.difficultraids.raids;

import java.util.HashMap;
import java.util.Map;

public class RaiderDefaultSpawns
{
    public static final Map<String, RaiderDefaultSpawns> SPAWNS = new HashMap<>();

    //TODO: Move this to DifficultRaidsConfig
    public static void init()
    {
        RaiderDefaultSpawns.createFor("VINDICATOR")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 2, 0, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 2, 2, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 4, 2, 2, 4, 4, 9})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 5, 4, 4, 5, 9, 10, 16})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 20, 25, 30, 34, 45})
                .register();

        RaiderDefaultSpawns.createFor("EVOKER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 0, 1, 1, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 0, 2, 2, 2, 3, 4})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 2, 4, 4, 5, 5, 8})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 8, 10, 12, 16, 20, 25})
                .register();

        RaiderDefaultSpawns.createFor("PILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 4, 3, 3, 4, 4, 4, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 5, 4, 4, 6, 6, 6, 6})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 5, 5, 5, 7, 8, 8, 10})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 8, 8, 10, 10, 12, 16, 20})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 23, 28, 32, 45, 50})
                .register();

        RaiderDefaultSpawns.createFor("WITCH")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 3, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 1, 3, 0, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 2, 2, 3, 3, 3, 5})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 4, 4, 10, 2, 2, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 7, 10, 30, 20, 23, 30})
                .register();

        RaiderDefaultSpawns.createFor("RAVAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 1, 0, 1, 0, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 1, 0, 1, 0, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 1, 1, 1, 1, 2})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 2, 2, 2, 2, 0, 5})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 5, 8, 8, 10, 10, 20})
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

    public static RaiderDefaultSpawns createFor(String raider)
    {
        RaiderDefaultSpawns spawns = new RaiderDefaultSpawns();
        spawns.raiderType = raider;
        spawns.defaults = new HashMap<>();
        return spawns;
    }

    public RaiderDefaultSpawns withDifficulty(RaidDifficulty difficulty, int[] defaults)
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
