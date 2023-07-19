package com.calculusmaster.difficultraids.raids;

import net.minecraft.world.entity.EntityType;

import java.util.*;
import java.util.stream.Collectors;

public class RaidDifficultyEnemyManager
{
    private final RaidDifficulty raidDifficulty;
    private final Map<String, List<Integer>> raiderWaves;
    private final Map<Integer, List<EntityType<?>>> eliteWaves;

    public RaidDifficultyEnemyManager(RaidDifficulty raidDifficulty)
    {
        this.raidDifficulty = raidDifficulty;
        this.raiderWaves = new HashMap<>();
        this.eliteWaves = new HashMap<>();
    }

    public RaidDifficultyEnemyManager(RaidDifficultyEnemyManager source)
    {
        this(source.raidDifficulty);

        source.raiderWaves.forEach((t, l) -> this.raiderWaves.put(t, new ArrayList<>(List.copyOf(l))));
        source.eliteWaves.forEach((i, l) -> this.eliteWaves.put(i, new ArrayList<>(List.copyOf(l))));
    }

    public void add(String raiderType, List<Integer> counts, boolean replace)
    {
        if(!this.raiderWaves.containsKey(raiderType) || replace)
            this.raiderWaves.put(raiderType, counts);
        else
        {
            List<Integer> current = new ArrayList<>(this.raiderWaves.get(raiderType));

            for(int i = 0; i < Math.min(current.size(), counts.size()); i++)
            {
                int count = Math.max(0, current.get(i) + counts.get(i));
                current.set(i, count);
            }

            if(counts.size() > current.size())
                current.addAll(counts.subList(current.size(), counts.size()));

            this.raiderWaves.put(raiderType, current);
        }
    }

    public Map<String, List<Integer>> getWaves()
    {
        return this.raiderWaves;
    }

    public boolean isEliteWave(int wave)
    {
        return this.eliteWaves.containsKey(wave);
    }

    public List<EntityType<?>> getElites(int wave)
    {
        return this.eliteWaves.getOrDefault(wave, new ArrayList<>());
    }

    @Override
    public String toString()
    {
        return "{%s, RaiderEntries=%s}".formatted(
            this.raidDifficulty.toString(),
            this.raiderWaves.entrySet().stream()
                .map(e -> "{%s: %s}".formatted(e.getKey(), e.getValue().toString()))
                .collect(Collectors.joining(", "))
        );
    }

    //Builder
    public static RaidDifficultyEnemyManager create(RaidDifficulty raidDifficulty)
    {
        return new RaidDifficultyEnemyManager(raidDifficulty);
    }

    public RaidDifficultyEnemyManager withRaider(String raiderType, int... counts)
    {
        this.raiderWaves.put(raiderType, Arrays.stream(counts).boxed().collect(Collectors.toList()));
        return this;
    }

    public RaidDifficultyEnemyManager withEliteWave(int wave, EntityType<?>... types)
    {
        this.eliteWaves.put(wave, Arrays.stream(types).collect(Collectors.toList()));
        return this;
    }

    public void registerDefault()
    {
        RaidEnemyRegistry.DEFAULT_WAVES.put(this.raidDifficulty, this);
        RaidEnemyRegistry.REGISTERED_RAIDER_TYPES.addAll(this.raiderWaves.keySet());
    }
}