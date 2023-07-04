package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

import java.util.*;

public class RaidEnemyRegistry
{
    public static final Map<RaidDifficulty, RaidEnemies> WAVES = new HashMap<>();
    public static final Map<RaidDifficulty, RaidReinforcements> REINFORCEMENTS = new HashMap<>();
    public static final Map<Integer, List<EntityType<?>>> ELITES = new HashMap<>();

    public static final String VINDICATOR = "VINDICATOR";
    public static final String EVOKER = "EVOKER";
    public static final String PILLAGER = "PILLAGER";
    public static final String WITCH = "WITCH";
    public static final String RAVAGER = "RAVAGER";
    public static final String ILLUSIONER = "ILLUSIONER";
    public static final String WARRIOR = "WARRIOR_ILLAGER";
    public static final String DART = "DART_ILLAGER";
    public static final String CONDUCTOR = "ELECTRO_ILLAGER";
    public static final String NECROMANCER = "NECROMANCER_ILLAGER";
    public static final String SHAMAN = "SHAMAN_ILLAGER";
    public static final String TANK = "TANK_ILLAGER";
    public static final String ASSASSIN = "ASSASSIN_ILLAGER";
    public static final String FROSTMAGE = "FROST_ILLAGER";

    //Mod Compatibility
    public static final String HUNTER = "HUNTERILLAGER";
    public static final String ENCHANTER = "ENCHANTER";

    private static final int[] BLANK = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

    //TODO: Increase wave counts, and this should then also use RaidDifficulty
    public static int getEliteWaveTier(Difficulty difficulty, int wave)
    {
        return difficulty.equals(Difficulty.EASY) ? (wave == 3 ? 1 : -1) : (difficulty.equals(Difficulty.NORMAL) ? (wave == 5 ? 1 : -1) : (difficulty.equals(Difficulty.HARD) ? (wave == 5 ? 1 : (wave == 7 ? 2 : -1)) : -1));
    }

    public static boolean isRaiderTypeEnabled(String raiderType)
    {
        return !DifficultRaidsConfig.ENABLED_RAIDERS.containsKey(raiderType.toUpperCase()) || DifficultRaidsConfig.ENABLED_RAIDERS.get(raiderType.toUpperCase()).get();
    }

    public static boolean isRaiderTypeRegistered(String raiderType)
    {
        return WAVES.values().stream().anyMatch(re -> re.waves.keySet().stream().anyMatch(raiderType::equalsIgnoreCase));
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
    }

    private static void createRaiderType(String typeName, EntityType<? extends Raider> type)
    {
        Raid.RaiderType.create(typeName, type, BLANK);
    }

    public static void registerElites()
    {
        //TODO: Change these when reworking the wave system
        RaidEnemyRegistry.registerTierElites(1,
                DifficultRaidsEntityTypes.NUAOS_ELITE.get()
        );

        RaidEnemyRegistry.registerTierElites(2,
                DifficultRaidsEntityTypes.MODUR_ELITE.get(),
                DifficultRaidsEntityTypes.XYDRAX_ELITE.get(),
                DifficultRaidsEntityTypes.VOLDON_ELITE.get()
        );
    }

