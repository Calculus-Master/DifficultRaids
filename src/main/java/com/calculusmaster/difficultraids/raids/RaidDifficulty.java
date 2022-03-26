package com.calculusmaster.difficultraids.raids;

import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Random;
import java.util.function.Function;

public enum RaidDifficulty
{
    DEFAULT(
            10,
            0,
            List.of(),
            0,
            0,
            r -> 0,
            Items.LEATHER_HELMET
    ),

    HERO(
            15,
            3,
            List.of(ArmorMaterials.LEATHER, ArmorMaterials.CHAIN, ArmorMaterials.IRON),
            25,
            5,
            r -> r.nextInt(1, 3),
            Items.CHAINMAIL_HELMET
    ),
    LEGEND(
            25,
            5,
            List.of(ArmorMaterials.CHAIN, ArmorMaterials.IRON, ArmorMaterials.DIAMOND),
            45,
            15,
            r -> r.nextInt(1, 5),
            Items.IRON_HELMET
    ),
    MASTER(
            50,
            7,
            List.of(ArmorMaterials.IRON, ArmorMaterials.DIAMOND, ArmorMaterials.NETHERITE),
            50,
            33,
            r -> r.nextInt(3, 6),
            Items.DIAMOND_HELMET
    ),
    APOCALYPSE(
            90,
            10,
            List.of(ArmorMaterials.DIAMOND, ArmorMaterials.NETHERITE),
            80,
            50,
            r -> r.nextInt(4, 6),
            Items.NETHERITE_HELMET
    ),

    DEBUG(
            0,
            0,
            List.of(),
            0,
            0,
            r -> 0,
            Items.LEATHER_HELMET
    );

    public final int reinforcementChance;
    public final int totalLootDrops;
    public final List<ArmorMaterials> armorMaterials;
    public final int armorChance;
    public final int protectionChance;
    public final Function<Random, Integer> protectionLevelFunction;
    public final Item zombieSpawnHelmet;

    RaidDifficulty(int reinforcementChance,
                   int totalLootDrops,
                   List<ArmorMaterials> armorMaterials, int armorChance,
                   int protectionChance, Function<Random, Integer> protectionLevelFunction,
                   Item zombieSpawnHelmet)
    {
        this.reinforcementChance = reinforcementChance;
        this.totalLootDrops = totalLootDrops;
        this.armorMaterials = armorMaterials;
        this.armorChance = armorChance;
        this.protectionChance = protectionChance;
        this.protectionLevelFunction = protectionLevelFunction;
        this.zombieSpawnHelmet = zombieSpawnHelmet;
    }

    public String getFormattedName()
    {
        return this.toString().charAt(0) + this.toString().substring(1).toLowerCase();
    }
}
