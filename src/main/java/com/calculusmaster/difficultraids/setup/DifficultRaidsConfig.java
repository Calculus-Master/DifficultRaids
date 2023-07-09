package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.config.RaiderConfigs;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DifficultRaidsConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    //Common Config Values
    public static Map<String, ForgeConfigSpec.BooleanValue> ENABLED_RAIDERS = new HashMap<>();
    public static ForgeConfigSpec.IntValue HIGHLIGHT_THRESHOLD;
    public static ForgeConfigSpec.DoubleValue BELL_SEARCH_RADIUS;

    public static RaidDifficultyConfig DEFAULT, HERO, LEGEND, MASTER, GRANDMASTER;

    public static void initializeConfigs()
    {
        DEFAULT.init();
        HERO.init();
        LEGEND.init();
        MASTER.init();
        GRANDMASTER.init();
    }

    public static void register()
    {
        //General Config
        ForgeConfigSpec.Builder GENERAL = new ForgeConfigSpec.Builder();

        HIGHLIGHT_THRESHOLD = GENERAL
                .comment("If there are fewer raiders alive than this threshold, they will be highlighted permanently. Set to 0 to disable highlighting.")
                .defineInRange("highlightThreshold", 3, 0, Integer.MAX_VALUE);

        BELL_SEARCH_RADIUS = GENERAL
                .comment("Search radius of the Bell when hit.")
                .comment("WARNING: This can cause a decent bit of lag at high radius values because it will search more and more blocks around the village center.")
                .comment("Vanilla Minecraft uses a radius of 48 blocks.")
                .defineInRange("bellSearchRadius", 256.0, 0., Integer.MAX_VALUE);

        GENERAL.comment("Customize which Raiders will show up in Raids. By default, all raiders are enabled.")
                .push("Enabled Raiders");

        ENABLED_RAIDERS.put(RaidEnemyRegistry.VINDICATOR, GENERAL.define("enableVindicators", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.EVOKER, GENERAL.define("enableEvokers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.PILLAGER, GENERAL.define("enablePillagers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.WITCH, GENERAL.define("enableWitches", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.RAVAGER, GENERAL.define("enableRavagers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ILLUSIONER, GENERAL.define("enableIllusioners", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.WARRIOR, GENERAL.define("enableWarriors", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.DART, GENERAL.define("enableDarts", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.CONDUCTOR, GENERAL.define("enableConductors", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.NECROMANCER, GENERAL.define("enableNecromancers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.SHAMAN, GENERAL.define("enableShamans", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.TANK, GENERAL.define("enableTanks", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ASSASSIN, GENERAL.define("enableAssassins", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.FROSTMAGE, GENERAL.define("enableFrostmages", true));

        ENABLED_RAIDERS.put(RaidEnemyRegistry.HUNTER, GENERAL.comment("If HunterIllager is installed.").define("enableHunters", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ENCHANTER, GENERAL.comment("If EnchantWithMob is installed.").define("enableEnchanters", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.ARCHER, GENERAL.comment("If It Takes a Pillage is installed.").define("enableArchers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.SKIRMISHER, GENERAL.comment("If It Takes a Pillage is installed.").define("enableSkirmishers", true));
        ENABLED_RAIDERS.put(RaidEnemyRegistry.LEGIONER, GENERAL.comment("If It Takes a Pillage is installed.").define("enableLegioners", true));

        GENERAL.pop();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, GENERAL.build(), DifficultRaids.MODID + "/general.toml");

        //Raid Difficulty Configs
        for(RaidDifficulty rd : RaidDifficulty.values())
        {
            RaidDifficultyConfig config = new RaidDifficultyConfig();
            ForgeConfigSpec.Builder spec = new ForgeConfigSpec.Builder();

            if(rd == RaidDifficulty.DEFAULT) spec.comment("Note: Changing values in this config will have no real impact on the game. Default Raids are Vanilla and do not feature anything from this mod. The config file exists for consistency within the code.\n");
            else spec.comment("Edit config values for " + rd.getFormattedName() + " Raids.\n");

            //General
            spec.push("General Settings");

            config.elitesEnabled = spec
                    .comment("Determines if Elite Raiders will show up in Raids.")
                    .define("elitesEnabled", true);

            config.playerHealthBoostAmount = spec
                    .comment("Extra health that Raiders will receive per additional player in the Raid. 0 to disable Raiders receiving extra health.")
                    .defineInRange("playerHealthBoostAmount", 2.0F, 0., Double.MAX_VALUE);

            spec.pop();

            //Vindicator
            spec.push("Vindicator Settings");
            config.vindicator = new RaiderConfigs.Vindicator(rd, spec);
            spec.pop();

            //Evoker
            spec.push("Evoker Settings");
            config.evoker = new RaiderConfigs.Evoker(rd, spec);
            spec.pop();

            //Pillager
            spec.push("Pillager Settings");
            config.pillager = new RaiderConfigs.Pillager(rd, spec);
            spec.pop();

            //Ravager
            spec.push("Ravager Settings");
            config.ravager = new RaiderConfigs.Ravager(rd, spec);
            spec.pop();

            //Warrior
            spec.push("Warrior Settings");
            config.warrior = new RaiderConfigs.Warrior(rd, spec);
            spec.pop();

            //Dart
            spec.push("Dart Settings");
            config.dart = new RaiderConfigs.Dart(rd, spec);
            spec.pop();

            //Conductor
            spec.push("Conductor Settings");
            config.conductor = new RaiderConfigs.Conductor(rd, spec);
            spec.pop();

            //Necromancer
            spec.push("Necromancer Settings");
            config.necromancer = new RaiderConfigs.Necromancer(rd, spec);
            spec.pop();

            //Shaman
            spec.push("Shaman Settings");
            config.shaman = new RaiderConfigs.Shaman(rd, spec);
            spec.pop();

            //Tank
            spec.push("Tank Settings");
            config.tank = new RaiderConfigs.Tank(rd, spec);
            spec.pop();

            //Assassin
            spec.push("Assassin Settings");
            config.assassin = new RaiderConfigs.Assassin(rd, spec);
            spec.pop();

            //Frostmage
            spec.push("Frostmage Settings");
            config.frostmage = new RaiderConfigs.Frostmage(rd, spec);
            spec.pop();

            spec.push("Elite Raiders");

            //Nuaos
            spec.push("Nuaos Settings");
            config.nuaos = new RaiderConfigs.Nuaos(rd, spec);
            spec.pop();

            //Xydrax
            spec.push("Xydrax Settings");
            config.xydrax = new RaiderConfigs.Xydrax(rd, spec);
            spec.pop();

            //Modur
            spec.push("Modur Settings");
            config.modur = new RaiderConfigs.Modur(rd, spec);
            spec.pop();

            //Voldon
            spec.push("Voldon Settings");
            config.voldon = new RaiderConfigs.Voldon(rd, spec);
            spec.pop();

            spec.pop();

            spec.push("Compatibility");

            //Hunter Illager
            spec.comment("REQUIRES 'HunterIllager'").push("Hunter Illager Settings");
            config.hunter = new RaiderConfigs.Hunter(rd, spec);
            spec.pop();

            //Archer
            spec.comment("REQUIRES 'It Takes a Pillage'").push("Archer Settings");
            config.archer = new RaiderConfigs.Archer(rd, spec);
            spec.pop();

            //Skirmisher
            spec.comment("REQUIRES 'It Takes a Pillage'").push("Skirmisher Settings");
            config.skirmisher = new RaiderConfigs.Skirmisher(rd, spec);
            spec.pop();

            //Legioner
            spec.comment("REQUIRES 'It Takes a Pillage'").push("Legioner Settings");
            config.legioner = new RaiderConfigs.Legioner(rd, spec);
            spec.pop();

            spec.pop();

            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, spec.build(), DifficultRaids.MODID + "/raid-" + rd.toString().toLowerCase() + ".toml");

            switch(rd)
            {
                case DEFAULT -> DEFAULT = config;
                case HERO -> HERO = config;
                case LEGEND -> LEGEND = config;
                case MASTER -> MASTER = config;
                case GRANDMASTER -> GRANDMASTER = config;
            }
        }
    }
}