    public static void registerWaves()
    {
        RaidEnemyRegistry.createWavesFor(RaidDifficulty.DEFAULT)
                .withRaider(PILLAGER,           new int[]{0, 4, 3, 3, 4, 4, 4, 2})
                .withRaider(VINDICATOR,         new int[]{0, 0, 2, 0, 1, 4, 2, 5})
                .withRaider(WARRIOR,            new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(TANK,               new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(DART,               new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(HUNTER,             new int[]{0, 0, 1, 2, 2, 1, 2, 3})
                .withRaider(WITCH,              new int[]{0, 0, 0, 0, 3, 0, 0, 1})
                .withRaider(RAVAGER,            new int[]{0, 0, 0, 1, 0, 1, 0, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(ASSASSIN,           new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(EVOKER,             new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(SHAMAN,             new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(ENCHANTER,          new int[]{0, 0, 1, 0, 1, 1, 2, 1})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.HERO)
                .withRaider(PILLAGER,           new int[]{0, 4, 3, 3, 4, 5, 5, 3})
                .withRaider(VINDICATOR,         new int[]{0, 2, 1, 1, 2, 3, 1, 2})
                .withRaider(WARRIOR,            new int[]{0, 2, 1, 1, 2, 1, 3, 2})
                .withRaider(TANK,               new int[]{0, 0, 2, 0, 2, 0, 2, 1})
                .withRaider(DART,               new int[]{0, 0, 0, 1, 1, 1, 0, 0})
                .withRaider(HUNTER,             new int[]{0, 1, 2, 2, 2, 2, 2, 3})
                .withRaider(WITCH,              new int[]{0, 0, 1, 0, 3, 1, 0, 2})
                .withRaider(RAVAGER,            new int[]{0, 0, 0, 1, 0, 2, 1, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 0, 0, 0, 1, 0})
                .withRaider(ASSASSIN,           new int[]{0, 0, 0, 0, 0, 1, 0, 0})
                .withRaider(EVOKER,             new int[]{0, 0, 0, 0, 1, 0, 2, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 0, 0, 0, 0, 0, 1})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 0, 1, 0, 0, 0})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 0, 1, 0, 0})
                .withRaider(SHAMAN,             new int[]{0, 0, 0, 1, 0, 0, 1, 1})
                .withRaider(ENCHANTER,          new int[]{0, 0, 1, 1, 1, 1, 0, 1})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.LEGEND)
                .withRaider(PILLAGER,           new int[]{0, 4, 3, 3, 4, 5, 5, 3})
                .withRaider(VINDICATOR,         new int[]{0, 2, 3, 1, 3, 4, 2, 3})
                .withRaider(WARRIOR,            new int[]{0, 2, 2, 3, 3, 1, 4, 4})
                .withRaider(TANK,               new int[]{0, 0, 2, 1, 2, 1, 2, 1})
                .withRaider(DART,               new int[]{0, 0, 2, 1, 2, 1, 3, 0})
                .withRaider(HUNTER,             new int[]{0, 1, 3, 2, 3, 2, 3, 4})
                .withRaider(WITCH,              new int[]{0, 1, 1, 2, 3, 1, 2, 2})
                .withRaider(RAVAGER,            new int[]{0, 0, 1, 1, 0, 2, 1, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 1, 1, 0, 1, 0})
                .withRaider(ASSASSIN,           new int[]{0, 1, 1, 1, 1, 1, 1, 1})
                .withRaider(EVOKER,             new int[]{0, 0, 2, 2, 1, 2, 2, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 1, 0, 0, 0, 1, 1})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 2, 0, 1, 2, 1})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 2, 2, 0, 1})
                .withRaider(SHAMAN,             new int[]{0, 0, 1, 1, 1, 2, 2, 3})
                .withRaider(ENCHANTER,          new int[]{0, 1, 2, 0, 0, 2, 0, 2})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.MASTER)
                .withRaider(PILLAGER,           new int[]{0, 5, 6, 5, 6, 6, 5, 5})
                .withRaider(VINDICATOR,         new int[]{0, 3, 2, 3, 3, 2, 3, 4})
                .withRaider(WARRIOR,            new int[]{0, 3, 1, 3, 3, 2, 4, 4})
                .withRaider(TANK,               new int[]{0, 2, 2, 2, 3, 3, 3, 3})
                .withRaider(DART,               new int[]{0, 0, 2, 2, 2, 2, 3, 4})
                .withRaider(HUNTER,             new int[]{0, 3, 4, 4, 4, 5, 3, 4})
                .withRaider(WITCH,              new int[]{0, 1, 3, 5, 4, 5, 3, 3})
                .withRaider(RAVAGER,            new int[]{0, 1, 1, 1, 0, 3, 1, 3})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 2, 1, 0, 2, 0})
                .withRaider(ASSASSIN,           new int[]{0, 2, 2, 2, 2, 2, 2, 2})
                .withRaider(EVOKER,             new int[]{0, 1, 2, 3, 4, 1, 1, 3})
                .withRaider(CONDUCTOR,          new int[]{0, 1, 2, 0, 1, 2, 2, 3})
                .withRaider(NECROMANCER,        new int[]{0, 1, 0, 3, 1, 2, 0, 3})
                .withRaider(FROSTMAGE,          new int[]{0, 1, 0, 0, 1, 2, 4, 3})
                .withRaider(SHAMAN,             new int[]{0, 2, 2, 2, 2, 3, 3, 3})
                .withRaider(ENCHANTER,          new int[]{0, 1, 1, 1, 1, 1, 1, 3})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.GRANDMASTER)
                .withRaider(PILLAGER,           new int[]{0, 6, 7, 6, 7, 7, 6, 6})
                .withRaider(VINDICATOR,         new int[]{0, 4, 3, 4, 4, 3, 4, 5})
                .withRaider(WARRIOR,            new int[]{0, 4, 2, 4, 4, 3, 5, 5})
                .withRaider(TANK,               new int[]{0, 2, 2, 3, 4, 4, 4, 4})
                .withRaider(DART,               new int[]{0, 0, 2, 2, 2, 2, 3, 4})
                .withRaider(HUNTER,             new int[]{0, 3, 4, 4, 4, 5, 3, 4})
                .withRaider(WITCH,              new int[]{0, 1, 3, 5, 4, 5, 3, 3})
                .withRaider(RAVAGER,            new int[]{0, 1, 1, 1, 0, 3, 1, 3})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 2, 1, 0, 2, 0})
                .withRaider(ASSASSIN,           new int[]{0, 2, 2, 2, 2, 2, 2, 2})
                .withRaider(EVOKER,             new int[]{0, 1, 2, 3, 4, 1, 1, 3})
                .withRaider(CONDUCTOR,          new int[]{0, 1, 2, 0, 1, 2, 2, 3})
                .withRaider(NECROMANCER,        new int[]{0, 1, 0, 3, 1, 2, 0, 3})
                .withRaider(FROSTMAGE,          new int[]{0, 1, 0, 0, 1, 2, 4, 3})
                .withRaider(SHAMAN,             new int[]{0, 2, 2, 2, 2, 3, 3, 3})
                .withRaider(ENCHANTER,          new int[]{0, 1, 1, 1, 1, 1, 1, 3})
                .register();

        //TODO: Look into using Reflection instead
        //ObfuscationReflectionHelper.findField(Raid.RaiderType.class, "f_37815_").set(Raid.RaiderType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5});
    }

    public static void registerReinforcements()
    {
        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.HERO)
                .withGroups(1)
                .addEntry(EntityType.ZOMBIE, 3, 0, 2, 5, -2, 3)
                .addEntry(EntityType.SKELETON, 3, 0, 2, 5, -2, 3)
                .addEntry(EntityType.WITCH, 1, 2, 1, 2, -1, 1)
                .addEntry(EntityType.CREEPER, 1, 2, 1, 5, 0, 2)
                .addEntry(EntityType.SPIDER, 2, 1, 2, 4, 0, 0)
                .register();

        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.LEGEND)
                .withGroups(2)
                .addEntry(EntityType.ZOMBIE, 4, 0, 4, 6, -2, 3)
                .addEntry(EntityType.SKELETON, 4, 0, 4, 6, -2, 3)
                .addEntry(EntityType.WITCH, 1, 2, 1, 2, -1, 1)
                .addEntry(EntityType.CREEPER, 1, 2, 1, 5, 0, 2)
                .addEntry(EntityType.SPIDER, 2, 1, 3, 7, 0, 0)
                .addEntry(EntityType.STRAY, 2, 3, 5, 7, 0, 2)
                .addEntry(EntityType.HUSK, 2, 3, 5, 7, 0, 2)
                .addEntry(EntityType.CAVE_SPIDER, 1, 4, 3, 4, -1, 1)
                .register();

        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.MASTER)
                .withGroups(3)
                .addEntry(EntityType.ZOMBIE, 4, 0, 2, 7, -2, 3)
                .addEntry(EntityType.SKELETON, 4, 0, 2, 7, -2, 3)
                .addEntry(EntityType.WITCH, 2, 2, 1, 4, -1, 1)
                .addEntry(EntityType.CREEPER, 2, 2, 1, 7, 0, 2)
                .addEntry(EntityType.SPIDER, 3, 1, 2, 6, 0, 0)
                .addEntry(EntityType.STRAY, 2, 3, 5, 8, 0, 2)
                .addEntry(EntityType.HUSK, 2, 3, 5, 8, 0, 2)
                .addEntry(EntityType.CAVE_SPIDER, 1, 4, 3, 4, -1, 1)
                .addEntry(EntityType.WITHER_SKELETON, 1, 4, 1, 2, 0, 3)
                .register();

        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.GRANDMASTER)
                .withGroups(4)
                .addEntry(EntityType.ZOMBIE, 4, 0, 2, 7, -2, 3)
                .addEntry(EntityType.SKELETON, 4, 0, 2, 7, -2, 3)
                .addEntry(EntityType.WITCH, 2, 2, 1, 4, -1, 1)
                .addEntry(EntityType.CREEPER, 2, 2, 1, 7, 0, 2)
                .addEntry(EntityType.SPIDER, 3, 1, 2, 6, 0, 0)
                .addEntry(EntityType.STRAY, 2, 3, 5, 8, 0, 2)
                .addEntry(EntityType.HUSK, 2, 3, 5, 8, 0, 2)
                .addEntry(EntityType.CAVE_SPIDER, 1, 4, 3, 4, -1, 1)
                .addEntry(EntityType.WITHER_SKELETON, 1, 4, 1, 2, 0, 3)
                .addEntry(EntityType.BLAZE, 1, 3, 2, 4, 0, 1)
                .addEntry(EntityType.PIGLIN_BRUTE, 1, 3, 2, 4, 0, 1)
                .addEntry(EntityType.ENDERMAN, 1, 3, 2, 4, 0, 1)
                .register();
    }

    //Primary Accessors

    public static EntityType<?> getRandomElite(int tier)
    {
        List<EntityType<?>> pool = new ArrayList<>();

        if(ELITES.containsKey(tier)) pool.addAll(ELITES.get(tier));
        else pool.addAll(ELITES.get(1));

        return pool.get(new Random().nextInt(pool.size()));
    }

    public static int[] getWaves(RaidDifficulty raidDifficulty, String raiderType)
    {
        return WAVES.get(raidDifficulty).waves.getOrDefault(raiderType.toUpperCase(), BLANK);
    }

    public static Map<EntityType<?>, Integer> getReinforcements(int waves, RaidDifficulty raidDifficulty, Difficulty levelDifficulty)
    {
        final Random random = new Random();

        RaidReinforcements info = REINFORCEMENTS
                .entrySet()
                .stream()
                .filter(e -> e.getKey().is(raidDifficulty))
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing RaidReinforcements entry for RaidDifficulty " + raidDifficulty)).getValue();

        List<? extends EntityType<?>> pool = info.reinforcements.stream().filter(e -> e.minWave() <= waves).map(e -> e.entityType).toList();

        //Generate the groups of reinforcements
        Map<EntityType<?>, Integer> groups = new HashMap<>();
        for(int i = 0; i < info.groups; i++)
        {
            EntityType<?> entity = pool.get(random.nextInt(pool.size()));

            if(groups.getOrDefault(entity, 0) == info.getEntry(entity).maxGroups()) { i--; continue; }

            groups.put(entity, groups.getOrDefault(entity, 0) + 1);
        }

        //Generate the actual number of enemies that will spawn from the groups
        Map<EntityType<?>, Integer> spawns = new HashMap<>();

        for(Map.Entry<EntityType<?>, Integer> groupInfo : groups.entrySet())
        {
            ReinforcementEntry entityEntry = info.getEntry(groupInfo.getKey());

            for(int i = 0; i < groupInfo.getValue(); i++)
            {
                int count = random.nextInt(entityEntry.minSpawnCount(), entityEntry.maxSpawnCount() + 1);

                count += levelDifficulty.equals(Difficulty.EASY) ? entityEntry.easySpawnCountModifier() : (levelDifficulty.equals(Difficulty.HARD) ? entityEntry.hardSpawnCountModifier() : 0);

                //In case count somehow becomes negative or 0
                if(count > 0) spawns.put(groupInfo.getKey(), spawns.getOrDefault(groupInfo.getKey(), 0) + count);
            }
        }

        return spawns;
    }

    //Elites
    public static void registerTierElites(int tier, EntityType<?>... elites)
    {
        ELITES.put(tier, List.of(elites));
    }

    //Waves

    public static RaidEnemies createWavesFor(RaidDifficulty raidDifficulty)
    {
        RaidEnemies waves = new RaidEnemies();
        waves.raidDifficulty = raidDifficulty;
        waves.waves = new HashMap<>();
        return waves;
    }

    public static class RaidEnemies
    {
        private RaidDifficulty raidDifficulty;
        private Map<String, int[]> waves;

        public RaidEnemies withRaider(String raider, int[] waves)
        {
            this.waves.put(raider, waves);
            return this;
        }

        public void register()
        {
            WAVES.put(this.raidDifficulty, this);
        }
    }

    //Reinforcements

    public static RaidReinforcements createReinforcementsFor(RaidDifficulty raidDifficulty)
    {
        RaidReinforcements reinforcements = new RaidReinforcements();
        reinforcements.raidDifficulty = raidDifficulty;
        return reinforcements;
    }

    public static class RaidReinforcements
    {
        private RaidDifficulty raidDifficulty;
        private int groups;
        private List<ReinforcementEntry> reinforcements = new ArrayList<>();

        public RaidReinforcements withGroups(int groups)
        {
            this.groups = groups;
            return this;
        }

        public RaidReinforcements addEntry(EntityType<? extends LivingEntity> entityType, int maxGroups, int minWave, int minSpawnCount, int maxSpawnCount, int easySpawnCountModifier, int hardSpawnCountModifier)
        {
            this.reinforcements.add(new ReinforcementEntry(entityType, maxGroups, minWave, minSpawnCount, maxSpawnCount, easySpawnCountModifier, hardSpawnCountModifier));
            return this;
        }

        public void register()
        {
            REINFORCEMENTS.put(this.raidDifficulty, this);
        }

        public ReinforcementEntry getEntry(EntityType<?> entityType)
        {
            return this.reinforcements.stream().filter(e -> e.entityType.equals(entityType)).findFirst().orElse(null);
        }
    }

    public record ReinforcementEntry(EntityType<?> entityType, int maxGroups, int minWave, int minSpawnCount, int maxSpawnCount, int easySpawnCountModifier, int hardSpawnCountModifier) {}

}
