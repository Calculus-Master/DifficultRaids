package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;

import java.util.List;

public enum RaidDifficulty
{
    DEFAULT,
    HERO,
    LEGEND,
    MASTER,
    GRANDMASTER;

    public static RaidDifficulty get(int badOmenLevel)
    {
        return switch(badOmenLevel) {
            case 2 -> HERO;
            case 3 -> LEGEND;
            case 4 -> MASTER;
            case 5 -> GRANDMASTER;
            default -> DEFAULT;
        };
    }

    public DifficultRaidsConfig.RaidDifficultyConfig config()
    {
        return switch(this) {
            case HERO -> DifficultRaidsConfig.HERO_CONFIG;
            case LEGEND -> DifficultRaidsConfig.LEGEND_CONFIG;
            case MASTER -> DifficultRaidsConfig.MASTER_CONFIG;
            case GRANDMASTER -> DifficultRaidsConfig.APOCALYPSE_CONFIG;
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
