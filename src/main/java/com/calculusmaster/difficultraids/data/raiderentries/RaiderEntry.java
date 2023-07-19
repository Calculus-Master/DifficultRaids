package com.calculusmaster.difficultraids.data.raiderentries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class RaiderEntry
{
    public static Codec<RaiderEntry> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.fieldOf("raider_type").forGetter((RaiderEntry o) -> o.raiderType),
                    Codec.INT.listOf().fieldOf("wave_counts").forGetter((RaiderEntry o) -> o.waveCounts)
            ).apply(instance, RaiderEntry::new)
    );

    private final String raiderType; public String getRaiderType() { return this.raiderType; }
    private final List<Integer> waveCounts; public List<Integer> getWaveCounts() { return this.waveCounts; }

    public RaiderEntry(String raiderType, List<Integer> waveCounts)
    {
        this.raiderType = raiderType;
        this.waveCounts = waveCounts;
    }
}
