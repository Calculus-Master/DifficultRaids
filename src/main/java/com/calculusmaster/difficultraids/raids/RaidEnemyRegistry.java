package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

import java.util.*;

public class RaidEnemyRegistry
{
    public static final Map<String, RaidWaveEnemies> SPAWNS = new HashMap<>();
    public static final Map<RaidDifficulty, RaidEnemies> WAVES = new HashMap<>();
    public static final Map<RaidDifficulty, RaidReinforcements> REINFORCEMENTS = new HashMap<>();

    private static final String VINDICATOR = "VINDICATOR";
    private static final String EVOKER = "EVOKER";
    private static final String PILLAGER = "PILLAGER";
    private static final String WITCH = "WITCH";
    private static final String RAVAGER = "RAVAGER";
    private static final String ILLUSIONER = "ILLUSIONER";
    private static final String WARRIOR = "WARRIOR_ILLAGER";
    private static final String DART = "DART_ILLAGER";
    private static final String CONDUCTOR = "ELECTRO_ILLAGER";
    private static final String NECROMANCER = "NECROMANCER_ILLAGER";
    private static final String SHAMAN = "SHAMAN_ILLAGER";
    private static final String TANK = "TANK_ILLAGER";
    private static final String ASSASSIN = "ASSASSIN_ILLAGER";
    private static final String FROSTMAGE = "FROST_ILLAGER";

    private static final int[] BLANK = new int[]{0, 0, 0, 0, 0, 0, 0, 0};

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

    public static void registerWaves()
    {
        RaidEnemyRegistry.createWavesFor(RaidDifficulty.DEFAULT)
                .withRaider(PILLAGER,           new int[]{0, 4, 3, 3, 4, 4, 4, 2})
                .withRaider(VINDICATOR,         new int[]{0, 0, 2, 0, 1, 4, 2, 5})
                .withRaider(WARRIOR,            new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(TANK,               new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(DART,               new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(WITCH,              new int[]{0, 0, 0, 0, 3, 0, 0, 1})
                .withRaider(RAVAGER,            new int[]{0, 0, 0, 1, 0, 1, 0, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(ASSASSIN,           new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(EVOKER,             new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withRaider(SHAMAN,             new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.HERO)
                .withRaider(PILLAGER,           new int[]{0, 5, 4, 4, 5, 5, 5, 3})
                .withRaider(VINDICATOR,         new int[]{0, 2, 3, 1, 2, 3, 3, 4})
                .withRaider(WARRIOR,            new int[]{0, 2, 3, 1, 2, 2, 3, 4})
                .withRaider(TANK,               new int[]{0, 0, 2, 0, 2, 0, 2, 1})
                .withRaider(DART,               new int[]{0, 0, 0, 1, 1, 1, 0, 0})
                .withRaider(WITCH,              new int[]{0, 0, 1, 0, 3, 1, 0, 2})
                .withRaider(RAVAGER,            new int[]{0, 0, 0, 1, 0, 2, 1, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 0, 0, 0, 1, 0})
                .withRaider(ASSASSIN,           new int[]{0, 0, 0, 0, 0, 1, 0, 0})
                .withRaider(EVOKER,             new int[]{0, 0, 0, 0, 1, 0, 2, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 0, 1, 0, 0, 0, 0})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 0, 1, 0, 0, 0})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 0, 1, 0, 0})
                .withRaider(SHAMAN,             new int[]{0, 0, 0, 0, 0, 1, 1, 1})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.LEGEND)
                .withRaider(PILLAGER,           new int[]{0, 6, 5, 5, 5, 5, 5, 4})
                .withRaider(VINDICATOR,         new int[]{0, 2, 3, 2, 3, 3, 3, 5})
                .withRaider(WARRIOR,            new int[]{0, 2, 4, 2, 3, 2, 4, 4})
                .withRaider(TANK,               new int[]{0, 0, 2, 1, 2, 1, 2, 1})
                .withRaider(DART,               new int[]{0, 0, 2, 1, 2, 1, 3, 0})
                .withRaider(WITCH,              new int[]{0, 1, 1, 2, 3, 1, 2, 2})
                .withRaider(RAVAGER,            new int[]{0, 0, 1, 1, 0, 2, 1, 2})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 1, 1, 0, 1, 0})
                .withRaider(ASSASSIN,           new int[]{0, 1, 1, 1, 1, 1, 1, 1})
                .withRaider(EVOKER,             new int[]{0, 0, 2, 2, 1, 2, 2, 2})
                .withRaider(CONDUCTOR,          new int[]{0, 0, 2, 0, 0, 0, 1, 1})
                .withRaider(NECROMANCER,        new int[]{0, 0, 0, 2, 0, 1, 2, 1})
                .withRaider(FROSTMAGE,          new int[]{0, 0, 0, 0, 2, 2, 0, 1})
                .withRaider(SHAMAN,             new int[]{0, 0, 1, 1, 1, 2, 2, 3})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.MASTER)
                .withRaider(PILLAGER,           new int[]{0, 7, 6, 6, 6, 6, 5, 5})
                .withRaider(VINDICATOR,         new int[]{0, 3, 2, 3, 3, 2, 3, 4})
                .withRaider(WARRIOR,            new int[]{0, 3, 1, 3, 3, 2, 4, 4})
                .withRaider(TANK,               new int[]{0, 2, 2, 2, 3, 3, 3, 3})
                .withRaider(DART,               new int[]{0, 0, 2, 2, 2, 2, 3, 4})
                .withRaider(WITCH,              new int[]{0, 1, 3, 7, 5, 5, 3, 3})
                .withRaider(RAVAGER,            new int[]{0, 1, 1, 1, 0, 3, 1, 3})
                .withRaider(ILLUSIONER,         new int[]{0, 0, 1, 2, 1, 0, 2, 0})
                .withRaider(ASSASSIN,           new int[]{0, 2, 2, 2, 2, 2, 2, 2})
                .withRaider(EVOKER,             new int[]{0, 1, 2, 3, 4, 1, 1, 3})
                .withRaider(CONDUCTOR,          new int[]{0, 1, 3, 0, 1, 2, 2, 3})
                .withRaider(NECROMANCER,        new int[]{0, 1, 0, 3, 1, 2, 0, 3})
                .withRaider(FROSTMAGE,          new int[]{0, 1, 0, 0, 1, 2, 4, 3})
                .withRaider(SHAMAN,             new int[]{0, 2, 2, 2, 2, 3, 3, 3})
                .register();

