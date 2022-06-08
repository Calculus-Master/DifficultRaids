package com.calculusmaster.difficultraids.raids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;

public class RaidWaves
{
    //TODO: Complete or rework idea
    public static final Map<RaidDifficulty, WaveHolder> WAVES = new LinkedHashMap<>();

    private static final Random random = new Random();

    //Raider Types
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

    public static final String HUNTER = "HUNTERILLAGER";
    public static final String ENCHANTER = "ENCHANTER";

    //Load wave data
    public static void load()
    {

    }

    //Hero -> Very similar to vanilla raids, but with slightly more enemies and
    private static void registerDefaultHero()
    {
        WaveHolder holder = new WaveHolder();

        holder.addWaveSet( //Wave 1
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 2), P.of(WARRIOR, 2)),
                Wave.create(P.of(PILLAGER, 3), P.of(VINDICATOR, 1), P.of(WARRIOR, 1), P.of(HUNTER, 1)),
                Wave.create(P.of(PILLAGER, 5), P.of(VINDICATOR, 1), P.of(TANK, 1)),
                Wave.create(P.of(PILLAGER, 5), P.of(WARRIOR, 1), P.of(TANK, 1))
        );

        holder.addWaveSet( //Wave 2
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 3), P.of(WARRIOR, 2), P.of(WITCH, 1)),
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 2), P.of(WARRIOR, 3), P.of(WITCH, 1)),
                Wave.create(P.of(PILLAGER, 6), P.of(TANK, 2), P.of(WITCH, 1)),
                Wave.create(P.of(PILLAGER, 4), P.of(TANK, 1), P.of(HUNTER, 2))
        );

        holder.addWaveSet( //Wave 3
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 3), P.of(WARRIOR, 3), P.of(WITCH, 2)),
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 3), P.of(WARRIOR, 3), P.of(EVOKER, 1), P.of(TANK, 1)),
                Wave.create(P.of(PILLAGER, 4), P.of(VINDICATOR, 1), P.of(WARRIOR, 1), P.of(TANK, 4), P.of(EVOKER, 1)),
                Wave.create(P.of(PILLAGER, 3), P.of(VINDICATOR, 3), P.of(WARRIOR, 3), P.of(WITCH, 2), P.of(HUNTER, 1))
        );

        holder.addWaveSet( //Wave 4
                List.of(P.of(TANK, 1)),
                Wave.create(P.of(PILLAGER, 5), P.of(VINDICATOR, 3), P.of(WARRIOR, 3), P.of(WITCH, 2), P.of(EVOKER, 1), P.of(HUNTER, 1)),
                Wave.create(P.of(PILLAGER, 5), P.of(VINDICATOR, 3), P.of(WARRIOR, 3), P.of(WITCH, 2), P.of(RAVAGER, 1), P.of(HUNTER, 1)),
                Wave.create(P.of(PILLAGER, 2), P.of(VINDICATOR, 5), P.of(WARRIOR, 3), P.of(WITCH, 2), P.of(HUNTER, 1)),
                Wave.create(P.of(PILLAGER, 2), P.of(VINDICATOR, 3), P.of(WARRIOR, 5), P.of(WITCH, 2), P.of(HUNTER, 1)),
                Wave.create(P.of(PILLAGER, 1), P.of(VINDICATOR, 2), P.of(WARRIOR, 2), P.of(WITCH, 2), P.of(HUNTER, 5)),
                Wave.create(P.of(RAVAGER, 2), P.of(HUNTER, 2))
        );

        holder.addWaveSet( //Wave 5
                List.of(P.of(TANK, 2), P.of(VINDICATOR, 4), P.of(WARRIOR, 4), P.of(WITCH, 3)),
                Wave.create(P.of(PILLAGER, 7), P.of(HUNTER, 5), P.of(EVOKER, 3)),
                Wave.create(P.of(PILLAGER, 6), P.of(HUNTER, 4), P.of(EVOKER, 1), P.of(RAVAGER, 1)),
                Wave.create(P.of(PILLAGER, 7), P.of(HUNTER, 4), P.of(EVOKER, 3), P.of(ILLUSIONER, 1)),
                Wave.create(P.of(PILLAGER, 6), P.of(HUNTER, 4), P.of(EVOKER, 1), P.of(RAVAGER, 1), P.of(ILLUSIONER, 1))
        );

        WAVES.put(RaidDifficulty.HERO, holder);
    }

    private static void registerDefaultLegend()
    {
        WaveHolder holder = new WaveHolder();

        WAVES.put(RaidDifficulty.LEGEND, holder);
    }

    private static void registerDefaultMaster()
    {
        WaveHolder holder = new WaveHolder();

        WAVES.put(RaidDifficulty.MASTER, holder);
    }

    private static void registerDefaultApocalypse()
    {
        WaveHolder holder = new WaveHolder();

        WAVES.put(RaidDifficulty.GRANDMASTER, holder);
    }

    //Data storage for waves for each RaidDifficulty
    public static class WaveHolder
    {
        private final List<WaveOptions> waveData;

        //First entry will just be empty so the indices refer to wave number
        WaveHolder() { this.waveData = new ArrayList<>(List.of(WaveOptions.EMPTY)); }

        void addWaveSet(Wave... waves)
        {
            this.waveData.add(WaveOptions.withWaves(waves));
        }

        void addWaveSet(List<P> fixed, Wave... waves)
        {
            for(Wave w : waves) w.add(fixed);
            this.addWaveSet(waves);
        }

        void setLatestAsEliteWithTier(int tier)
        {
            this.waveData.get(this.waveData.size() - 1).setEliteTier(tier);
        }
    }

    //All the possible waves per slot
    public static class WaveOptions
    {
        private static final WaveOptions EMPTY = new WaveOptions();

        private final List<Wave> waves;
        private int eliteTier;

        WaveOptions() { this.waves = new ArrayList<>(); this.eliteTier = 0; }

        static WaveOptions withWaves(Wave... waves)
        {
            WaveOptions waveOptions = new WaveOptions();
            waveOptions.waves.addAll(List.of(waves));
            return waveOptions;
        }

        public Wave pickRandom()
        {
            List<Wave> pool = new ArrayList<>(List.copyOf(this.waves));

            if(DifficultRaidsUtil.isHunterIllagerLoaded()) pool.removeIf(w -> w.raiders.containsKey(HUNTER));
            if(DifficultRaidsUtil.isEnchantWithMobLoaded()) pool.removeIf(w -> w.raiders.containsKey(ENCHANTER));

            return pool.get(random.nextInt(pool.size()));
        }

        void setEliteTier(int tier) { this.eliteTier = tier; }

        public EntityType<? extends AbstractIllagerVariant> pickElite()
        {
            List<RegistryObject<? extends EntityType<? extends AbstractIllagerVariant>>> pool = this.eliteTier == 1 ? List.of(DifficultRaidsEntityTypes.NUAOS_ELITE, DifficultRaidsEntityTypes.XYDRAX_ELITE) :
                    this.eliteTier == 2 ? List.of(DifficultRaidsEntityTypes.MODUR_ELITE, DifficultRaidsEntityTypes.VOLDON_ELITE) : List.of();

            if(pool.isEmpty()) return null;
            else return pool.get(random.nextInt(pool.size())).get();
        }
    }

    //Individual wave data
    public static class Wave
    {
        private final Map<String, Integer> raiders;

        Wave() { this.raiders = new HashMap<>(); }

        static Wave create(P... raiderPairs)
        {
            Wave wave = new Wave();
            for(P raiderPair : raiderPairs) wave.raiders.put(raiderPair.type, raiderPair.count);
            return wave;
        }

        void add(List<P> pairs)
        {
            pairs.forEach(p -> this.raiders.put(p.type, p.count));
        }

        void remove(String raiderType)
        {
            this.raiders.remove(raiderType);
        }
    }

    //Record version of Tuple<A, B> because its shorter lel
    private record P(String type, int count)
    {
        static P of(String type, int count)
        {
            return new P(type, count);
        }
    }
}