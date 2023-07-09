package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
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

    public RaidDifficultyConfig config()
    {
        return switch(this)
        {
            case HERO -> DifficultRaidsConfig.HERO;
            case LEGEND -> DifficultRaidsConfig.LEGEND;
            case MASTER -> DifficultRaidsConfig.MASTER;
            case GRANDMASTER -> DifficultRaidsConfig.GRANDMASTER;
            default -> DifficultRaidsConfig.DEFAULT;
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
