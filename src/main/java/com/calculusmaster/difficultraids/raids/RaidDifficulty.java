package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;

import java.util.List;

public enum RaidDifficulty
{
    DEFAULT,
    HERO,
    LEGEND,
    MASTER,
    APOCALYPSE;

    public static RaidDifficulty current()
    {
        return DifficultRaidsConfig.RAID_DIFFICULTY.get();
    }

    public DifficultRaidsConfig.RaidDifficultyConfig config()
    {
        return switch(this) {
            case HERO -> DifficultRaidsConfig.HERO_CONFIG;
            case LEGEND -> DifficultRaidsConfig.LEGEND_CONFIG;
            case MASTER -> DifficultRaidsConfig.MASTER_CONFIG;
            case APOCALYPSE -> DifficultRaidsConfig.APOCALYPSE_CONFIG;
            default -> DifficultRaidsConfig.DEFAULT_CONFIG;
        };
    }

    public boolean isDefault()
    {
        return this.equals(DEFAULT);
    }

    public String getFormattedName()
    {
        return this.toString().charAt(0) + this.toString().substring(1).toLowerCase();
    }

    public boolean is(RaidDifficulty... others)
    {
        return List.of(others).contains(this);
    }
}
