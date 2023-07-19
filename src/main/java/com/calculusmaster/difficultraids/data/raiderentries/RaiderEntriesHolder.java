package com.calculusmaster.difficultraids.data.raiderentries;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RaiderEntriesHolder
{
    private final Map<RaidDifficulty, Map<String, List<Integer>>> waves;
    private boolean replace;

    public RaiderEntriesHolder()
    {
        this.waves = new LinkedHashMap<>();
        this.replace = false;
    }

    public RaiderEntriesHolder(List<DifficultyRaiderEntries> list)
    {
        this();
        list.forEach(this::merge);
    }

    public void merge(DifficultyRaiderEntries data)
    {
        if(!this.waves.containsKey(data.getRaidDifficulty())) this.waves.put(data.getRaidDifficulty(), new HashMap<>());

        data.getValues().forEach(entries -> this.waves.get(data.getRaidDifficulty()).put(entries.getRaiderType(), entries.getWaveCounts()));
    }

    public Map<RaidDifficulty, Map<String, List<Integer>>> getWaves()
    {
        return this.waves;
    }

    public void setReplace(boolean replace)
    {
        this.replace = replace;
    }

    public boolean isReplace()
    {
        return this.replace;
    }

    @Override
    public String toString()
    {
        return this.waves.entrySet().stream()
                .map(e -> "{%s, RaiderEntries=%s}".formatted(
                        e.getKey().toString(),
                        e.getValue().entrySet().stream()
                                .map(e2 -> "{%s: %s}".formatted(e2.getKey(), e2.getValue().toString()))
                                .collect(Collectors.joining(", "))
                        )
                ).collect(Collectors.joining(" | "));
    }
}
