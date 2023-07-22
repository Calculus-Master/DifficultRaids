package com.calculusmaster.difficultraids.raids;

public enum RaiderCategory
{
    STANDARD ("Classic Raider units."),
    ADVANCED ("More powerful Raider units."),
    MYSTICAL ("Basic Raider units with magical abilities."),
    ADVANCED_MYSTICAL ("Powerful Raider units with magical abilities."),
    CHAMPION ("Extra-powerful Raider units."),
    ELITE ("The most powerful Raider units.")
    ;

    private final String description; public String getDescription() { return this.description; }

    RaiderCategory(String description)
    {
        this.description = description;
    }


}
