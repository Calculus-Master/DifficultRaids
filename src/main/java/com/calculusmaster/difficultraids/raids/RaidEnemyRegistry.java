package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.data.RaidEnemyManager;
import com.calculusmaster.difficultraids.data.RaiderEntriesHolder;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.util.Compat;
import com.infamous.dungeons_mobs.mod.ModEntityTypes;
import com.mojang.logging.LogUtils;
import com.teamabnormals.savage_and_ravage.core.registry.SREntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes.*;

public class RaidEnemyRegistry
{
    public static final Map<RaidDifficulty, RaidEnemyManager> DEFAULT_WAVES = new HashMap<>();
    public static final Map<RaidDifficulty, RaidEnemyManager> CURRENT_WAVES = new HashMap<>();
    public static final Set<String> REGISTERED_RAIDER_TYPES = new HashSet<>();

    //Minecraft
    public static final String VINDICATOR = "VINDICATOR";
    public static final String EVOKER = "EVOKER";
    public static final String PILLAGER = "PILLAGER";
    public static final String WITCH = "WITCH";
    public static final String RAVAGER = "RAVAGER";

    //Difficult Raids
    public static final String ILLUSIONER = "ILLUSIONER";
    public static final String WARRIOR = "WARRIOR_ILLAGER";
    public static final String DART = "DART_ILLAGER";
    public static final String CONDUCTOR = "ELECTRO_ILLAGER";
    public static final String NECROMANCER = "NECROMANCER_ILLAGER";
    public static final String SHAMAN = "SHAMAN_ILLAGER";
    public static final String TANK = "TANK_ILLAGER";
    public static final String ASSASSIN = "ASSASSIN_ILLAGER";
    public static final String FROSTMAGE = "FROST_ILLAGER";

    public static final String NUAOS = "NUAOS_ELITE";
    public static final String XYDRAX = "XYDRAX_ELITE";
    public static final String MODUR = "MODUR_ELITE";
    public static final String VOLDON = "VOLDON_ELITE";

    //Hunter Illager
    public static final String HUNTER = "hunterillager";

    //Enchant With Mob
    public static final String ENCHANTER = "enchanter";

    //It Takes a Pillage
    public static final String ARCHER = "ARCHER";
    public static final String SKIRMISHER = "SKIRMISHER";
    public static final String LEGIONER = "LEGIONER";

    //Illage & Spillage
    public static final String IGNITER = "entity.illageandspillage.igniter";
    public static final String TWITTOLLAGER = "entity.illageandspillage.twittollager";
    public static final String PRESERVER = "entity.illageandspillage.preserver";
    public static final String ABSORBER = "entity.illageandspillage.absorber";
    public static final String CROCOFANG = "entity.illageandspillage.crocofang";
    public static final String MAGISPELLER = "entity.illageandspillage.magispeller";
    public static final String SPIRITCALLER = "entity.illageandspillage.spiritcaller";
    public static final String FREAKAGER = "entity.illageandspillage.freakager";
    public static final String BOSS_RANDOMIZER = "entity.illageandspillage.boss_randomizer";

    //Savage and Ravage
    public static final String GRIEFER = "GRIEFER";
    public static final String EXECUTIONER = "EXECUTIONER";
    public static final String TRICKSTER = "TRICKSTER";
    public static final String ICEOLOGER_SR = "SR_ICEOLOGER";

    //Dungeons Mobs
    public static final String MOUNTAINEER = "mountaineer";
    public static final String ROYAL_GUARD = "royal_guard";
    public static final String GEOMANCER = "geomancer";
    public static final String ILLUSIONER_DM = "DM_ILLUSIONER";
    public static final String MAGE = "mage";
    public static final String ICEOLOGER_DM = "iceologer";
    public static final String WINDCALLER = "windcaller";
    public static final String SQUALL_GOLEM = "squall_golem";
    public static final String REDSTONE_GOLEM = "redstone_golem";

    private static final int[] BLANK = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    public static boolean isRaiderTypeEnabled(String raiderType)
    {
        return DifficultRaidsConfig.ENABLED_RAIDERS.containsKey(raiderType) && DifficultRaidsConfig.ENABLED_RAIDERS.get(raiderType).get();
    }

    public static boolean isRaiderTypeRegistered(String raiderType)
    {
        return REGISTERED_RAIDER_TYPES.contains(raiderType);
    }