        RaidEnemyRegistry.createWavesFor(RaidDifficulty.APOCALYPSE)
                .withRaider(PILLAGER,           new int[]{0, 10, 13, 16, 19, 23, 25, 30})
                .withRaider(VINDICATOR,         new int[]{0, 5, 7, 9, 11, 13, 15, 17})
                .withRaider(WARRIOR,            new int[]{0, 5, 7, 9, 11, 13, 15, 17})
                .withRaider(TANK,               new int[]{0, 2, 4, 6, 8, 10, 12, 14})
                .withRaider(DART,               new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(WITCH,              new int[]{0, 10, 10, 10, 10, 10, 10, 10})
                .withRaider(RAVAGER,            new int[]{0, 3, 3, 4, 4, 5, 5, 7})
                .withRaider(ILLUSIONER,         new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(ASSASSIN,           new int[]{0, 6, 6, 6, 6, 6, 6, 6})
                .withRaider(EVOKER,             new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(CONDUCTOR,          new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(NECROMANCER,        new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(FROSTMAGE,          new int[]{0, 5, 5, 5, 5, 5, 5, 5})
                .withRaider(SHAMAN,             new int[]{0, 8, 8, 8, 8, 8, 8, 8})
                .register();

        //TODO: Look into using Reflection instead
        //ObfuscationReflectionHelper.findField(Raid.RaiderType.class, "f_37815_").set(Raid.RaiderType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5});
    }

    public static void registerReinforcements()
    {
        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.HERO)
                .withGroups(2)
                .addEntry(EntityType.ZOMBIE, 3, 0, 2, 5, -2, 3)
                .addEntry(EntityType.SKELETON, 3, 0, 2, 5, -2, 3)
                .addEntry(EntityType.WITCH, 1, 2, 1, 2, -1, 1)
                .addEntry(EntityType.CREEPER, 1, 2, 1, 5, 0, 2)
                .addEntry(EntityType.SPIDER, 2, 1, 2, 4, 0, 0)
                .register();

        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.LEGEND)
                .withGroups(3)
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
                .withGroups(5)
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

        RaidEnemyRegistry.createReinforcementsFor(RaidDifficulty.APOCALYPSE)
                .withGroups(10)
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

    //Waves

    public static RaidEnemies createWavesFor(RaidDifficulty raidDifficulty)
    {
        RaidEnemies waves = new RaidEnemies();
        waves.raidDifficulty = raidDifficulty;
        waves.waves = new HashMap<>();
        return waves;
    }

    public static RaidWaveEnemies createWavesFor(String raider)
    {
        RaidWaveEnemies spawns = new RaidWaveEnemies();
        spawns.raiderType = raider;
        spawns.defaults = new HashMap<>();
        return spawns;
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

    public static class RaidWaveEnemies
    {
        private String raiderType;
        private Map<RaidDifficulty, int[]> defaults;

        public RaidWaveEnemies withDifficulty(RaidDifficulty difficulty, int[] defaults)
        {
            if(defaults.length != 8) throw new IllegalArgumentException("Raider Default Spawn Count Array must be of length 8!");

            this.defaults.put(difficulty, defaults);
            return this;
        }

        public void register()
        {
            SPAWNS.put(this.raiderType, this);
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
