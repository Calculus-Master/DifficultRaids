package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

import java.util.*;

public class RaidEnemyRegistry
{
    public static final Map<String, RaidWaveEnemies> SPAWNS = new HashMap<>();
    public static final Map<RaidDifficulty, RaidReinforcements> REINFORCEMENTS = new HashMap<>();

    //TODO: Move this to DifficultRaidsConfig
    public static void init()
    {
        //TODO: Look into using Reflection instead
        //ObfuscationReflectionHelper.findField(Raid.RaiderType.class, "f_37815_").set(Raid.RaiderType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5});

        RaidEnemyRegistry.createWavesFor("VINDICATOR")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 2, 0, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 2, 2, 1, 4, 2, 5})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 4, 2, 2, 4, 4, 6})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 4, 4, 2, 6, 5, 10})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 20, 25, 30, 34, 45})
                .register();

        RaidEnemyRegistry.createWavesFor("EVOKER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 1, 1, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 0, 1, 1, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 0, 2, 2, 2, 3, 4})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 2, 4, 4, 5, 5, 8})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 8, 10, 12, 16, 20, 25})
                .register();

        RaidEnemyRegistry.createWavesFor("PILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 4, 3, 3, 4, 4, 4, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 5, 4, 4, 6, 6, 6, 6})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 5, 5, 5, 7, 8, 8, 8})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 5, 3, 6, 7, 7, 12, 13})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 20, 23, 28, 32, 45, 50})
                .register();

        RaidEnemyRegistry.createWavesFor("WITCH")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 3, 0, 0, 1})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 0, 1, 3, 0, 1, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 2, 2, 3, 3, 3, 5})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 4, 4, 4, 2, 2, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 7, 10, 30, 20, 23, 30})
                .register();

        RaidEnemyRegistry.createWavesFor("RAVAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 1, 0, 1, 0, 2})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 1, 0, 2, 0, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 0, 0, 1, 0, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 0, 2, 0, 1, 3, 0, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 5, 8, 8, 10, 10, 20})
                .register();
    }

    public static void registerNewRaiders()
    {
        Raid.RaiderType.create("ILLUSIONER", EntityType.ILLUSIONER, new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("ILLUSIONER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 1, 2, 0, 1, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 1, 2, 2, 3, 2, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 1, 2, 2, 4, 3, 5})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 8, 10, 11, 15, 18, 22})
                .register();

        Raid.RaiderType.create("WARRIOR_ILLAGER", DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("WARRIOR_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 2, 4, 2, 3, 4, 3, 8})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 3, 3, 4, 3, 7})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 5, 1, 1, 6, 7, 7})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 16, 16, 25, 25, 30, 40})
                .register();

        Raid.RaiderType.create("DART_ILLAGER", DifficultRaidsEntityTypes.DART_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("DART_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 2, 2, 3})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 1, 1, 3, 4, 5})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 2, 1, 6, 6, 4, 8})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 8, 8, 10, 12, 16, 20, 30})
                .register();

        Raid.RaiderType.create("ELECTRO_ILLAGER", DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("ELECTRO_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 1, 1, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 2, 1, 1, 1, 2, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 0, 3, 1, 5, 2, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 5, 6, 7, 8, 9, 10, 11})
                .register();

        Raid.RaiderType.create("NECROMANCER_ILLAGER", DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("NECROMANCER_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 0, 1, 0, 1, 0, 1, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 1, 1, 1, 0, 2})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 1, 1, 2, 0, 1, 1, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 2, 2, 3, 5, 2, 4, 8})
                .register();

        Raid.RaiderType.create("SHAMAN_ILLAGER", DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("SHAMAN_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 1, 1, 1, 1, 0, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 2, 1, 1, 3, 0, 3, 1})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 2, 4, 2, 0, 4, 2})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 4, 4, 3, 1, 6, 6, 6})
                .register();

        Raid.RaiderType.create("TANK_ILLAGER", DifficultRaidsEntityTypes.TANK_ILLAGER.get(), new int[]{0, 0, 2, 1, 2, 4, 2, 1});

        RaidEnemyRegistry.createWavesFor("TANK_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 2, 3, 2, 1, 1, 3, 2})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 1, 3, 3, 3, 4, 3, 4})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 3, 2, 1, 1, 3, 4, 6})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 10, 16, 16, 25, 25, 30, 40})
                .register();

        Raid.RaiderType.create("ASSASSIN_ILLAGER", DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("ASSASSIN_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 1, 1, 1, 1, 1, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 2, 2, 0, 0, 0, 3, 3})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 0, 3, 0, 5, 5, 5})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 6, 3, 1, 6, 6, 6})
                .register();

        Raid.RaiderType.create("FROST_ILLAGER", DifficultRaidsEntityTypes.FROST_ILLAGER.get(), new int[]{0, 0, 0, 0, 0, 0, 0, 0});

        RaidEnemyRegistry.createWavesFor("FROST_ILLAGER")
                .withDifficulty(RaidDifficulty.DEFAULT,     new int[]{0, 0, 0, 0, 0, 0, 0, 0})
                .withDifficulty(RaidDifficulty.HERO,        new int[]{0, 1, 0, 1, 1, 1, 0, 1})
                .withDifficulty(RaidDifficulty.LEGEND,      new int[]{0, 0, 1, 2, 2, 4, 2, 1})
                .withDifficulty(RaidDifficulty.MASTER,      new int[]{0, 2, 1, 3, 2, 1, 3, 3})
                .withDifficulty(RaidDifficulty.APOCALYPSE,  new int[]{0, 6, 6, 6, 4, 6, 6, 6})
                .register();
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

    public static int[] getDefaultSpawns(String raiderType, RaidDifficulty difficulty)
    {
        return SPAWNS
                .entrySet()
                .stream()
                .filter(e -> e.getKey().equalsIgnoreCase(raiderType))
                .findFirst().orElseThrow(() -> new IllegalStateException("Missing RaidWaveEnemies entry for RaiderType " + raiderType)).getValue()
                .defaults
                .get(difficulty);
    }

    public static Map<EntityType<?>, Integer> generateReinforcements(int waves, RaidDifficulty raidDifficulty, Difficulty levelDifficulty)
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

    //Waves

    public static RaidWaveEnemies createWavesFor(String raider)
    {
        RaidWaveEnemies spawns = new RaidWaveEnemies();
        spawns.raiderType = raider;
        spawns.defaults = new HashMap<>();
        return spawns;
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
}
