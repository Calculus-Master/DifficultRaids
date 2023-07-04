package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DifficultRaidsConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static Map<String, ForgeConfigSpec.BooleanValue> ENABLED_RAIDERS = new HashMap<>();

    public static RaidDifficultyConfig DEFAULT_CONFIG, HERO_CONFIG, LEGEND_CONFIG, MASTER_CONFIG, GRANDMASTER_CONFIG;

    public static void register()
    {
        //Server Configs
        ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

        //Disable mobs

        SERVER.push("Illagers");

        ENABLED_RAIDERS.put(RaidEnemyRegistry.VINDICATOR, SERVER.comment("Determines if Vindicators will show up in Raids.").define("enableVindicators", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.EVOKER, SERVER.comment("Determines if Evokers will show up in Raids.").define("enableEvokers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.PILLAGER, SERVER.comment("Determines if Pillagers will show up in Raids.").define("enablePillagers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.WITCH, SERVER.comment("Determines if Witches will show up in Raids.").define("enableWitches", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.RAVAGER, SERVER.comment("Determines if Ravagers will show up in Raids.").define("enableRavagers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ILLUSIONER, SERVER.comment("Determines if Illusioners will show up in Raids.").define("enableIllusioners", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.WARRIOR, SERVER.comment("Determines if Warriors will show up in Raids.").define("enableWarriors", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.DART, SERVER.comment("Determines if Darts will show up in Raids.").define("enableDarts", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.CONDUCTOR, SERVER.comment("Determines if Conductors will show up in Raids.").define("enableConductors", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.NECROMANCER, SERVER.comment("Determines if Necromancers will show up in Raids.").define("enableNecromancers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.SHAMAN, SERVER.comment("Determines if Shamans will show up in Raids.").define("enableShamans", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.TANK, SERVER.comment("Determines if Tanks will show up in Raids.").define("enableTanks", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ASSASSIN, SERVER.comment("Determines if Assassins will show up in Raids.").define("enableAssassins", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.FROSTMAGE, SERVER.comment("Determines if Frostmages will show up in Raids.").define("enableFrostmages", true));

        ENABLED_RAIDERS.put(RaidEnemyRegistry.HUNTER, SERVER.comment("(IF HunterIllager IS INSTALLED) Determines if Hunters will show up in Raids.").define("enableHunters", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ENCHANTER, SERVER.comment("(IF EnchantWithMob IS INSTALLED) Determines if Enchanters will show up in Raids.").define("enableEnchanters", true));

        SERVER.pop();

        //Raid Difficulty specific

        for(RaidDifficulty raidDifficulty : RaidDifficulty.values())
        {
            RaidDifficultyConfig config = new RaidDifficultyConfig();
            ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

            BUILDER.comment("Adjust settings for " + raidDifficulty.getFormattedName() + " Difficulty Raids.");

            //Elites
            config.elitesEnabled = BUILDER
                    .comment("Toggle whether Elite Raiders will spawn in Raids or not.")
                    .define("elitesEnabled", true);

            //Reinforcement Chance
            int default_reinforcementChance = switch(raidDifficulty) {
                case HERO -> 15;
                case LEGEND -> 25;
                case MASTER -> 40;
                case GRANDMASTER -> 80;
                default -> 0;
            };

            config.reinforcementChance = BUILDER
                    .comment("Determines the chance that Reinforcements will spawn on any wave of a Raid.")
                    .defineInRange("reinforcementChance", default_reinforcementChance, 0, 100);

            //Mob-Based Config

            //Assassin
            BUILDER.comment("Change settings regarding the Assassin Illager Entity during Raids.").push("Assassin Illager");
            config.assassinConfig = new AssassinIllagerConfig();

                int default_assassin_sharpnessLevel = switch(raidDifficulty) {
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 5;
                    default -> 0;
                };

                config.assassinConfig.sharpnessLevel = BUILDER
                        .comment("Determines the level of Sharpness that Assassin Illager Swords will be enchanted with.")
                        .defineInRange("assassinSharpnessLevel", default_assassin_sharpnessLevel, 0, 10);

            BUILDER.pop();

            //Dart
            BUILDER.comment("Change settings regarding the Dart Illager Entity during Raids.").push("Dart Illager");
            config.dartConfig = new DartIllagerConfig();

                int default_dart_sharpnessLevel = switch(raidDifficulty) {
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case GRANDMASTER -> 5;
                    default -> 0;
                };

                config.dartConfig.sharpnessLevel = BUILDER
                        .comment("Determines the level of Sharpness that Dart Illager Swords will be enchanted with.")
                        .defineInRange("dartSharpnessLevel", default_dart_sharpnessLevel, 0, 10);

            BUILDER.pop();

            //Electro
            BUILDER.comment("Change settings regarding the Electro Illager Entity during Raids.").push("Electro Illager");
            config.electroConfig = new ElectroIllagerConfig();

                double default_electro_concentratedBoltDamage = switch(raidDifficulty) {
                    case HERO -> 19.0F;
                    case LEGEND -> 20.0F;
                    case MASTER -> 24.0F;
                    case GRANDMASTER -> 30.0F;
                    default -> 18.0F;
                };

                config.electroConfig.concentratedBoltDamage = BUILDER
                        .comment("The damage that the Electro Illager's Concentrated Bolt spell deals.")
                        .defineInRange("electroConcentratedBoltDamage", default_electro_concentratedBoltDamage, 0.1, Double.MAX_VALUE);

                int default_electro_genericLightningStrikeCount = switch(raidDifficulty) {
                    case HERO -> 4;
                    case LEGEND -> 6;
                    case MASTER -> 8;
                    case GRANDMASTER -> 10;
                    default -> 3;
                };

                config.electroConfig.genericLightningStrikeCount = BUILDER
                        .comment("Determines the number of lightning strikes generated by the Electro Illager's Generic Lightning attack.")
                        .defineInRange("electroGenericLightningStrikeCount", default_electro_genericLightningStrikeCount, 1, Integer.MAX_VALUE);

                boolean default_electro_extraRingBolts = raidDifficulty.is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER);

                config.electroConfig.extraRingBolts = BUILDER
                        .comment("If true, Electro Illagers will summon a second set of Lightning Bolts during their Ring Lightning attack.")
                        .define("electroExtraRingBolts", default_electro_extraRingBolts);

            BUILDER.pop();

            //Frost
            BUILDER.comment("Change settings regarding the Frost Illager Entity during Raids.").push("Frost Illager");
            config.frostConfig = new FrostIllagerConfig();

                float default_frost_snowballBlastDamage = switch(raidDifficulty) {
                    case HERO -> 3.5F;
                    case LEGEND -> 5.5F;
                    case MASTER -> 7.0F;
                    case GRANDMASTER -> 10.0F;
                    default -> 1.5F;
                };

                config.frostConfig.snowballBlastDamage = BUILDER
                        .comment("Determines the damage of individual snowballs in the Frost Illager's Snowball Blast attack")
                        .defineInRange("frostSnowballBlastDamage", default_frost_snowballBlastDamage, 0.1, Double.MAX_VALUE);

                int default_frost_barrageDuration = switch(raidDifficulty) {
                    case HERO -> 20 * 7;
                    case LEGEND -> 20 * 12;
                    case MASTER -> 20 * 15;
                    case GRANDMASTER -> 20 * 20;
                    default -> 20 * 3;
                };

                config.frostConfig.barrageDuration = BUILDER
                        .comment("Determines the duration of the Frost Illager's Barrage attack, in ticks (20 ticks = 1 second).")
                        .defineInRange("frostBarrageDuration", default_frost_barrageDuration, 1, Integer.MAX_VALUE);

                int default_frost_freezeDuration = switch(raidDifficulty) {
                    case HERO -> 20 * 6;
                    case LEGEND -> 20 * 8;
                    case MASTER -> 20 * 10;
                    case GRANDMASTER -> 20 * 15;
                    default -> 20 * 4;
                };

                config.frostConfig.freezeDuration = BUILDER
                        .comment("Determines the duration of the Frost Illager's Freeze attack, in ticks (20 ticks = 1 second). Mining Fatigue duration will be 1/4 of this value.")
                        .defineInRange("frostFreezeDuration", default_frost_freezeDuration, 1, Integer.MAX_VALUE);

            BUILDER.pop();

            //Necromancer
            BUILDER.comment("Change settings regarding the Necromancer Illager Entity during Raids.").push("Necromancer Illager");
            config.necromancerConfig = new NecromancerIllagerConfig();

                int default_necromancer_minionSummonCount = switch(raidDifficulty) {
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 5;
                    case GRANDMASTER -> 10;
                    default -> 0;
                };

                config.necromancerConfig.minionSummonCount = BUILDER
                        .comment("The number of mobs spawned when the Necromancer uses its Summon Minions attack.")
                        .defineInRange("necromancerMinionSummonCount", default_necromancer_minionSummonCount, 0, Integer.MAX_VALUE);

                int default_necromancer_minionProtectionLevel = switch(raidDifficulty) {
                    case HERO -> 1;
                    case LEGEND -> 2;
                    case MASTER -> 3;
                    case GRANDMASTER -> 4;
                    default -> 0;
                };

                config.necromancerConfig.minionProtectionLevel = BUILDER
                        .comment("The maximum level of Protection a Summoned Minion's armor will be enchanted with. The actual level of Protection will randomly chosen between 1 and this value. 0 removes this enchantment from Minion armor.")
                        .defineInRange("minionProtectionLevel", default_necromancer_minionProtectionLevel, 0, 10);

                int default_necromancer_hordeSize = switch(raidDifficulty) {
                    case HERO -> 10;
                    case LEGEND -> 15;
                    case MASTER -> 20;
                    case GRANDMASTER -> 30;
                    default -> 5;
                };

                config.necromancerConfig.hordeSize = BUILDER
                        .comment("The number of mobs spawned when the Necromancer uses its Summon Horde attack.")
                        .defineInRange("necromancerHordeSize", default_necromancer_hordeSize, 0, Integer.MAX_VALUE);

                int default_necromancer_hordeLifetime = switch(raidDifficulty) {
                    case HERO -> 20 * 30;
                    case LEGEND -> 20 * 60;
                    case MASTER -> 20 * 90;
                    case GRANDMASTER -> 20 * 180;
                    default -> 20 * 15;
                };

                config.necromancerConfig.hordeLifetime = BUILDER
                        .comment("How long Necromancer Summoned Hordes will be alive before dying automatically, in ticks (20 ticks = 1 second).")
                        .defineInRange("necromancerHordeLifetime", default_necromancer_hordeLifetime, 0, Integer.MAX_VALUE);

            BUILDER.pop();

            //Shaman
            BUILDER.comment("Change settings regarding the Shaman Illager Entity during Raids.").push("Shaman Illager");
            config.shamanConfig = new ShamanIllagerConfig();

                int default_shaman_debuffAmount = switch(raidDifficulty) {
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case GRANDMASTER -> 5;
                    default -> 1;
                };

                config.shamanConfig.debuffAmount = BUILDER
                        .comment("The amount of debuffs the Shaman will inflict during its primary attack.")
                        .defineInRange("shamanDebuffAmount", default_shaman_debuffAmount, 0, 6);

                int default_shaman_nauseaDuration = switch(raidDifficulty) {
                    case HERO -> 40;
                    case LEGEND -> 60;
                    case MASTER -> 80;
                    case GRANDMASTER -> 160;
                    default -> 20;
                };

                config.shamanConfig.nauseaDuration = BUILDER
                        .comment("Duration of the Nausea Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanNauseaDuration", default_shaman_nauseaDuration, 0, Integer.MAX_VALUE);

                int default_shaman_slownessDuration = switch(raidDifficulty) {
                    case HERO, LEGEND -> 100;
                    case MASTER -> 160;
                    case GRANDMASTER -> 240;
                    default -> 60;
                };

                config.shamanConfig.slownessDuration = BUILDER
                        .comment("Duration of the Slowness Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanSlownessDuration", default_shaman_slownessDuration, 0, Integer.MAX_VALUE);

                int default_shaman_slownessAmplifier = switch(raidDifficulty) {
                    case LEGEND, MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 1;
                };

                config.shamanConfig.slownessAmplifier = BUILDER
                        .comment("Amplifier of the Slowness Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanSlownessAmplifier", default_shaman_slownessAmplifier, 0, Integer.MAX_VALUE);

                int default_shaman_MiningFatigueDuration = switch(raidDifficulty) {
                    case HERO -> 100;
                    case LEGEND, MASTER -> 160;
                    case GRANDMASTER -> 280;
                    default -> 40;
                };

                config.shamanConfig.miningFatigueDuration = BUILDER
                        .comment("Duration of the Mining Fatigue Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanMiningFatigueDuration", default_shaman_MiningFatigueDuration, 0, Integer.MAX_VALUE);

                int default_shaman_miningFatigueAmplifier = switch(raidDifficulty) {
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 1;
                };

                config.shamanConfig.miningFatigueAmplifier = BUILDER
                        .comment("Amplifier of the Mining Fatigue Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanMiningFatigueAmplifier", default_shaman_miningFatigueAmplifier, 0, Integer.MAX_VALUE);

                int default_shaman_poisonDuration = switch(raidDifficulty) {
                    case HERO, LEGEND, MASTER -> 100;
                    case GRANDMASTER -> 200;
                    default -> 50;
                };

                config.shamanConfig.poisonDuration = BUILDER
                        .comment("Duration of the Poison Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanPoisonDuration", default_shaman_poisonDuration, 0, Integer.MAX_VALUE);

                int default_shaman_poisonAmplifier = switch(raidDifficulty) {
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case GRANDMASTER -> 5;
                    default -> 1;
                };

                config.shamanConfig.poisonAmplifier = BUILDER
                        .comment("Amplifier of the Poison Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanPoisonAmplifier", default_shaman_poisonAmplifier, 0, Integer.MAX_VALUE);

                int default_shaman_levitationDuration = switch(raidDifficulty) {
                    case HERO -> 40;
                    case LEGEND -> 80;
                    case MASTER -> 100;
                    case GRANDMASTER -> 160;
                    default -> 20;
                };

                config.shamanConfig.levitationDuration = BUILDER
                        .comment("Duration of the Levitation Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanLevitationDuration", default_shaman_levitationDuration, 0, Integer.MAX_VALUE);

                int default_shaman_weaknessDuration = switch(raidDifficulty) {
                    case HERO -> 60;
                    case LEGEND -> 100;
                    case MASTER -> 120;
                    case GRANDMASTER -> 200;
                    default -> 20;
                };

                config.shamanConfig.weaknessDuration = BUILDER
                        .comment("Weakness of the Weakness Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanWeaknessDuration", default_shaman_weaknessDuration, 0, Integer.MAX_VALUE);

                int default_shaman_weaknessAmplifier = switch(raidDifficulty) {
                    case HERO, LEGEND -> 2;
                    case MASTER -> 3;
                    case GRANDMASTER -> 4;
                    default -> 1;
                };

                config.shamanConfig.weaknessAmplifier = BUILDER
                        .comment("Amplifier of the Weakness Effect inflicted by the Shaman, in ticks (20 ticks = 1 second).")
                        .defineInRange("shamanWeaknessAmplifier", default_shaman_weaknessAmplifier, 0, Integer.MAX_VALUE);

                float default_shaman_buffRadius = switch(raidDifficulty) {
                    case HERO -> 5.0F;
                    case LEGEND -> 8.0F;
                    case MASTER -> 12.0F;
                    case GRANDMASTER -> 20.0F;
                    default -> 3.0F;
                };

                config.shamanConfig.buffRadius = BUILDER
                        .comment("The maximum radius of the Shaman's boost attacks. All raiders within this radius of the Shaman will receive boosts.")
                        .defineInRange("shamanBuffRadius", default_shaman_buffRadius, 0, Double.MAX_VALUE);

                int default_shaman_allyResistanceDuration = switch(raidDifficulty) {
                    case HERO -> 80;
                    case LEGEND, MASTER -> 160;
                    case GRANDMASTER -> 360;
                    default -> 40;
                };

                config.shamanConfig.allyResistanceDuration = BUILDER
                        .comment("The duration of the Resistance Effect that the Shaman gives its allies.")
                        .defineInRange("shamanAllyResistanceDuration", default_shaman_allyResistanceDuration, 0, Integer.MAX_VALUE);

                int default_shaman_allyResistanceAmplifier = switch(raidDifficulty) {
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 1;
                };

                config.shamanConfig.allyResistanceAmplifier = BUILDER
                        .comment("The amplifier of the Resistance Effect that the Shaman gives its allies.")
                        .defineInRange("shamanAllyResistanceAmplifier", default_shaman_allyResistanceAmplifier, 0, Integer.MAX_VALUE);

                int default_shaman_allyStrengthDuration = switch(raidDifficulty) {
                    case HERO, MASTER -> 200;
                    case LEGEND, GRANDMASTER -> 480;
                    default -> 120;
                };

                config.shamanConfig.allyStrengthDuration = BUILDER
                        .comment("The duration of the Strength Effect that the Shaman gives its allies.")
                        .defineInRange("shamanAllyStrengthDuration", default_shaman_allyStrengthDuration, 0, Integer.MAX_VALUE);

                int default_shaman_allyStrengthAmplifier = switch(raidDifficulty) {
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 1;
                };

                config.shamanConfig.allyStrengthAmplifier = BUILDER
                        .comment("The amplifier of the Strength Effect that the Shaman gives its allies.")
                        .defineInRange("shamanAllyStrengthAmplifier", default_shaman_allyStrengthAmplifier, 0, Integer.MAX_VALUE);

            BUILDER.pop();

            //Tank TODO - No properties
            BUILDER.comment("Change settings regarding the Tank Illager Entity during Raids.").push("Tank Illager");
//            config.tankConfig = new TankIllagerConfig();
//
//                int default_tank_protectionLevel = switch(raidDifficulty) {
//                    case HERO -> 2;
//                    case LEGEND -> 3;
//                    case MASTER -> 4;
//                    case GRANDMASTER -> 5;
//                    default -> 1;
//                };
//
//                config.tankConfig.protectionLevel = BUILDER
//                        .comment("Determines the level of Protection that Tank Illager armor will be enchanted with. 0 to disable.")
//                        .defineInRange("tankProtectionLevel", default_tank_protectionLevel, 0, 10);
//
//                int default_tank_thornsLevel = switch(raidDifficulty) {
//                    case LEGEND -> 1;
//                    case MASTER -> 2;
//                    case GRANDMASTER -> 3;
//                    default -> 0;
//                };
//
//                config.tankConfig.thornsLevel = BUILDER
//                        .comment("Determines the level of Thorns that Tank Illager armor will be enchanted with. 0 to disable.")
//                        .defineInRange("tankThornsLevel", default_tank_thornsLevel, 0, 10);

            BUILDER.pop();

            //Warrior
            BUILDER.comment("Change settings regarding the Warrior Illager Entity during Raids.").push("Warrior Illager");
            config.warriorConfig = new WarriorIllagerConfig();

                List<String> default_warrior_possibleSwords = switch(raidDifficulty) {
                    case HERO -> List.of("STONE", "GOLD", "IRON");
                    case LEGEND -> List.of("IRON", "DIAMOND");
                    case MASTER -> List.of("IRON", "DIAMOND", "NETHERITE");
                    case GRANDMASTER -> List.of("NETHERITE");
                    default -> List.of("STONE");
                };

                config.warriorConfig.possibleSwords = BUILDER
                        .comment("Determines the possible swords that Warrior Illagers can have equipped during Raids. Valid Swords: STONE, GOLD, IRON, DIAMOND, NETHERITE (Case-Sensitive!).")
                        .defineList("warriorPossibleSwords", default_warrior_possibleSwords, o -> List.of("STONE", "GOLD", "IRON", "DIAMOND", "NETHERITE").contains(o.toString()));

                int default_warrior_sharpnessChance = switch(raidDifficulty) {
                    case HERO -> 20;
                    case LEGEND -> 35;
                    case MASTER -> 45;
                    case GRANDMASTER -> 90;
                    default -> 0;
                };

                config.warriorConfig.sharpnessChance = BUILDER
                        .comment("Determines the chance that Warrior Illager swords will be enchanted with some level of Sharpness.")
                        .defineInRange("warriorSharpnessChance", default_warrior_sharpnessChance, 0, 100);

                Tuple<Integer, Integer> default_warrior_sharpnessLevel = switch(raidDifficulty) {
                    case HERO -> new Tuple<>(1, 3);
                    case LEGEND -> new Tuple<>(2, 4);
                    case MASTER -> new Tuple<>(3, 5);
                    case GRANDMASTER -> new Tuple<>(4, 6);
                    default -> new Tuple<>(0, 0);
                };

                config.warriorConfig.minSharpnessLevel = BUILDER
                        .comment("The minimum level of Sharpness that Warrior Illager swords will be enchanted with.")
                        .defineInRange("warriorMinSharpnessLevel", default_warrior_sharpnessLevel.getA(), 1, 10);

                config.warriorConfig.maxSharpnessLevel = BUILDER
                        .comment("The maximum level of Sharpness that Warrior Illager swords will be enchanted with.")
                        .defineInRange("warriorMaxSharpnessLevel", default_warrior_sharpnessLevel.getB(), 1, 10);

                int default_warrior_fireAspectChance = switch(raidDifficulty) {
                    case HERO -> 5;
                    case LEGEND -> 10;
                    case MASTER -> 15;
                    case GRANDMASTER -> 50;
                    default -> 0;
                };

                config.warriorConfig.fireAspectChance = BUILDER
                        .comment("Determines the chance that Warrior Illager swords will be enchanted with some level of Fire Aspect.")
                        .defineInRange("warriorFireAspectChance", default_warrior_fireAspectChance, 0, 100);

                int default_warrior_fireAspectLevel = switch(raidDifficulty) {
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 0;
                };

                config.warriorConfig.fireAspectLevel = BUILDER
                        .comment("The level of Fire Aspect that Warrior Illager swords will be enchanted with.")
                        .defineInRange("warriorFireAspectLevel", default_warrior_fireAspectLevel, 1, 10);

                int default_warrior_knockbackChance = switch(raidDifficulty) {
                    case HERO -> 10;
                    case LEGEND -> 15;
                    case MASTER -> 20;
                    case GRANDMASTER -> 90;
                    default -> 0;
                };

                config.warriorConfig.knockbackChance = BUILDER
                        .comment("Determines the chance that Warrior Illager swords will be enchanted with some level of Knockback.")
                        .defineInRange("warriorKnockbackChance", default_warrior_knockbackChance, 0, 100);

                int default_warrior_knockbackLevel = switch(raidDifficulty) {
                    case HERO -> 1;
                    case LEGEND, MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 0;
                };

                config.warriorConfig.knockbackLevel = BUILDER
                        .comment("The level of Knockback that Warrior Illager swords will be enchanted with.")
                        .defineInRange("warriorKnockbackLevel", default_warrior_knockbackLevel, 1, 10);

            BUILDER.pop();

            //Assign the relevant objects
            switch(raidDifficulty)
            {
                case DEFAULT -> DEFAULT_CONFIG = config;
                case HERO -> HERO_CONFIG = config;
                case LEGEND -> LEGEND_CONFIG = config;
                case MASTER -> MASTER_CONFIG = config;
                case GRANDMASTER -> GRANDMASTER_CONFIG = config;
            }

            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, BUILDER.build(), DifficultRaids.MODID + "/difficulty-" + raidDifficulty.toString().toLowerCase() + ".toml");
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build(), DifficultRaids.MODID + "/general.toml");
    }

    public static class RaidDifficultyConfig
    {
        private ForgeConfigSpec.BooleanValue elitesEnabled;
        public boolean areElitesEnabled()
        {
            return this.elitesEnabled.get();
        }

        private ForgeConfigSpec.IntValue reinforcementChance;
        public int reinforcementChance()
        {
            return this.reinforcementChance.get();
        }

        //Mob Configs
        private AssassinIllagerConfig assassinConfig;
        public AssassinIllagerConfig assassin() { return this.assassinConfig; }

        private DartIllagerConfig dartConfig;
        public DartIllagerConfig dart() { return this.dartConfig; }

        private ElectroIllagerConfig electroConfig;
        public ElectroIllagerConfig electro() { return this.electroConfig; }

        private FrostIllagerConfig frostConfig;
        public FrostIllagerConfig frost() { return this.frostConfig; }

        private NecromancerIllagerConfig necromancerConfig;
        public NecromancerIllagerConfig necromancer() { return this.necromancerConfig; }

        private ShamanIllagerConfig shamanConfig;
        public ShamanIllagerConfig shaman() { return this.shamanConfig; }

        private WarriorIllagerConfig warriorConfig;
        public WarriorIllagerConfig warrior() { return this.warriorConfig; }
    }

    public static class AssassinIllagerConfig
    {
        private ForgeConfigSpec.IntValue sharpnessLevel;
        public int sharpnessLevel()
        {
            return this.sharpnessLevel.get();
        }
    }

    public static class DartIllagerConfig
    {
        private ForgeConfigSpec.IntValue sharpnessLevel;
        public int sharpnessLevel()
        {
            return this.sharpnessLevel.get();
        }
    }

    public static class ElectroIllagerConfig
    {
        private ForgeConfigSpec.DoubleValue concentratedBoltDamage;
        public float concentratedBoltDamage()
        {
            return this.concentratedBoltDamage.get().floatValue();
        }

        private ForgeConfigSpec.IntValue genericLightningStrikeCount;
        public int genericLightningStrikeCount()
        {
            return this.genericLightningStrikeCount.get();
        }

        private ForgeConfigSpec.BooleanValue extraRingBolts;
        public boolean extraRingBolts()
        {
            return this.extraRingBolts.get();
        }
    }

    public static class FrostIllagerConfig
    {
        private ForgeConfigSpec.DoubleValue snowballBlastDamage;
        public float snowballBlastDamage()
        {
            return this.snowballBlastDamage.get().floatValue();
        }

        private ForgeConfigSpec.IntValue barrageDuration;
        public int barrageDuration()
        {
            return this.barrageDuration.get();
        }

        private ForgeConfigSpec.IntValue freezeDuration;
        public int freezeDuration()
        {
            return this.freezeDuration.get();
        }
    }

    public static class NecromancerIllagerConfig
    {
        public ForgeConfigSpec.IntValue minionSummonCount;
        public int minionSummonCount()
        {
            return this.minionSummonCount.get();
        }

        private ForgeConfigSpec.IntValue minionProtectionLevel;
        public int minionProtectionLevel()
        {
            return this.minionProtectionLevel.get();
        }

        private ForgeConfigSpec.IntValue hordeSize;
        public int hordeSize()
        {
            return this.hordeSize.get();
        }

        private ForgeConfigSpec.IntValue hordeLifetime;
        public int hordeLifetime()
        {
            return this.hordeLifetime.get();
        }
    }

    public static class ShamanIllagerConfig
    {
        private ForgeConfigSpec.IntValue debuffAmount;
        public int debuffAmount()
        {
            return this.debuffAmount.get();
        }

        private ForgeConfigSpec.IntValue nauseaDuration;
        public int nauseaDuration()
        {
            return this.nauseaDuration.get();
        }

        private ForgeConfigSpec.IntValue slownessDuration;
        public int slownessDuration()
        {
            return this.slownessDuration.get();
        }

        private ForgeConfigSpec.IntValue slownessAmplifier;
        public int slownessAmplifier()
        {
            return this.slownessAmplifier.get();
        }

        private ForgeConfigSpec.IntValue miningFatigueDuration;
        public int miningFatigueDuration()
        {
            return this.miningFatigueDuration.get();
        }

        private ForgeConfigSpec.IntValue miningFatigueAmplifier;
        public int miningFatigueAmplifier()
        {
            return this.miningFatigueAmplifier.get();
        }

        private ForgeConfigSpec.IntValue poisonDuration;
        public int poisonDuration()
        {
            return this.poisonDuration.get();
        }

        private ForgeConfigSpec.IntValue poisonAmplifier;
        public int poisonAmplifier()
        {
            return this.poisonAmplifier.get();
        }

        private ForgeConfigSpec.IntValue levitationDuration;
        public int levitationDuration()
        {
            return this.levitationDuration.get();
        }

        private ForgeConfigSpec.IntValue weaknessDuration;
        public int weaknessDuration()
        {
            return this.weaknessDuration.get();
        }

        private ForgeConfigSpec.IntValue weaknessAmplifier;
        public int weaknessAmplifier()
        {
            return this.weaknessAmplifier.get();
        }

        private ForgeConfigSpec.DoubleValue buffRadius;
        public float buffRadius()
        {
            return this.buffRadius.get().floatValue();
        }

        private ForgeConfigSpec.IntValue allyResistanceDuration;
        public int allyResistanceDuration()
        {
            return this.allyResistanceDuration.get();
        }

        private ForgeConfigSpec.IntValue allyResistanceAmplifier;
        public int allyResistanceAmplifier()
        {
            return this.allyResistanceAmplifier.get();
        }

        private ForgeConfigSpec.IntValue allyStrengthDuration;
        public int allyStrengthDuration()
        {
            return this.allyStrengthDuration.get();
        }

        private ForgeConfigSpec.IntValue allyStrengthAmplifier;
        public int allyStrengthAmplifier()
        {
            return this.allyStrengthAmplifier.get();
        }
    }

    public static class WarriorIllagerConfig
    {
        private ForgeConfigSpec.ConfigValue<List<? extends String>> possibleSwords;
        public List<Item> possibleSwords()
        {
            return this.possibleSwords.get().stream().map(String::toString).map(s -> switch(s) {
                case "STONE" -> Items.STONE_SWORD;
                case "GOLD" -> Items.GOLDEN_SWORD;
                case "IRON" -> Items.IRON_SWORD;
                case "DIAMOND" -> Items.DIAMOND_SWORD;
                case "NETHERITE" -> Items.NETHERITE_SWORD;
                default -> null;
            }).filter(Objects::nonNull).toList();
        }

        private ForgeConfigSpec.IntValue sharpnessChance;
        public int sharpnessChance()
        {
            return this.sharpnessChance.get();
        }

        private ForgeConfigSpec.IntValue minSharpnessLevel;
        private ForgeConfigSpec.IntValue maxSharpnessLevel;
        public Tuple<Integer, Integer> sharpnessLevel()
        {
            return new Tuple<>(this.minSharpnessLevel.get(), this.maxSharpnessLevel.get());
        }

        private ForgeConfigSpec.IntValue fireAspectChance;
        public int fireAspectChance()
        {
            return this.fireAspectChance.get();
        }

        private ForgeConfigSpec.IntValue fireAspectLevel;
        public int fireAspectLevel()
        {
            return this.fireAspectLevel.get();
        }

        private ForgeConfigSpec.IntValue knockbackChance;
        public int knockbackChance()
        {
            return this.knockbackChance.get();
        }

        private ForgeConfigSpec.IntValue knockbackLevel;
        public int knockbackLevel()
        {
            return this.knockbackLevel.get();
        }
    }
}
