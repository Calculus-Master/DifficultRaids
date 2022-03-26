package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

public class DifficultRaidsConfig
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static ForgeConfigSpec.EnumValue<RaidDifficulty> RAID_DIFFICULTY;
    public static ForgeConfigSpec.BooleanValue RAID_LOSS_APOCALYPSE_SHOULD_WITHER_SPAWN;
    public static ForgeConfigSpec.BooleanValue RAID_PREVENT_SUNLIGHT_BURNING_HELMETS;
    public static ForgeConfigSpec.IntValue RAID_CREEPER_INVIS_CHANCE_MASTER;

    public static void register()
    {
        //Server Configs
        ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

        RAID_DIFFICULTY = SERVER
                .comment("""
                        Change the overall difficulty of Raids.
                        The difficulty of a Raid will depend on both this value and the game difficulty (EASY, NORMAL, HARD).
                        DEFAULT difficulty removes all of this mod's changes to raids, and uses the vanilla Minecraft Raid settings.
                        HERO is the easiest difficulty added by this mod (but still harder than DEFAULT).
                        APOCALYPSE difficulty ignores the game difficulty. WARNING: APOCALYPSE difficulty spawns massive amounts of mobs, potentially generating vast amounts of lag. Choose at your own risk!
                        """)
                .defineEnum("raidDifficulty", RaidDifficulty.HERO, EnumGetMethod.NAME_IGNORECASE);

        RAID_LOSS_APOCALYPSE_SHOULD_WITHER_SPAWN = SERVER
                .comment("If true, losing a Raid on Apocalypse difficulty will spawn a Wither at the village center. Default: true")
                .define("raidLoss_shouldWitherSpawn", true);

        RAID_PREVENT_SUNLIGHT_BURNING_HELMETS = SERVER
                .comment("If true, any Zombies or Skeletons that spawn as Raid Reinforcements will automatically have a helmet equipped to prevent them from burning in daylight.")
                .define("raidPreventBurningHelmets", true);

        RAID_CREEPER_INVIS_CHANCE_MASTER = SERVER
                .comment("The chance that a Creeper will be invisible for a few seconds when summoned as part of Raid Reinforcements on Master and Apocalypse difficulty raids. This chance will be increased by 5% on Apocalypse difficulty. Set to 0 to disable this feature.")
                .defineInRange("raidCreeperInvisChance", 15, 0, 100);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());

        LOGGER.info(
                "Server Config Successfully Loaded!\n" +
                "Raid Difficulty: {%s}\n".formatted(RAID_DIFFICULTY.get()) +
                "Wither Spawn on Raid Loss at Apocalypse Difficulty: {%s}".formatted(RAID_LOSS_APOCALYPSE_SHOULD_WITHER_SPAWN)
        );
    }
}