    public static void registerRaiders()
    {
        RaidEnemyRegistry.createRaiderType(ILLUSIONER, EntityType.ILLUSIONER);
        RaidEnemyRegistry.createRaiderType(WARRIOR, DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(DART, DifficultRaidsEntityTypes.DART_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(CONDUCTOR, DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(NECROMANCER, DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(SHAMAN, DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(TANK, DifficultRaidsEntityTypes.TANK_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(ASSASSIN, DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get());
        RaidEnemyRegistry.createRaiderType(FROSTMAGE, DifficultRaidsEntityTypes.FROST_ILLAGER.get());

        RaidEnemyRegistry.createRaiderType(NUAOS, NUAOS_ELITE.get());
        RaidEnemyRegistry.createRaiderType(XYDRAX, XYDRAX_ELITE.get());
        RaidEnemyRegistry.createRaiderType(MODUR, MODUR_ELITE.get());
        RaidEnemyRegistry.createRaiderType(VOLDON, VOLDON_ELITE.get());

        //Compatibility
        if(Compat.SAVAGE_AND_RAVAGE.isLoaded()) RaidEnemyRegistry.createRaiderType(ICEOLOGER_SR, SREntityTypes.ICEOLOGER.get());
        if(Compat.DUNGEONS_MOBS.isLoaded()) RaidEnemyRegistry.createRaiderType(ILLUSIONER_DM, ModEntityTypes.ILLUSIONER.get());
    }

    public static void compileWaveData(final Map<ResourceLocation, RaiderEntriesHolder> data)
    {
        CURRENT_WAVES.clear();
        for(RaidDifficulty d : RaidDifficulty.values()) if(d != RaidDifficulty.DEFAULT)
            CURRENT_WAVES.put(d, new RaidEnemyManager(DEFAULT_WAVES.get(d)));

        List<RaiderEntriesHolder> replaceEntries = new ArrayList<>();
        List<RaiderEntriesHolder> modifyEntries = new ArrayList<>();

        for(Map.Entry<ResourceLocation, RaiderEntriesHolder> entry : data.entrySet())
        {
            boolean replace = entry.getKey().getNamespace().equals(DifficultRaids.MODID);
            LogUtils.getLogger().info("DifficultRaids: Organizing Raid Data for ResourceLocation{%s}, replace=%s.".formatted(entry.getKey().toString(), replace));

            RaiderEntriesHolder holder = entry.getValue();
            holder.setReplace(replace);
            (replace ? replaceEntries : modifyEntries).add(holder);
        }

        List<RaiderEntriesHolder> sequentialHolders = new ArrayList<>();
        sequentialHolders.addAll(replaceEntries);
        sequentialHolders.addAll(modifyEntries);

        for(RaiderEntriesHolder holder : sequentialHolders)
        {
            for(var holderEntry : holder.getWaves().entrySet())
            {
                RaidEnemyManager manager = CURRENT_WAVES.get(holderEntry.getKey());
                for(String raiderType : holderEntry.getValue().keySet())
                    manager.add(raiderType, holderEntry.getValue().get(raiderType), holder.isReplace());
            }
        }
    }

    public static void printWaveData(Logger logger)
    {
        for(RaidEnemyManager d : CURRENT_WAVES.values())
            logger.info("Raid Data: " + d.toString());
    }

    private static void createRaiderType(String typeName, EntityType<? extends Raider> type)
    {
        Raid.RaiderType.create(typeName, type, BLANK);
    }

    public static void registerWaves()
    {
        RaidEnemyRegistry.createDefaultWavesFor(RaidDifficulty.HERO)
                .withRaider(PILLAGER,           0, 4, 3, 3, 4, 5, 5, 3)
                .withRaider(VINDICATOR,         0, 2, 1, 1, 2, 3, 1, 2)
                .withRaider(WARRIOR,            0, 2, 1, 1, 2, 1, 3, 2)
                .withRaider(SKIRMISHER,         0, 1, 1, 2, 1, 1, 1, 3)
                .withRaider(TANK,               0, 0, 2, 0, 2, 0, 2, 1)
                .withRaider(LEGIONER,           0, 0, 0, 1, 2, 1, 3, 3)
                .withRaider(DART,               0, 0, 0, 1, 1, 1, 0, 0)
                .withRaider(HUNTER,             0, 1, 2, 2, 2, 2, 2, 3)
                .withRaider(ARCHER,             0, 2, 1, 2, 3, 3, 3, 5)
                .withRaider(WITCH,              0, 0, 1, 0, 3, 1, 0, 2)
                .withRaider(RAVAGER,            0, 0, 0, 1, 0, 2, 1, 2)
                .withRaider(ILLUSIONER,         0, 0, 1, 0, 0, 0, 1, 0)
                .withRaider(ASSASSIN,           0, 0, 0, 0, 0, 1, 0, 0)
                .withRaider(EVOKER,             0, 0, 0, 0, 1, 0, 2, 2)
                .withRaider(CONDUCTOR,          0, 0, 0, 0, 0, 0, 0, 1)
                .withRaider(NECROMANCER,        0, 0, 0, 0, 1, 0, 0, 0)
                .withRaider(FROSTMAGE,          0, 0, 0, 0, 0, 1, 0, 0)
                .withRaider(SHAMAN,             0, 0, 0, 1, 0, 0, 1, 1)
                .withRaider(ENCHANTER,          0, 0, 1, 1, 1, 1, 0, 1)
                .withRaider(IGNITER,            0, 0, 1, 2, 2, 2, 2, 4)
                .withRaider(TWITTOLLAGER,       0, 0, 1, 1, 2, 0, 2, 2)
                .withRaider(PRESERVER,          0, 1, 0, 1, 2, 1, 3, 4)
                .withRaider(ABSORBER,           0, 0, 1, 0, 1, 0, 0, 2)
                .withRaider(CROCOFANG,          0, 0, 1, 1, 2, 3, 1, 3)
                .withRaider(MAGISPELLER,        0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(SPIRITCALLER,       0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(FREAKAGER,          0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(BOSS_RANDOMIZER,    0, 0, 0, 1, 0, 1, 0, 1)
                .withRaider(GRIEFER,            0, 1, 1, 2, 2, 2, 3, 3)
                .withRaider(EXECUTIONER,        0, 1, 1, 1, 0, 1, 2, 2)
                .withRaider(TRICKSTER,          0, 0, 1, 0, 1, 1, 1, 2)
                .withRaider(ICEOLOGER_SR,       0, 0, 0, 1, 0, 1, 0, 1)
                .withRaider(MOUNTAINEER,        0, 1, 3, 1, 1, 4, 2, 5)
                .withRaider(ROYAL_GUARD,        0, 1, 1, 1, 1, 2, 1, 2)
                .withRaider(GEOMANCER,          0, 0, 1, 1, 0, 2, 1, 2)
                .withRaider(ILLUSIONER_DM,      0, 0, 1, 0, 1, 1, 1, 2)
                .withRaider(MAGE,               0, 0, 1, 1, 0, 2, 0, 2)
                .withRaider(ICEOLOGER_DM,       0, 0, 0, 1, 0, 1, 0, 1)
                .withRaider(WINDCALLER,         0, 0, 1, 1, 0, 2, 1, 2)
                .withRaider(SQUALL_GOLEM,       0, 0, 1, 1, 1, 1, 0, 2)
                .withRaider(REDSTONE_GOLEM,     0, 0, 0, 1, 0, 1, 0, 1)
                .withEliteWave(5, NUAOS_ELITE.get())
                .withEliteWave(7, NUAOS_ELITE.get(), VOLDON_ELITE.get())
                .registerDefault();

        RaidEnemyRegistry.createDefaultWavesFor(RaidDifficulty.LEGEND)
                .withRaider(PILLAGER,           0, 4, 3, 3, 4, 5, 5, 3)
                .withRaider(VINDICATOR,         0, 2, 3, 1, 3, 4, 2, 3)
                .withRaider(WARRIOR,            0, 2, 2, 3, 3, 1, 4, 4)
                .withRaider(SKIRMISHER,         0, 2, 2, 2, 3, 2, 2, 3)
                .withRaider(TANK,               0, 0, 2, 1, 2, 1, 2, 1)
                .withRaider(LEGIONER,           0, 1, 1, 2, 2, 3, 3, 4)
                .withRaider(DART,               0, 0, 2, 1, 2, 1, 3, 0)
                .withRaider(HUNTER,             0, 1, 3, 2, 3, 2, 3, 4)
                .withRaider(ARCHER,             0, 2, 2, 4, 3, 4, 3, 6)
                .withRaider(WITCH,              0, 1, 1, 2, 3, 1, 2, 2)
                .withRaider(RAVAGER,            0, 0, 1, 1, 0, 2, 1, 2)
                .withRaider(ILLUSIONER,         0, 0, 1, 1, 1, 0, 1, 0)
                .withRaider(ASSASSIN,           0, 1, 1, 1, 1, 1, 1, 1)
                .withRaider(EVOKER,             0, 0, 2, 2, 1, 2, 2, 2)
                .withRaider(CONDUCTOR,          0, 0, 1, 0, 0, 0, 1, 1)
                .withRaider(NECROMANCER,        0, 0, 0, 2, 0, 1, 2, 1)
                .withRaider(FROSTMAGE,          0, 0, 0, 0, 2, 2, 0, 1)
                .withRaider(SHAMAN,             0, 0, 1, 1, 1, 2, 2, 3)
                .withRaider(ENCHANTER,          0, 1, 2, 0, 0, 2, 0, 2)
                .withRaider(IGNITER,            0, 2, 2, 3, 2, 3, 2, 5)
                .withRaider(TWITTOLLAGER,       0, 1, 2, 1, 2, 1, 2, 3)
                .withRaider(PRESERVER,          0, 1, 1, 2, 2, 2, 4, 4)
                .withRaider(ABSORBER,           0, 0, 1, 1, 2, 0, 1, 2)
                .withRaider(CROCOFANG,          0, 1, 2, 1, 2, 3, 2, 4)
                .withRaider(MAGISPELLER,        0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(SPIRITCALLER,       0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(FREAKAGER,          0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(BOSS_RANDOMIZER,    0, 0, 1, 0, 0, 1, 1, 1)
                .withRaider(GRIEFER,            0, 1, 2, 2, 3, 2, 3, 3)
                .withRaider(EXECUTIONER,        0, 1, 2, 1, 1, 2, 2, 3)
                .withRaider(TRICKSTER,          0, 0, 1, 0, 1, 2, 2, 3)
                .withRaider(ICEOLOGER_SR,       0, 0, 1, 2, 0, 2, 0, 2)
                .withRaider(MOUNTAINEER,        0, 2, 3, 2, 2, 5, 2, 6)
                .withRaider(ROYAL_GUARD,        0, 1, 2, 2, 1, 4, 1, 4)
                .withRaider(GEOMANCER,          0, 1, 1, 2, 1, 2, 1, 3)
                .withRaider(ILLUSIONER_DM,      0, 0, 1, 2, 1, 3, 1, 3)
                .withRaider(MAGE,               0, 1, 1, 1, 0, 2, 1, 3)
                .withRaider(ICEOLOGER_DM,       0, 0, 1, 2, 0, 2, 0, 2)
                .withRaider(WINDCALLER,         0, 1, 1, 2, 1, 2, 1, 3)
                .withRaider(SQUALL_GOLEM,       0, 1, 1, 2, 1, 2, 1, 2)
                .withRaider(REDSTONE_GOLEM,     0, 1, 1, 1, 1, 1, 1, 1)
                .withEliteWave(3, NUAOS_ELITE.get(), VOLDON_ELITE.get())
                .withEliteWave(5, VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(7, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .registerDefault();

        RaidEnemyRegistry.createDefaultWavesFor(RaidDifficulty.MASTER)
                .withRaider(PILLAGER,           0, 5, 6, 5, 6, 6, 5, 5)
                .withRaider(VINDICATOR,         0, 3, 2, 3, 3, 2, 3, 4)
                .withRaider(WARRIOR,            0, 3, 1, 3, 3, 2, 4, 4)
                .withRaider(SKIRMISHER,         0, 2, 2, 4, 3, 5, 2, 5)
                .withRaider(TANK,               0, 2, 2, 2, 3, 3, 3, 3)
                .withRaider(LEGIONER,           0, 1, 2, 3, 2, 4, 3, 5)
                .withRaider(DART,               0, 0, 2, 2, 2, 2, 3, 4)
                .withRaider(HUNTER,             0, 3, 4, 4, 4, 5, 3, 4)
                .withRaider(ARCHER,             0, 3, 3, 5, 4, 5, 5, 7)
                .withRaider(WITCH,              0, 1, 3, 5, 4, 5, 3, 3)
                .withRaider(RAVAGER,            0, 1, 1, 1, 0, 3, 1, 3)
                .withRaider(ILLUSIONER,         0, 1, 1, 2, 1, 1, 2, 3)
                .withRaider(ASSASSIN,           0, 2, 2, 2, 2, 2, 2, 2)
                .withRaider(EVOKER,             0, 1, 2, 3, 4, 1, 1, 3)
                .withRaider(CONDUCTOR,          0, 1, 2, 0, 1, 2, 2, 3)
                .withRaider(NECROMANCER,        0, 1, 0, 3, 1, 2, 0, 3)
                .withRaider(FROSTMAGE,          0, 1, 0, 0, 1, 2, 4, 3)
                .withRaider(SHAMAN,             0, 2, 2, 2, 2, 3, 3, 3)
                .withRaider(ENCHANTER,          0, 1, 1, 1, 1, 1, 1, 3)
                .withRaider(IGNITER,            0, 3, 2, 4, 3, 4, 3, 6)
                .withRaider(TWITTOLLAGER,       0, 1, 3, 1, 3, 2, 2, 3)
                .withRaider(PRESERVER,          0, 2, 2, 2, 2, 2, 4, 4)
                .withRaider(ABSORBER,           0, 1, 1, 1, 2, 1, 1, 3)
                .withRaider(CROCOFANG,          0, 2, 2, 3, 2, 4, 2, 5)
                .withRaider(MAGISPELLER,        0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(SPIRITCALLER,       0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(FREAKAGER,          0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(BOSS_RANDOMIZER,    0, 0, 1, 1, 0, 1, 1, 1)
                .withRaider(GRIEFER,            0, 2, 3, 2, 3, 3, 3, 4)
                .withRaider(EXECUTIONER,        0, 2, 2, 2, 3, 2, 3, 4)
                .withRaider(TRICKSTER,          0, 1, 2, 1, 2, 2, 2, 3)
                .withRaider(ICEOLOGER_SR,       0, 1, 2, 3, 1, 3, 1, 3)
                .withRaider(MOUNTAINEER,        0, 3, 4, 5, 2, 7, 4, 7)
                .withRaider(ROYAL_GUARD,        0, 2, 2, 3, 2, 4, 3, 5)
                .withRaider(GEOMANCER,          0, 2, 1, 3, 2, 3, 1, 3)
                .withRaider(ILLUSIONER_DM,      0, 1, 2, 2, 2, 3, 2, 3)
                .withRaider(MAGE,               0, 1, 2, 2, 1, 3, 1, 3)
                .withRaider(ICEOLOGER_DM,       0, 1, 2, 3, 1, 3, 1, 3)
                .withRaider(WINDCALLER,         0, 1, 2, 3, 2, 3, 2, 3)
                .withRaider(SQUALL_GOLEM,       0, 1, 2, 2, 2, 3, 2, 3)
                .withRaider(REDSTONE_GOLEM,     0, 1, 1, 2, 1, 2, 1, 2)
                .withEliteWave(1, NUAOS_ELITE.get(), VOLDON_ELITE.get())
                .withEliteWave(3, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(5, XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(6, XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(7, XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .registerDefault();

        RaidEnemyRegistry.createDefaultWavesFor(RaidDifficulty.GRANDMASTER)
                .withRaider(PILLAGER,           0, 6, 7, 6, 7, 7, 6, 6)
                .withRaider(VINDICATOR,         0, 4, 3, 4, 4, 3, 4, 5)
                .withRaider(WARRIOR,            0, 4, 2, 4, 4, 3, 5, 5)
                .withRaider(SKIRMISHER,         0, 2, 2, 4, 3, 5, 2, 5)
                .withRaider(TANK,               0, 2, 2, 3, 4, 4, 4, 4)
                .withRaider(LEGIONER,           0, 1, 2, 3, 2, 4, 3, 5)
                .withRaider(DART,               0, 0, 2, 2, 2, 2, 3, 4)
                .withRaider(HUNTER,             0, 3, 4, 4, 4, 5, 3, 4)
                .withRaider(ARCHER,             0, 3, 3, 5, 4, 5, 5, 7)
                .withRaider(WITCH,              0, 1, 3, 5, 4, 5, 3, 3)
                .withRaider(RAVAGER,            0, 1, 1, 1, 0, 3, 1, 3)
                .withRaider(ILLUSIONER,         0, 0, 1, 2, 1, 1, 2, 3)
                .withRaider(ASSASSIN,           0, 2, 2, 2, 2, 2, 2, 2)
                .withRaider(EVOKER,             0, 1, 2, 3, 4, 1, 1, 3)
                .withRaider(CONDUCTOR,          0, 1, 2, 0, 1, 2, 2, 3)
                .withRaider(NECROMANCER,        0, 1, 0, 3, 1, 2, 0, 3)
                .withRaider(FROSTMAGE,          0, 1, 0, 0, 1, 2, 4, 3)
                .withRaider(SHAMAN,             0, 2, 2, 2, 2, 3, 3, 3)
                .withRaider(ENCHANTER,          0, 1, 1, 1, 1, 1, 1, 3)
                .withRaider(IGNITER,            0, 3, 2, 4, 3, 4, 3, 6)
                .withRaider(TWITTOLLAGER,       0, 1, 3, 1, 3, 2, 2, 3)
                .withRaider(PRESERVER,          0, 2, 2, 2, 2, 2, 4, 4)
                .withRaider(ABSORBER,           0, 1, 1, 1, 2, 1, 1, 3)
                .withRaider(CROCOFANG,          0, 2, 2, 3, 2, 4, 2, 5)
                .withRaider(MAGISPELLER,        0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(SPIRITCALLER,       0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(FREAKAGER,          0, 0, 0, 0, 0, 0, 0, 0)
                .withRaider(BOSS_RANDOMIZER,    0, 1, 1, 1, 1, 1, 1, 1)
                .withRaider(GRIEFER,            0, 2, 3, 2, 3, 3, 3, 4)
                .withRaider(EXECUTIONER,        0, 2, 2, 2, 3, 2, 3, 4)
                .withRaider(TRICKSTER,          0, 1, 2, 1, 2, 2, 2, 3)
                .withRaider(ICEOLOGER_SR,       0, 1, 2, 3, 1, 3, 1, 3)
                .withRaider(MOUNTAINEER,        0, 3, 4, 5, 2, 7, 4, 7)
                .withRaider(ROYAL_GUARD,        0, 2, 2, 3, 2, 4, 3, 5)
                .withRaider(GEOMANCER,          0, 2, 1, 3, 2, 3, 1, 3)
                .withRaider(ILLUSIONER_DM,      0, 1, 2, 2, 2, 3, 2, 3)
                .withRaider(MAGE,               0, 1, 2, 2, 1, 3, 1, 3)
                .withRaider(ICEOLOGER_DM,       0, 1, 2, 3, 1, 3, 1, 3)
                .withRaider(WINDCALLER,         0, 1, 2, 3, 2, 3, 2, 3)
                .withRaider(SQUALL_GOLEM,       0, 1, 2, 2, 2, 3, 2, 3)
                .withRaider(REDSTONE_GOLEM,     0, 1, 1, 3, 1, 3, 1, 3)
                .withEliteWave(1, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(2, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(3, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(4, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(5, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(6, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .withEliteWave(7, NUAOS_ELITE.get(), VOLDON_ELITE.get(), XYDRAX_ELITE.get(), MODUR_ELITE.get())
                .registerDefault();

        //Reflection Option:
        //ObfuscationReflectionHelper.findField(Raid.RaiderType.class, "f_37815_").set(Raid.RaiderType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5});
    }

    public static boolean isEliteWave(RaidDifficulty raidDifficulty, int wave)
    {
        return CURRENT_WAVES.get(raidDifficulty).isEliteWave(wave);
    }

    public static EntityType<?> getRandomElite(RaidDifficulty raidDifficulty, int wave)
    {
        List<EntityType<?>> pool = CURRENT_WAVES.get(raidDifficulty).getElites(wave);

        if(pool.isEmpty()) return null;
        else if(pool.size() == 1) return pool.get(0);
        else return pool.get(new Random().nextInt(pool.size()));
    }

    public static List<Integer> getWaves(RaidDifficulty raidDifficulty, String raiderType)
    {
        return CURRENT_WAVES.get(raidDifficulty).getWaves().getOrDefault(raiderType.toUpperCase(), Arrays.stream(BLANK).boxed().collect(Collectors.toList()));
    }

    //Waves

    public static RaidEnemyManager createDefaultWavesFor(RaidDifficulty raidDifficulty)
    {
        return new RaidEnemyManager(raidDifficulty);
    }
}
