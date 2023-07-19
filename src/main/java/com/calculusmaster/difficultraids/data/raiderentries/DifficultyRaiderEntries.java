package com.calculusmaster.difficultraids.data.raiderentries;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class DifficultyRaiderEntries
{
    public static Codec<DifficultyRaiderEntries> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.STRING.comapFlatMap(DifficultyRaiderEntries::parseRaidDifficulty, RaidDifficulty::toString).fieldOf("difficulty").forGetter((DifficultyRaiderEntries o) -> o.raidDifficulty),
                    RaiderEntry.CODEC.listOf().fieldOf("values").forGetter((DifficultyRaiderEntries o) -> o.values)
            ).apply(instance, DifficultyRaiderEntries::new)
    );

    private static DataResult<RaidDifficulty> parseRaidDifficulty(String difficultyString)
    {
        try
        { return DataResult.success(RaidDifficulty.valueOf(difficultyString.toUpperCase())); }
        catch(IllegalArgumentException e)
        { return DataResult.error("Invalid Raid Difficulty: " + difficultyString); }
    }

    private final RaidDifficulty raidDifficulty; public RaidDifficulty getRaidDifficulty() { return this.raidDifficulty; }
    private final List<RaiderEntry> values; public List<RaiderEntry> getValues() { return this.values; }

    public DifficultyRaiderEntries(RaidDifficulty raidDifficulty, List<RaiderEntry> values)
    {
        this.raidDifficulty = raidDifficulty;
        this.values = values;
    }
}
