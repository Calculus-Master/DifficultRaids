package com.calculusmaster.difficultraids.config;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class RaiderConfigs
{
    public static class Vindicator extends RaiderConfig
    {
        public static final TagKey<Item> TAG_AXES_HERO = ItemTags.create(new ResourceLocation("difficultraids:vindicator/axes_hero"));
        public static final TagKey<Item> TAG_AXES_LEGEND = ItemTags.create(new ResourceLocation("difficultraids:vindicator/axes_legend"));
        public static final TagKey<Item> TAG_AXES_MASTER = ItemTags.create(new ResourceLocation("difficultraids:vindicator/axes_master"));
        public static final TagKey<Item> TAG_AXES_GRANDMASTER = ItemTags.create(new ResourceLocation("difficultraids:vindicator/axes_grandmaster"));

        private final ForgeConfigSpec.IntValue sharpnessLevel_config;
        public int sharpnessLevel;

        private final ForgeConfigSpec.IntValue criticalBurstLevel_config;
        public int criticalBurstLevel;

        private final ForgeConfigSpec.IntValue criticalStrikeLevel_config;
        public int criticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue axeDropChance_config;
        public float axeDropChance;

        public Vindicator(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.sharpnessLevel_config = spec
                    .comment("Level of Sharpness that Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("vindicator_sharpnessLevel", switch(rd)
                    {
                        case DEFAULT -> 0;
                        case HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.criticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("vindicator_criticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 6;
                    }, 0, Integer.MAX_VALUE);

            this.criticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("vindicator_criticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT -> 0;
                        case HERO, LEGEND -> 1;
                        case MASTER, GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.axeDropChance_config = spec
                    .comment("Chance that a Vindicator will drop their axe upon death. 0 to disable.")
                    .defineInRange("vindicator_axeDropChance", 0., 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.sharpnessLevel = this.sharpnessLevel_config.get();
            this.criticalBurstLevel = this.criticalBurstLevel_config.get();
            this.criticalStrikeLevel = this.criticalStrikeLevel_config.get();
            this.axeDropChance = this.axeDropChance_config.get().floatValue();
        }

        public Item getAxe()
        {
            return ForgeRegistries.ITEMS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_AXES_HERO;
                case LEGEND -> TAG_AXES_LEGEND;
                case MASTER -> TAG_AXES_MASTER;
                case GRANDMASTER -> TAG_AXES_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(Items.IRON_AXE);
        }
    }

    public static class Evoker extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue evokerFangDamage_config;
        public float evokerFangDamage;

        public Evoker(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.evokerFangDamage_config = spec
                    .comment("Damage dealt by Evoker Fangs.")
                    .defineInRange("evoker_fangDamage", switch(rd)
                    {
                        case DEFAULT -> 6.0F;
                        case HERO -> 7.0F;
                        case LEGEND -> 9.0F;
                        case MASTER -> 13.0F;
                        case GRANDMASTER -> 18.0F;
                    }, 0., Double.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.evokerFangDamage = this.evokerFangDamage_config.get().floatValue();
        }
    }

    public static class Pillager extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue powerLevel_config;
        public int powerLevel;

        private final ForgeConfigSpec.IntValue quickChargeLevel_config;
        public int quickChargeLevel;

        private final ForgeConfigSpec.DoubleValue multishotChance_config;
        public float multishotChance;

        private final ForgeConfigSpec.DoubleValue crossbowDropChance_config;
        public float crossbowDropChance;

        public Pillager(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.powerLevel_config = spec
                    .comment("Level of Power that Pillager crossbows will be enchanted with. 0 to disable.")
                    .defineInRange("pillager_powerLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.quickChargeLevel_config = spec
                    .comment("Level of Quick Charge that Pillager crossbows will be enchanted with. 0 to disable.")
                    .defineInRange("pillager_quickChargeLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.multishotChance_config = spec
                    .comment("Chance that a Pillager's crossbow will be enchanted with Multishot. 0 to disable.")
                    .defineInRange("pillager_multishotChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.25F;
                        case LEGEND -> 0.33F;
                        case MASTER -> 0.5F;
                        case GRANDMASTER -> 1.0F;
                    }, 0., 1.);

            this.crossbowDropChance_config = spec
                    .comment("Chance that a Pillager will drop their crossbow upon death. 0 to disable.")
                    .defineInRange("pillager_crossbowDropChance", 0., 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.powerLevel = this.powerLevel_config.get();
            this.quickChargeLevel = this.quickChargeLevel_config.get();
            this.multishotChance = this.multishotChance_config.get().floatValue();
            this.crossbowDropChance = this.crossbowDropChance_config.get().floatValue();
        }
    }

    public static class Ravager extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue damageMultiplier_config;
        public float damageMultiplier;

        private final ForgeConfigSpec.DoubleValue speedMultiplier_config;
        public float speedMultiplier;

        public Ravager(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.damageMultiplier_config = spec
                    .comment("Damage dealt by Ravagers. The base damage will be multiplied by this value.")
                    .defineInRange("ravager_damageMultiplier", switch(rd)
                    {
                        case DEFAULT, HERO -> 1.05F;
                        case LEGEND -> 1.25F;
                        case MASTER -> 1.5F;
                        case GRANDMASTER -> 2.0F;
                    }, 0., Double.MAX_VALUE);

            this.speedMultiplier_config = spec
                    .comment("Speed of Ravagers. The base speed will be multiplied by this value.")
                    .defineInRange("ravager_speedMultiplier", switch(rd)
                    {
                        case DEFAULT, HERO -> 1.05F;
                        case LEGEND -> 1.15F;
                        case MASTER -> 1.25F;
                        case GRANDMASTER -> 1.5F;
                    }, 0., Double.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.damageMultiplier = this.damageMultiplier_config.get().floatValue();
            this.speedMultiplier = this.speedMultiplier_config.get().floatValue();
        }
    }

    public static class Warrior extends RaiderConfig
    {
        public static final TagKey<Item> TAG_SWORDS_HERO = ItemTags.create(new ResourceLocation("difficultraids:warrior/swords_hero"));
        public static final TagKey<Item> TAG_SWORDS_LEGEND = ItemTags.create(new ResourceLocation("difficultraids:warrior/swords_legend"));
        public static final TagKey<Item> TAG_SWORDS_MASTER = ItemTags.create(new ResourceLocation("difficultraids:warrior/swords_master"));
        public static final TagKey<Item> TAG_SWORDS_GRANDMASTER = ItemTags.create(new ResourceLocation("difficultraids:warrior/swords_grandmaster"));

        private final ForgeConfigSpec.IntValue sharpnessLevel_config;
        public int sharpnessLevel;

        private final ForgeConfigSpec.DoubleValue fireAspectChance_config;
        public float fireAspectChance;

        private final ForgeConfigSpec.IntValue fireAspectLevel_config;
        public int fireAspectLevel;

        private final ForgeConfigSpec.DoubleValue knockbackChance_config;
        public float knockbackChance;

        private final ForgeConfigSpec.IntValue knockbackLevel_config;
        public int knockbackLevel;

        private final ForgeConfigSpec.IntValue criticalStrikeLevel_config;
        public int criticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue swordDropChance_config;
        public float swordDropChance;

        public Warrior(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.sharpnessLevel_config = spec
                    .comment("Level of Sharpness that Warrior swords will be enchanted with. 0 to disable.")
                    .defineInRange("warrior_sharpnessLevel", switch(rd)
                    {
                        case DEFAULT -> 0;
                        case HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.fireAspectChance_config = spec
                    .comment("Chance that a Warrior's sword will be enchanted with Fire Aspect. 0 to disable.")
                    .defineInRange("warrior_fireAspectChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.0F;
                        case LEGEND -> 0.25F;
                        case MASTER -> 0.5F;
                        case GRANDMASTER -> 1.0F;
                    }, 0., 1.);

            this.fireAspectLevel_config = spec
                    .comment("Level of Fire Aspect that Warrior swords will be enchanted with. 0 to disable.")
                    .defineInRange("warrior_fireAspectLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.knockbackChance_config = spec
                    .comment("Chance that a Warrior's sword will be enchanted with Knockback. 0 to disable.")
                    .defineInRange("warrior_knockbackChance", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0.0F;
                        case MASTER -> 0.2F;
                        case GRANDMASTER -> 1.0F;
                    }, 0., 1.);

            this.knockbackLevel_config = spec
                    .comment("Level of Knockback that Warrior swords will be enchanted with. 0 to disable.")
                    .defineInRange("warrior_knockbackLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.criticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Warrior swords will be enchanted with. 0 to disable.")
                    .defineInRange("warrior_criticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER -> 2;
                        case GRANDMASTER -> 4;
                    }, 0, Integer.MAX_VALUE);

            this.swordDropChance_config = spec
                    .comment("Chance that a Warrior will drop their sword. 0 to disable.")
                    .defineInRange("warrior_swordDropChance", 0., 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.sharpnessLevel = this.sharpnessLevel_config.get();
            this.fireAspectChance = this.fireAspectChance_config.get().floatValue();
            this.fireAspectLevel = this.fireAspectLevel_config.get();
            this.knockbackChance = this.knockbackChance_config.get().floatValue();
            this.knockbackLevel = this.knockbackLevel_config.get();
            this.criticalStrikeLevel = this.criticalStrikeLevel_config.get();
            this.swordDropChance = this.swordDropChance_config.get().floatValue();
        }

        public Item getSword()
        {
            return ForgeRegistries.ITEMS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_SWORDS_HERO;
                case LEGEND -> TAG_SWORDS_LEGEND;
                case MASTER -> TAG_SWORDS_MASTER;
                case GRANDMASTER -> TAG_SWORDS_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(Items.IRON_SWORD);
        }
    }

    public static class Dart extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue sharpnessLevel_config;
        public int sharpnessLevel;

        private final ForgeConfigSpec.IntValue knockbackLevel_config;
        public int knockbackLevel;

        private final ForgeConfigSpec.DoubleValue swordDropChance_config;
        public float swordDropChance;

        public Dart(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.sharpnessLevel_config = spec
                    .comment("Level of Sharpness that Dart swords will be enchanted with. 0 to disable.")
                    .defineInRange("dart_sharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 4;
                    }, 0, Integer.MAX_VALUE);

            this.knockbackLevel_config = spec
                    .comment("Level of Knockback that Dart swords will be enchanted with. 0 to disable.")
                    .defineInRange("dart_knockbackLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.swordDropChance_config = spec
                    .comment("Chance that a Dart will drop their sword. 0 to disable.")
                    .defineInRange("dart_swordDropChance", 0.1, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.sharpnessLevel = this.sharpnessLevel_config.get();
            this.knockbackLevel = this.knockbackLevel_config.get();
            this.swordDropChance = this.swordDropChance_config.get().floatValue();
        }
    }

    public static class Conductor extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue concentratedBoltDamage_config;
        public float concentratedBoltDamage;

        private final ForgeConfigSpec.IntValue genericLightningStrikeCount_config;
        public int genericLightningStrikeCount;

        private final ForgeConfigSpec.DoubleValue genericLightningDamage_config;
        public float genericLightningDamage;

        private final ForgeConfigSpec.BooleanValue ringExtraBolts_config;
        public boolean ringExtraBolts;

        private final ForgeConfigSpec.DoubleValue ringLightningDamage_config;
        public float ringLightningDamage;

        public Conductor(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.concentratedBoltDamage_config = spec
                    .comment("Damage dealt by a Conductor's Concentrated Bolt attack.")
                    .defineInRange("conductor_concentratedBoltDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 19.0F;
                        case LEGEND -> 20.0F;
                        case MASTER -> 24.0F;
                        case GRANDMASTER -> 30.0F;
                    }, 0., Double.MAX_VALUE);

            this.genericLightningStrikeCount_config = spec
                    .comment("Number of lightning strikes that a Conductor will summon when using the Generic Lightning attack.")
                    .defineInRange("conductor_genericLightningStrikeCount", switch(rd)
                    {
                        case DEFAULT, HERO -> 4;
                        case LEGEND -> 6;
                        case MASTER -> 8;
                        case GRANDMASTER -> 10;
                    }, 0, Integer.MAX_VALUE);

            this.genericLightningDamage_config = spec
                    .comment("Damage dealt by a Conductor's Generic Lightning attack.")
                    .defineInRange("conductor_genericLightningDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 8.0F;
                        case LEGEND -> 10.0F;
                        case MASTER -> 12.0F;
                        case GRANDMASTER -> 15.0F;
                    }, 0., Double.MAX_VALUE);

            this.ringExtraBolts_config = spec
                    .comment("Whether Conductors will summon an extra ring of lightning bolts when using their Ring Lightning attack.")
                    .define("conductor_ringExtraBolts", rd.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER));

            this.ringLightningDamage_config = spec
                    .comment("Damage dealt by each lightning bolt in a Conductor's Ring Lightning attack.")
                    .defineInRange("conductor_ringLightningDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 2.0F;
                        case LEGEND -> 5.0F;
                        case MASTER -> 7.0F;
                        case GRANDMASTER -> 10.0F;
                    }, 0., Double.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.concentratedBoltDamage = this.concentratedBoltDamage_config.get().floatValue();
            this.genericLightningStrikeCount = this.genericLightningStrikeCount_config.get();
            this.genericLightningDamage = this.genericLightningDamage_config.get().floatValue();
            this.ringExtraBolts = this.ringExtraBolts_config.get();
            this.ringLightningDamage = this.ringLightningDamage_config.get().floatValue();
        }
    }

    public static class Necromancer extends RaiderConfig
    {
        public static final TagKey<EntityType<?>> TAG_MINION_TYPES_HERO = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("difficultraids:necromancer/minion_types_hero"));
        public static final TagKey<EntityType<?>> TAG_MINION_TYPES_LEGEND = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("difficultraids:necromancer/minion_types_legend"));
        public static final TagKey<EntityType<?>> TAG_MINION_TYPES_MASTER = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("difficultraids:necromancer/minion_types_master"));
        public static final TagKey<EntityType<?>> TAG_MINION_TYPES_GRANDMASTER = TagKey.create(Registry.ENTITY_TYPE_REGISTRY, new ResourceLocation("difficultraids:necromancer/minion_types_grandmaster"));

        private final ForgeConfigSpec.DoubleValue reflectedDamagePercentage_config;
        public float reflectedDamagePercentage;

        private final ForgeConfigSpec.IntValue minionChargeSummonCount_config;
        public int minionChargeSummonCount;

        private final ForgeConfigSpec.IntValue minionMaxProtectionLevel_config;
        public int minionMaxProtectionLevel;

        private final ForgeConfigSpec.IntValue hordeSize_config;
        public int hordeSize;

        private final ForgeConfigSpec.IntValue hordeLifetime_config;
        public int hordeLifetime;

        public Necromancer(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.reflectedDamagePercentage_config = spec
                    .comment("Percentage of damage taken that Necromancers will occasionally reflect onto alive minions.")
                    .defineInRange("necromancer_reflectedDamagePercentage", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0.4F;
                        case MASTER -> 0.5F;
                        case GRANDMASTER -> 0.75F;
                    }, 0., 1.);

            this.minionChargeSummonCount_config = spec
                    .comment("Number of minions that Necromancers will summon at a time.")
                    .defineInRange("necromancer_minionChargeSummonCount", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.minionMaxProtectionLevel_config = spec
                    .comment("Maximum level of Protection that Necromancer minions' armor will be enchanted with.")
                    .defineInRange("necromancer_minionMaxProtectionLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.hordeSize_config = spec
                    .comment("Number of skeletons that will spawn in a horde.")
                    .defineInRange("necromancer_hordeSize", switch(rd)
                    {
                        case DEFAULT, HERO -> 10;
                        case LEGEND -> 15;
                        case MASTER -> 20;
                        case GRANDMASTER -> 30;
                    }, 0, Integer.MAX_VALUE);

            this.hordeLifetime_config = spec
                    .comment("Number of ticks that a horde will remain alive for.")
                    .defineInRange("necromancer_hordeLifetime", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 15;
                        case LEGEND -> 20 * 20;
                        case MASTER -> 20 * 30;
                        case GRANDMASTER -> 20 * 45;
                    }, 0, Integer.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.reflectedDamagePercentage = this.reflectedDamagePercentage_config.get().floatValue();
            this.minionChargeSummonCount = this.minionChargeSummonCount_config.get();
            this.minionMaxProtectionLevel = this.minionMaxProtectionLevel_config.get();
            this.hordeSize = this.hordeSize_config.get();
            this.hordeLifetime = this.hordeLifetime_config.get();
        }

        public EntityType<?> getMinionType()
        {
            return ForgeRegistries.ENTITY_TYPES.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_MINION_TYPES_HERO;
                case LEGEND -> TAG_MINION_TYPES_LEGEND;
                case MASTER -> TAG_MINION_TYPES_MASTER;
                case GRANDMASTER -> TAG_MINION_TYPES_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(EntityType.ZOMBIE);
        }
    }

    public static class Shaman extends RaiderConfig
    {
        public static final TagKey<MobEffect> TAG_EFFECTS_HERO = ForgeRegistries.MOB_EFFECTS.tags().createTagKey(new ResourceLocation("difficultraids:shaman/effects_hero"));
        public static final TagKey<MobEffect> TAG_EFFECTS_LEGEND = ForgeRegistries.MOB_EFFECTS.tags().createTagKey(new ResourceLocation("difficultraids:shaman/effects_legend"));
        public static final TagKey<MobEffect> TAG_EFFECTS_MASTER = ForgeRegistries.MOB_EFFECTS.tags().createTagKey(new ResourceLocation("difficultraids:shaman/effects_master"));
        public static final TagKey<MobEffect> TAG_EFFECTS_GRANDMASTER = ForgeRegistries.MOB_EFFECTS.tags().createTagKey(new ResourceLocation("difficultraids:shaman/effects_grandmaster"));

        private final ForgeConfigSpec.IntValue invisibilityDuration_config;
        public int invisibilityDuration;

        private final ForgeConfigSpec.IntValue maxDebuffCount_config;
        public int maxDebuffCount;

        private final ForgeConfigSpec.DoubleValue additionalDebuffChance_config;
        public float additionalDebuffChance;

        private final ForgeConfigSpec.IntValue debuffDuration_config;
        public int debuffDuration;

        private final ForgeConfigSpec.IntValue debuffAmplifier_config;
        public int debuffAmplifier;

        private final ForgeConfigSpec.DoubleValue allyBuffRadius_config;
        public float allyBuffRadius;

        private final ForgeConfigSpec.IntValue allyResistanceDuration_config;
        public int allyResistanceDuration;

        private final ForgeConfigSpec.IntValue allyResistanceAmplifier_config;
        public int allyResistanceAmplifier;

        private final ForgeConfigSpec.IntValue allyStrengthDuration_config;
        public int allyStrengthDuration;

        private final ForgeConfigSpec.IntValue allyStrengthAmplifier_config;
        public int allyStrengthAmplifier;

        public Shaman(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.invisibilityDuration_config = spec
                    .comment("Duration (in ticks) of Invisibility Shamans can apply to themselves.")
                    .defineInRange("shaman_invisibilityDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 60;
                        case LEGEND -> 80;
                        case MASTER -> 100;
                        case GRANDMASTER -> 120;
                    }, 0, Integer.MAX_VALUE);

            this.maxDebuffCount_config = spec
                    .comment("Maximum number of debuff effects Shamans can apply to players with each attack.")
                    .defineInRange("shaman_debuffCount", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.additionalDebuffChance_config = spec
                    .comment("Chance (from 0 to 1) of Shamans applying an additional debuff effect to players with each attack.")
                    .comment("If successful, this chance will be checked again to add another debuff, up to the specified maximum.")
                    .defineInRange("shaman_additionalDebuffChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.2F;
                        case LEGEND -> 0.3F;
                        case MASTER -> 0.5F;
                        case GRANDMASTER -> 0.75F;
                    }, 0., 1.);

            this.debuffDuration_config = spec
                    .comment("Duration (in ticks) of the random debuff effect Shamans can apply to players.")
                    .defineInRange("shaman_debuffDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 80;
                        case LEGEND -> 100;
                        case MASTER -> 120;
                        case GRANDMASTER -> 200;
                    }, 0, Integer.MAX_VALUE);

            this.debuffAmplifier_config = spec
                    .comment("Amplifier of the random debuff effect Shamans can apply to players.")
                    .defineInRange("shaman_debuffAmplifier", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER -> 1;
                        case GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.allyBuffRadius_config = spec
                    .comment("Radius (in blocks) of the Resistance and Strength effects Shamans can apply to ally Raiders.")
                    .defineInRange("shaman_allyBuffRadius", switch(rd)
                    {
                        case DEFAULT, HERO -> 8.;
                        case LEGEND -> 10.;
                        case MASTER -> 12.;
                        case GRANDMASTER -> 16.;
                    }, 0, Double.MAX_VALUE);

            this.allyResistanceDuration_config = spec
                    .comment("Duration (in ticks) of the Resistance effect Shamans can apply to ally Raiders.")
                    .defineInRange("shaman_allyResistanceDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 140;
                        case LEGEND -> 180;
                        case MASTER -> 220;
                        case GRANDMASTER -> 300;
                    }, 0, Integer.MAX_VALUE);

            this.allyResistanceAmplifier_config = spec
                    .comment("Amplifier of the Resistance effect Shamans can apply to ally Raiders.")
                    .defineInRange("shaman_allyResistanceAmplifier", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER, GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.allyStrengthDuration_config = spec
                    .comment("Duration (in ticks) of the Strength effect Shamans can apply to ally Raiders.")
                    .defineInRange("shaman_allyStrengthDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 140;
                        case LEGEND -> 180;
                        case MASTER -> 220;
                        case GRANDMASTER -> 300;
                    }, 0, Integer.MAX_VALUE);

            this.allyStrengthAmplifier_config = spec
                    .comment("Amplifier of the Strength effect Shamans can apply to ally Raiders.")
                    .defineInRange("shaman_allyStrengthAmplifier", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.invisibilityDuration = this.invisibilityDuration_config.get();
            this.maxDebuffCount = this.maxDebuffCount_config.get();
            this.additionalDebuffChance = this.additionalDebuffChance_config.get().floatValue();
            this.debuffDuration = this.debuffDuration_config.get();
            this.debuffAmplifier = this.debuffAmplifier_config.get();
            this.allyBuffRadius = this.allyBuffRadius_config.get().floatValue();
            this.allyResistanceDuration = this.allyResistanceDuration_config.get();
            this.allyResistanceAmplifier = this.allyResistanceAmplifier_config.get();
            this.allyStrengthDuration = this.allyStrengthDuration_config.get();
            this.allyStrengthAmplifier = this.allyStrengthAmplifier_config.get();
        }

        public List<MobEffect> getEffectPool()
        {
            return ForgeRegistries.MOB_EFFECTS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_EFFECTS_HERO;
                case LEGEND -> TAG_EFFECTS_LEGEND;
                case MASTER -> TAG_EFFECTS_MASTER;
                case GRANDMASTER -> TAG_EFFECTS_GRANDMASTER;
            }).stream().toList();
        }
    }

    public static class Tank extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue extraArmor_config;
        public float extraArmor;

        private final ForgeConfigSpec.DoubleValue extraArmorToughness_config;
        public float extraArmorToughness;

        private final ForgeConfigSpec.DoubleValue thornsChance_config;
        public float thornsChance;

        public Tank(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.extraArmor_config = spec
                    .comment("Extra armor points Tanks receive.")
                    .defineInRange("tank_extraArmor", switch(rd)
                    {
                        case DEFAULT -> 7.5;
                        case HERO -> 11.0;
                        case LEGEND -> 15.0;
                        case MASTER, GRANDMASTER -> 20.0;
                    }, 0., Double.MAX_VALUE);

            this.extraArmorToughness_config = spec
                    .comment("Extra armor toughness Tanks receive.")
                    .defineInRange("tank_extraArmorToughness", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND, MASTER -> 0.0;
                        case GRANDMASTER -> 15.0;
                    }, 0., Double.MAX_VALUE);

            this.thornsChance_config = spec
                    .comment("Chance for Tanks to spawn with armor enchanted with Thorns.")
                    .defineInRange("tank_thornsChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.0;
                        case LEGEND -> 0.1;
                        case MASTER -> 0.3;
                        case GRANDMASTER -> 0.8;
                    }, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.extraArmor = this.extraArmor_config.get().floatValue();
            this.extraArmorToughness = this.extraArmorToughness_config.get().floatValue();
            this.thornsChance = this.thornsChance_config.get().floatValue();
        }
    }

    public static class Assassin extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue invisibilityCooldown_config;
        public int invisibilityCooldown;

        private final ForgeConfigSpec.IntValue teleportCooldown_config;
        public int teleportCooldown;

        public Assassin(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.invisibilityCooldown_config = spec
                    .comment("Cooldown (in ticks) after an Assassin locks onto a target, before they go invisible again.")
                    .defineInRange("assassin_invisibilityCooldown", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 60 * 3;
                        case LEGEND -> 20 * 60 * 2;
                        case MASTER -> 20 * 60;
                        case GRANDMASTER -> 20 * 30;
                    }, 0, Integer.MAX_VALUE);

            this.teleportCooldown_config = spec
                    .comment("Cooldown (in ticks) after an Assassin teleports to a target, before they can teleport again.")
                    .defineInRange("assassin_teleportCooldown", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 60 * 3;
                        case LEGEND -> 20 * 60 * 2;
                        case MASTER -> 20 * 60;
                        case GRANDMASTER -> 20 * 30;
                    }, 0, Integer.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.invisibilityCooldown = this.invisibilityCooldown_config.get();
            this.teleportCooldown = this.teleportCooldown_config.get();
        }
    }

    public static class Frostmage extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue slownessAuraRadius_config;
        public float slownessAuraRadius;

        private final ForgeConfigSpec.DoubleValue barrageSnowballDamage_config;
        public float barrageSnowballDamage;

        private final ForgeConfigSpec.IntValue barrageDuration_config;
        public int barrageDuration;

        private final ForgeConfigSpec.DoubleValue snowballBlastDamage_config;
        public float snowballBlastDamage;

        private final ForgeConfigSpec.IntValue freezeDuration_config;
        public int freezeDuration;

        public Frostmage(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.slownessAuraRadius_config = spec
                    .comment("Radius of the aura around Frostmages in which enemies will be slowed.")
                    .defineInRange("frostmage_slownessAuraRadius", switch(rd)
                    {
                        case DEFAULT, HERO -> 4.0;
                        case LEGEND -> 4.5;
                        case MASTER -> 5.0;
                        case GRANDMASTER -> 6.0;
                    }, 0., Double.MAX_VALUE);

            this.barrageSnowballDamage_config = spec
                    .comment("Damage dealt by each snowball in the Frostmages' Barrage attack.")
                    .defineInRange("frostmage_barrageSnowballDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 1.8F;
                        case LEGEND -> 2.2F;
                        case MASTER -> 2.8F;
                        case GRANDMASTER -> 3.0F;
                    }, 0., Double.MAX_VALUE);

            this.barrageDuration_config = spec
                    .comment("Duration (in ticks) of the Frostmages' Barrage attack.")
                    .defineInRange("frostmage_barrageDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 2;
                        case LEGEND -> 20 * 3;
                        case MASTER -> 20 * 6;
                        case GRANDMASTER -> 20 * 9;
                    }, 0, Integer.MAX_VALUE);

            this.snowballBlastDamage_config = spec
                    .comment("Damage dealt by each snowball in the Frostmages' Snowball Blast attack.")
                    .defineInRange("frostmage_snowballBlastDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 4.0F;
                        case LEGEND -> 6.5F;
                        case MASTER -> 10.0F;
                        case GRANDMASTER -> 14.0F;
                    }, 0., Double.MAX_VALUE);

            this.freezeDuration_config = spec
                    .comment("Duration (in ticks) of the Frostmages' Freeze attack.")
                    .defineInRange("frostmage_freezeDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 2;
                        case LEGEND -> 20 * 3;
                        case MASTER -> 20 * 4;
                        case GRANDMASTER -> 20 * 5;
                    }, 0, Integer.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.slownessAuraRadius = this.slownessAuraRadius_config.get().floatValue();
            this.barrageSnowballDamage = this.barrageSnowballDamage_config.get().floatValue();
            this.snowballBlastDamage = this.snowballBlastDamage_config.get().floatValue();
        }
    }

    public static class Nuaos extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue friendlyDamageReduction_config;
        public float friendlyDamageReduction;

        private final ForgeConfigSpec.IntValue sharpnessLevel_config;
        public int sharpnessLevel;

        private final ForgeConfigSpec.IntValue criticalStrikeLevel_config;
        public int criticalStrikeLevel;

        private final ForgeConfigSpec.IntValue criticalBurstLevel_config;
        public int criticalBurstLevel;

        private final ForgeConfigSpec.DoubleValue swordDropChance_config;
        public float swordDropChance;

        private final ForgeConfigSpec.DoubleValue maxChargeDamage_config;
        public float maxChargeDamage;

        private final ForgeConfigSpec.DoubleValue shockwaveRadius_config;
        public float shockwaveRadius;

        private final ForgeConfigSpec.DoubleValue buffAuraRadius_config;
        public float buffAuraRadius;

        private final ForgeConfigSpec.IntValue buffAuraStrengthLevel_config;
        public int buffAuraStrengthLevel;

        private final ForgeConfigSpec.ConfigValue<List<? extends Double>> chargedDamageBoost_config;
        public List<Double> chargedDamageBoost;

        private final ForgeConfigSpec.BooleanValue chargeDecay_config;
        public boolean chargeDecay;

        public Nuaos(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.friendlyDamageReduction_config = spec
                    .comment("Multiplier on the damage Nuaos takes from player-friendly mobs (Iron Golems & Guards). 1 to remove this feature.")
                    .defineInRange("nuaos_friendlyDamageReduction", 0.4, 0., 1.);

            this.sharpnessLevel_config = spec
                    .comment("Level of Sharpness that Nuaos' sword will be enchanted with. 0 to disable.")
                    .defineInRange("nuaos_sharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.criticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Nuaos' sword will be enchanted with. 0 to disable.")
                    .defineInRange("nuaos_criticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER -> 1;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.criticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Nuaos' sword will be enchanted with. 0 to disable.")
                    .defineInRange("nuaos_criticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 5;
                        case LEGEND -> 6;
                        case MASTER -> 8;
                        case GRANDMASTER -> 10;
                    }, 0, Integer.MAX_VALUE);

            this.swordDropChance_config = spec
                    .comment("Chance that Nuaos will drop his sword. 0 to disable.")
                    .defineInRange("nuaos_swordDropChance", 0.25F, 0., 1.);

            this.maxChargeDamage_config = spec
                    .comment("Amount of damage Nuaos can charge before he explodes.")
                    .defineInRange("nuaos_maxChargeDamage", 50.0F, 0., Double.MAX_VALUE);

            this.shockwaveRadius_config = spec
                    .comment("Radius of the shockwave Nuaos creates when he explodes.")
                    .defineInRange("nuaos_shockwaveRadius", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 4.0D;
                        case MASTER -> 5.0D;
                        case GRANDMASTER -> 7.0D;
                    }, 0., Double.MAX_VALUE);

            this.buffAuraRadius_config = spec
                    .comment("Radius of the constant aura Nuaos creates around himself, buffing nearby Raiders.")
                    .defineInRange("nuaos_buffAuraRadius", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 8.0D;
                        case MASTER -> 10.0D;
                        case GRANDMASTER -> 12.0D;
                    }, 0., Double.MAX_VALUE);

            this.buffAuraStrengthLevel_config = spec
                    .comment("Amplifier of Strength that Raiders within Nuaos' aura will receive. 0 to disable.")
                    .defineInRange("nuaos_buffAuraStrengthLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.chargedDamageBoost_config = spec
                    .comment("Damage multiplier that Nuaos receives when he is charged, depending on the charge amount.")
                    .comment("The list is in order: [No Charge, Low Charge, High Charge, Max Charge].")
                    .defineList("nuaos_chargedDamageBoost", List.of(1.0, 1.25, 1.75, 2.25), o -> o instanceof Double);

            this.chargeDecay_config = spec
                    .comment("Whether Nuaos' stored charge should decay over time (if Nuaos is not actively taking damage).")
                    .define("nuaos_chargeDecay", true);
        }

        @Override
        public void initialize()
        {
            this.friendlyDamageReduction = this.friendlyDamageReduction_config.get().floatValue();
            this.sharpnessLevel = this.sharpnessLevel_config.get();
            this.criticalStrikeLevel = this.criticalStrikeLevel_config.get();
            this.criticalBurstLevel = this.criticalBurstLevel_config.get();
            this.swordDropChance = this.swordDropChance_config.get().floatValue();
            this.maxChargeDamage = this.maxChargeDamage_config.get().floatValue();
            this.shockwaveRadius = this.shockwaveRadius_config.get().floatValue();
            this.buffAuraRadius = this.buffAuraRadius_config.get().floatValue();
            this.buffAuraStrengthLevel = this.buffAuraStrengthLevel_config.get();

            List<? extends Double> chargedDamageBoostRaw = this.chargedDamageBoost_config.get();
            this.chargedDamageBoost = new ArrayList<>();
            this.chargedDamageBoost.addAll(chargedDamageBoostRaw.size() == 4 ? chargedDamageBoostRaw : this.chargedDamageBoost_config.getDefault());

            this.chargeDecay = this.chargeDecay_config.get();
        }
    }

    public static class Xydrax extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue friendlyDamageReduction_config;
        public float friendlyDamageReduction;

        private final ForgeConfigSpec.IntValue vortexPullInterval_config;
        public int vortexPullInterval;

        private final ForgeConfigSpec.DoubleValue vortexForce_config;
        public float vortexForce;

        private final ForgeConfigSpec.IntValue vortexDamageInterval_config;
        public int vortexDamageInterval;

        private final ForgeConfigSpec.ConfigValue<List<? extends Double>> vortexBaseDamageThresholds_config;
        public List<Double> vortexBaseDamageThresholds;

        private final ForgeConfigSpec.DoubleValue vortexDamageMultiplier_config;
        public float vortexDamageMultiplier;

        private final ForgeConfigSpec.ConfigValue<List<? extends Integer>> windColumnLifetime_config;
        public List<Integer> windColumnLifetime;

        private final ForgeConfigSpec.IntValue vortexLifetime_config;
        public int vortexLifetime;

        private final ForgeConfigSpec.IntValue barrageCurseDuration_config;
        public int barrageCurseDuration;

        private final ForgeConfigSpec.IntValue barrageCurseAmplifier_config;
        public int barrageCurseAmplifier;

        public Xydrax(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.friendlyDamageReduction_config = spec
                    .comment("Multiplier on the damage Xydrax takes from player-friendly mobs (Iron Golems & Guards). 1 to remove this feature.")
                    .defineInRange("xydrax_friendlyDamageReduction", 0.4, 0., 1.);

            this.vortexPullInterval_config = spec
                    .comment("Interval (in ticks) between each pull event of Xydrax's vortex attack.")
                    .defineInRange("xydrax_vortexPullInterval", 20, 1, Integer.MAX_VALUE);

            this.vortexForce_config = spec
                    .comment("Force applied during Xydrax's vortex attack.")
                    .defineInRange("xydrax_vortexForce", switch(rd)
                    {
                        case DEFAULT -> 1.25F;
                        case HERO -> 1.75F;
                        case LEGEND -> 2.5F;
                        case MASTER -> 3.75F;
                        case GRANDMASTER -> 5.0F;
                    }, 0., Double.MAX_VALUE);

            this.vortexDamageInterval_config = spec
                    .comment("Interval (in ticks) between each damage event of Xydrax's vortex attack.")
                    .defineInRange("xydrax_vortexDamageInterval", 40, 1, Integer.MAX_VALUE);

            this.vortexBaseDamageThresholds_config = spec
                    .comment("Base damage dealt by Xydrax's vortex attack at each threshold.")
                    .comment("The list is in this order: [ <1 block, <2 blocks, <5 blocks, >=5 blocks ].")
                    .defineList("xydrax_vortexDamageThresholds", List.of(17.5, 10.0, 4.0, 1.0), o -> o instanceof Double);

            this.vortexDamageMultiplier_config = spec
                    .comment("Multiplier on the damage dealt by Xydrax's vortex attack.")
                    .defineInRange("xydrax_vortexDamageMultiplier", switch(rd)
                    {
                        case DEFAULT, HERO -> 1.0F;
                        case LEGEND -> 1.05F;
                        case MASTER -> 1.2F;
                        case GRANDMASTER -> 2.0F;
                    }, 0., Double.MAX_VALUE);

            this.windColumnLifetime_config = spec
                    .comment("Lifetime of one of Xydrax's wind columns (in ticks).")
                    .comment("The first element is the minimum life, the second element is the maximum life. The actual lifetime will be randomly selected between the two values when summoned.")
                    .defineList("xydrax_windColumnLifetime", switch(rd)
                    {
                        case DEFAULT -> List.of(20 * 5, 20 * 15);
                        case HERO -> List.of(20 * 7, 20 * 17);
                        case LEGEND -> List.of(20 * 9, 20 * 17);
                        case MASTER -> List.of(20 * 10, 20 * 20);
                        case GRANDMASTER -> List.of(20 * 15, 20 * 25);
                    }, o -> o instanceof Integer);

            this.vortexLifetime_config = spec
                    .comment("Lifetime of Xydrax's vortex (in ticks).")
                    .defineInRange("xydrax_vortexLifetime", switch(rd)
                    {
                        case DEFAULT -> 20 * 12;
                        case HERO, LEGEND -> 20 * 20;
                        case MASTER, GRANDMASTER -> 20 * 30;
                    }, 0, Integer.MAX_VALUE);

            this.barrageCurseDuration_config = spec
                    .comment("Duration (in ticks) of the Curse of the Winds effect applied by Xydrax's barrage attack.")
                    .defineInRange("xydrax_barrageCurseDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 10;
                        case LEGEND -> 20 * 20;
                        case MASTER -> 20 * 30;
                        case GRANDMASTER -> 20 * 40;
                    }, 0, Integer.MAX_VALUE);

            this.barrageCurseAmplifier_config = spec
                    .comment("Amplifier of the Curse of the Winds effect applied by Xydrax's barrage attack.")
                    .defineInRange("xydrax_barrageCurseAmplifier", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 4;
                    }, 0, Integer.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.friendlyDamageReduction = this.friendlyDamageReduction_config.get().floatValue();
            this.vortexPullInterval = this.vortexPullInterval_config.get();
            this.vortexForce = this.vortexForce_config.get().floatValue();
            this.vortexDamageInterval = this.vortexDamageInterval_config.get();

            List<? extends Double> vortexBaseDamageThresholdRaw = this.vortexBaseDamageThresholds_config.get();
            this.vortexBaseDamageThresholds = new ArrayList<>();
            this.vortexBaseDamageThresholds.addAll(vortexBaseDamageThresholdRaw.size() == 4 ? vortexBaseDamageThresholdRaw : this.vortexBaseDamageThresholds_config.getDefault());

            this.vortexDamageMultiplier = this.vortexDamageMultiplier_config.get().floatValue();

            List<? extends Integer> windColumnLifetimeRaw = this.windColumnLifetime_config.get();
            this.windColumnLifetime = new ArrayList<>();
            this.windColumnLifetime.addAll(windColumnLifetimeRaw.size() == 2 && windColumnLifetimeRaw.get(1) > windColumnLifetimeRaw.get(0) ? windColumnLifetimeRaw : this.windColumnLifetime_config.getDefault());

            this.vortexLifetime = this.vortexLifetime_config.get();
            this.barrageCurseDuration = this.barrageCurseDuration_config.get();
            this.barrageCurseAmplifier = this.barrageCurseAmplifier_config.get();
        }
    }

    public static class Modur extends RaiderConfig
    {
        private final ForgeConfigSpec.DoubleValue friendlyDamageReduction_config;
        public float friendlyDamageReduction;

        private final ForgeConfigSpec.DoubleValue projectileDamageReduction_config;
        public float projectileDamageReduction;

        private final ForgeConfigSpec.DoubleValue basicLightningStrikeDamage_config;
        public float basicLightningStrikeDamage;

        private final ForgeConfigSpec.IntValue stormStrikesPerTick_config;
        public int stormStrikesPerTick;

        private final ForgeConfigSpec.DoubleValue stormStrikeDamage_config;
        public float stormStrikeDamage;

        private final ForgeConfigSpec.IntValue stormDuration_config;
        public int stormDuration;

        private final ForgeConfigSpec.DoubleValue stormRadius_config;
        public float stormRadius;

        private final ForgeConfigSpec.IntValue chargedBoltCount_config;
        public int chargedBoltCount;

        private final ForgeConfigSpec.DoubleValue chargedBoltDamage_config;
        public float chargedBoltDamage;

        private final ForgeConfigSpec.DoubleValue homingBoltDamage_config;
        public float homingBoltDamage;

        private final ForgeConfigSpec.IntValue homingBoltTime_config;
        public int homingBoltTime;

        private final ForgeConfigSpec.DoubleValue zapBoltDamage_config;
        public float zapBoltDamage;

        public Modur(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.friendlyDamageReduction_config = spec
                    .comment("Multiplier on the damage Modur takes from player-friendly mobs (Iron Golems & Guards). 1 to remove this feature.")
                    .defineInRange("modur_friendlyDamageReduction", 0.4, 0., 1.);

            this.projectileDamageReduction_config = spec
                    .comment("Multiplier on the damage Modur takes from projectiles. 1 to remove this feature.")
                    .defineInRange("modur_projectileDamageReduction", 0.8, 0., 1.);

            this.basicLightningStrikeDamage_config = spec
                    .comment("Damage dealt by Modur's basic lightning strike ranged attack.")
                    .defineInRange("modur_basicLightningStrikeDamage", switch(rd)
                    {
                        case HERO -> 7.0F;
                        case LEGEND -> 12.0F;
                        case MASTER -> 16.0F;
                        case GRANDMASTER -> 20.0F;
                        default -> 5.0F;
                    }, 0., Double.MAX_VALUE);

            this.stormStrikesPerTick_config = spec
                    .comment("Number of lightning strikes Modur summons per tick during his storm attack.")
                    .comment("Warning: This can be very loud if set too high!")
                    .defineInRange("modur_stormStrikesPerTick", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER, GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.stormStrikeDamage_config = spec
                    .comment("Damage dealt by each lightning strike during Modur's storm attack.")
                    .defineInRange("modur_stormStrikeDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 10.0F;
                        case LEGEND -> 15.0F;
                        case MASTER -> 20.0F;
                        case GRANDMASTER -> 30.0F;
                    }, 0., Double.MAX_VALUE);

            this.stormDuration_config = spec
                    .comment("Duration of Modur's storm attack, in ticks.")
                    .defineInRange("modur_stormDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 7;
                        case LEGEND -> 20 * 12;
                        case MASTER -> 20 * 16;
                        case GRANDMASTER -> 20 * 20;
                    }, 0, Integer.MAX_VALUE);

            this.stormRadius_config = spec
                    .comment("Radius of Modur's storm attack.")
                    .defineInRange("modur_stormRadius", switch(rd)
                    {
                        case DEFAULT, HERO -> 12.0;
                        case LEGEND -> 18.0;
                        case MASTER -> 20.0;
                        case GRANDMASTER -> 25.0;
                    }, 0., Double.MAX_VALUE);

            this.chargedBoltCount_config = spec
                    .comment("Number of lightning strikes that comprise Modur's charged bolt attack.")
                    .comment("Warning: These will all happen simultaneously, so this can be very loud if set too high!")
                    .defineInRange("modur_chargedBoltCount", 10, 0, Integer.MAX_VALUE);

            this.chargedBoltDamage_config = spec
                    .comment("Damage dealt by each lightning strike in Modur's charged bolt attack.")
                    .defineInRange("modur_chargedBoltDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 25.0F;
                        case LEGEND -> 40.0F;
                        case MASTER -> 60.0F;
                        case GRANDMASTER -> 100.0F;
                    }, 0., Double.MAX_VALUE);

            this.homingBoltDamage_config = spec
                    .comment("Damage dealt by lightning strikes during Modur's homing bolt attack.")
                    .defineInRange("modur_homingBoltDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 8.0F;
                        case LEGEND -> 11.0F;
                        case MASTER -> 15.0F;
                        case GRANDMASTER -> 20.0F;
                    }, 0., Double.MAX_VALUE);

            this.homingBoltTime_config = spec
                    .comment("Duration of Modur's homing bolt attack.")
                    .defineInRange("modur_homingBoltTime", switch(rd)
                    {
                        case DEFAULT, HERO -> 10 * 8;
                        case LEGEND -> 10 * 14;
                        case MASTER -> 10 * 20;
                        case GRANDMASTER -> 10 * 30;
                    }, 0, Integer.MAX_VALUE);

            this.zapBoltDamage_config = spec
                    .comment("Damage dealt by lightning strikes during Modur's lightning zap attack.")
                    .defineInRange("modur_zapDamage", switch(rd)
                    {
                        case DEFAULT, HERO -> 12.0F;
                        case LEGEND -> 15.0F;
                        case MASTER -> 18.0F;
                        case GRANDMASTER -> 22.0F;
                    }, 0., Double.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.friendlyDamageReduction = this.friendlyDamageReduction_config.get().floatValue();
            this.projectileDamageReduction = this.projectileDamageReduction_config.get().floatValue();
            this.basicLightningStrikeDamage = this.basicLightningStrikeDamage_config.get().floatValue();
            this.stormStrikesPerTick = this.stormStrikesPerTick_config.get();
            this.stormStrikeDamage = this.stormStrikeDamage_config.get().floatValue();
            this.stormDuration = this.stormDuration_config.get();
            this.stormRadius = this.stormRadius_config.get().floatValue();
            this.chargedBoltCount = this.chargedBoltCount_config.get();
            this.chargedBoltDamage = this.chargedBoltDamage_config.get().floatValue();
            this.homingBoltDamage = this.homingBoltDamage_config.get().floatValue();
            this.homingBoltTime = this.homingBoltTime_config.get();
            this.zapBoltDamage = this.zapBoltDamage_config.get().floatValue();
        }
    }

    public static class Voldon extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue familiarSummonCooldown_config;
        public int familiarSummonCooldown;

        private final ForgeConfigSpec.DoubleValue friendlyDamageReduction_config;
        public float friendlyDamageReduction;

        private final ForgeConfigSpec.IntValue fireballCount_config;
        public int fireballCount;

        private final ForgeConfigSpec.IntValue sacrificeBuffDuration_config;
        public int sacrificeBuffDuration;

        private final ForgeConfigSpec.IntValue familiarSummonCount_config;
        public int familiarSummonCount;

        private final ForgeConfigSpec.BooleanValue familiarWeaknessOnDeath_config;
        public boolean familiarWeaknessOnDeath;

        private final ForgeConfigSpec.DoubleValue familiarAttackDamageMultiplier_config;
        public float familiarAttackDamageMultiplier;

        private final ForgeConfigSpec.DoubleValue familiarHealthMultiplier_config;
        public float familiarHealthMultiplier;

        public Voldon(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.familiarSummonCooldown_config = spec
                    .comment("Cooldown (in ticks) after all Voldon's familiars are dead. Voldon will not summon more familiars until this cooldown is over.")
                    .defineInRange("voldon_familiarSummonCooldown", 20 * 15, 0, Integer.MAX_VALUE);

            this.friendlyDamageReduction_config = spec
                    .comment("Multiplier on the damage Voldon takes from player-friendly mobs (Iron Golems & Guards). 1 to remove this feature.")
                    .defineInRange("voldon_friendlyDamageReduction", 0.4, 0., 1.);

            this.fireballCount_config = spec
                    .comment("Number of fireballs Voldon will shoot at once during his fireball attack.")
                    .defineInRange("voldon_fireballCount", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 4;
                        case MASTER -> 5;
                        case GRANDMASTER -> 6;
                    }, 0, Integer.MAX_VALUE);

            this.sacrificeBuffDuration_config = spec
                    .comment("Duration (in ticks) of the buff Voldon gives himself after sacrificing a familiar.")
                    .defineInRange("voldon_sacrificeBuffDuration", switch(rd)
                    {
                        case DEFAULT, HERO -> 20 * 20;
                        case LEGEND -> 20 * 25;
                        case MASTER -> 20 * 40;
                        case GRANDMASTER -> 20 * 60;
                    }, 0, Integer.MAX_VALUE);

            this.familiarSummonCount_config = spec
                    .comment("Number of familiars Voldon will summon at once.")
                    .defineInRange("voldon_familiarSummonCount", switch(rd)
                    {
                        case DEFAULT, HERO -> 4;
                        case LEGEND -> 6;
                        case MASTER -> 8;
                        case GRANDMASTER -> 12;
                    }, 0, Integer.MAX_VALUE);

            this.familiarWeaknessOnDeath_config = spec
                    .comment("Whether Voldon's familiars will give the entity that killed them the weakness effect.")
                    .define("voldon_familiarWeaknessOnDeath", true);

            this.familiarAttackDamageMultiplier_config = spec
                    .comment("Multiplier on the damage Voldon's familiars deal. 1 to remove this feature.")
                    .defineInRange("voldon_familiarAttackDamageMultiplier", switch(rd)
                    {
                        case DEFAULT, HERO -> 1.25F;
                        case LEGEND -> 1.75F;
                        case MASTER -> 2.4F;
                        case GRANDMASTER -> 3.0F;
                    }, 0., Double.MAX_VALUE);

            this.familiarHealthMultiplier_config = spec
                    .comment("Multiplier on the health Voldon's familiars have. 1 to remove this feature.")
                    .defineInRange("voldon_familiarHealthMultiplier", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1.0F;
                        case MASTER -> 2.0F;
                        case GRANDMASTER -> 2.5F;
                    }, 0.01, Double.MAX_VALUE);
        }

        @Override
        public void initialize()
        {
            this.familiarSummonCooldown = this.familiarSummonCooldown_config.get();
            this.friendlyDamageReduction = this.friendlyDamageReduction_config.get().floatValue();
            this.fireballCount = this.fireballCount_config.get();
            this.sacrificeBuffDuration = this.sacrificeBuffDuration_config.get();
            this.familiarSummonCount = this.familiarSummonCount_config.get();
            this.familiarWeaknessOnDeath = this.familiarWeaknessOnDeath_config.get();
            this.familiarAttackDamageMultiplier = this.familiarAttackDamageMultiplier_config.get().floatValue();
            this.familiarHealthMultiplier = this.familiarHealthMultiplier_config.get().floatValue();
        }
    }

    public static class Hunter extends RaiderConfig
    {
        public static final TagKey<Item> TAG_SWORDS_HERO = ItemTags.create(new ResourceLocation("difficultraids:hunter/swords_hero"));
        public static final TagKey<Item> TAG_SWORDS_LEGEND = ItemTags.create(new ResourceLocation("difficultraids:hunter/swords_legend"));
        public static final TagKey<Item> TAG_SWORDS_MASTER = ItemTags.create(new ResourceLocation("difficultraids:hunter/swords_master"));
        public static final TagKey<Item> TAG_SWORDS_GRANDMASTER = ItemTags.create(new ResourceLocation("difficultraids:hunter/swords_grandmaster"));

        private final ForgeConfigSpec.IntValue bowPowerLevel_config;
        public int bowPowerLevel;

        private final ForgeConfigSpec.IntValue swordSharpnessLevel_config;
        public int swordSharpnessLevel;

        private final ForgeConfigSpec.DoubleValue bowPunchChance_config;
        public float bowPunchChance;

        private final ForgeConfigSpec.IntValue bowPunchLevel_config;
        public int bowPunchLevel;

        private final ForgeConfigSpec.DoubleValue swordKnockbackChance_config;
        public float swordKnockbackChance;

        private final ForgeConfigSpec.IntValue swordKnockbackLevel_config;
        public int swordKnockbackLevel;

        private final ForgeConfigSpec.DoubleValue boomerangLoyaltyChance_config;
        public float boomerangLoyaltyChance;

        private final ForgeConfigSpec.IntValue boomerangSharpnessLevel_config;
        public int boomerangSharpnessLevel;

        private final ForgeConfigSpec.IntValue foodCount_config;
        public int foodCount;

        private final ForgeConfigSpec.DoubleValue foodGoldenAppleChance_config;
        public float foodGoldenAppleChance;

        private final ForgeConfigSpec.DoubleValue mainItemDropChance_config;
        public float mainItemDropChance;

        private final ForgeConfigSpec.DoubleValue boomerangDropChance_config;
        public float boomerangDropChance;

        public Hunter(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.bowPowerLevel_config = spec
                    .comment("Level of Power that Hunter bows will be enchanted with. 0 to disable.")
                    .defineInRange("hunter_bowPowerLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.swordSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Hunter swords will be enchanted with. 0 to disable.")
                    .defineInRange("hunter_swordSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.bowPunchChance_config = spec
                    .comment("Chance that Hunter bows will be enchanted with Punch. 0 to disable.")
                    .defineInRange("hunter_bowPunchChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.25;
                        case LEGEND -> 0.5;
                        case MASTER -> 0.75;
                        case GRANDMASTER -> 1.0;
                    }, 0., 1.);

            this.bowPunchLevel_config = spec
                    .comment("Level of Punch that Hunter bows will be enchanted with. 0 to disable.")
                    .defineInRange("hunter_bowPunchLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.swordKnockbackChance_config = spec
                    .comment("Chance that Hunter swords will be enchanted with Knockback. 0 to disable.")
                    .defineInRange("hunter_swordKnockbackChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.25;
                        case LEGEND -> 0.5;
                        case MASTER -> 0.75;
                        case GRANDMASTER -> 1.0;
                    }, 0., 1.);

            this.swordKnockbackLevel_config = spec
                    .comment("Level of Knockback that Hunter swords will be enchanted with. 0 to disable.")
                    .defineInRange("hunter_swordKnockbackLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.boomerangLoyaltyChance_config = spec
                    .comment("Chance that Hunter boomerangs will be enchanted with Loyalty. 0 to disable.")
                    .defineInRange("hunter_boomerangLoyaltyChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.33;
                        case LEGEND -> 0.45;
                        case MASTER -> 0.75;
                        case GRANDMASTER -> 1.0;
                    }, 0., 1.);

            this.boomerangSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Hunter boomerangs will be enchanted with. 0 to disable.")
                    .defineInRange("hunter_boomerangSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.foodCount_config = spec
                    .comment("Number of cooked food items that Hunters will be given.")
                    .defineInRange("hunter_foodCount", switch(rd)
                    {
                        case DEFAULT, HERO -> 6;
                        case LEGEND -> 8;
                        case MASTER -> 10;
                        case GRANDMASTER -> 20;
                    }, 0, Integer.MAX_VALUE);

            this.foodGoldenAppleChance_config = spec
                    .comment("Chance that Hunters will be given Golden Apples rather than their normal cooked food item.")
                    .defineInRange("hunter_foodGoldenAppleChance", switch(rd)
                    {
                        case DEFAULT, HERO -> 0.15;
                        case LEGEND -> 0.2;
                        case MASTER -> 0.4;
                        case GRANDMASTER -> 0.75;
                    }, 0., 1.);

            this.mainItemDropChance_config = spec
                    .comment("Chance that Hunters will drop their main hand item (bow or sword) when killed.")
                    .defineInRange("hunter_mainItemDropChance", 0.25, 0., 1.);

            this.boomerangDropChance_config = spec
                    .comment("Chance that Hunters will drop their boomerang when killed.")
                    .defineInRange("hunter_boomerangDropChance", 0.5, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.bowPowerLevel = this.bowPowerLevel_config.get();
            this.swordSharpnessLevel = this.swordSharpnessLevel_config.get();
            this.bowPunchChance = this.bowPunchChance_config.get().floatValue();
            this.bowPunchLevel = this.bowPunchLevel_config.get();
            this.swordKnockbackChance = this.swordKnockbackChance_config.get().floatValue();
            this.swordKnockbackLevel = this.swordKnockbackLevel_config.get();
            this.boomerangLoyaltyChance = this.boomerangLoyaltyChance_config.get().floatValue();
            this.boomerangSharpnessLevel = this.boomerangSharpnessLevel_config.get();
            this.foodCount = this.foodCount_config.get();
            this.foodGoldenAppleChance = this.foodGoldenAppleChance_config.get().floatValue();
            this.mainItemDropChance = this.mainItemDropChance_config.get().floatValue();
            this.boomerangDropChance = this.boomerangDropChance_config.get().floatValue();
        }

        public Item getSword()
        {
            return ForgeRegistries.ITEMS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_SWORDS_HERO;
                case LEGEND -> TAG_SWORDS_LEGEND;
                case MASTER -> TAG_SWORDS_MASTER;
                case GRANDMASTER -> TAG_SWORDS_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(Items.IRON_SWORD);
        }
    }

    public static class Archer extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue bowPowerLevel_config;
        public int bowPowerLevel;

        private final ForgeConfigSpec.IntValue bowPunchLevel_config;
        public int bowPunchLevel;

        private final ForgeConfigSpec.DoubleValue bowDropChance_config;
        public float bowDropChance;

        public Archer(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.bowPowerLevel_config = spec
                    .comment("Level of Power that Archer bows will be enchanted with. 0 to disable.")
                    .defineInRange("archer_bowPowerLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.bowPunchLevel_config = spec
                    .comment("Level of Punch that Archer bows will be enchanted with. 0 to disable.")
                    .defineInRange("archer_bowPunchLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER -> 1;
                        case GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.bowDropChance_config = spec
                    .comment("Chance that Archers will drop their bow when killed.")
                    .defineInRange("archer_bowDropChance", 0.33F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.bowPowerLevel = this.bowPowerLevel_config.get();
            this.bowPunchLevel = this.bowPunchLevel_config.get();
            this.bowDropChance = this.bowDropChance_config.get().floatValue();
        }
    }

    public static class Skirmisher extends RaiderConfig
    {
        public static final TagKey<Item> TAG_AXES_HERO = ItemTags.create(new ResourceLocation("difficultraids:skirmisher/axes_hero"));
        public static final TagKey<Item> TAG_AXES_LEGEND = ItemTags.create(new ResourceLocation("difficultraids:skirmisher/axes_legend"));
        public static final TagKey<Item> TAG_AXES_MASTER = ItemTags.create(new ResourceLocation("difficultraids:skirmisher/axes_master"));
        public static final TagKey<Item> TAG_AXES_GRANDMASTER = ItemTags.create(new ResourceLocation("difficultraids:skirmisher/axes_grandmaster"));

        private final ForgeConfigSpec.IntValue axeSharpnessLevel_config;
        public int axeSharpnessLevel;

        private final ForgeConfigSpec.IntValue axeCriticalBurstLevel_config;
        public int axeCriticalBurstLevel;

        private final ForgeConfigSpec.IntValue axeCriticalStrikeLevel_config;
        public int axeCriticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue axeDropChance_config;
        public float axeDropChance;

        public Skirmisher(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.axeSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Skirmisher axes will be enchanted with. 0 to disable.")
                    .defineInRange("skirmisher_axeSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.axeCriticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Skirmisher axes will be enchanted with. 0 to disable.")
                    .defineInRange("skirmisher_axeCriticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 6;
                    }, 0, Integer.MAX_VALUE);

            this.axeCriticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Skirmisher axes will be enchanted with. 0 to disable.")
                    .defineInRange("skirmisher_axeCriticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER, GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.axeDropChance_config = spec
                    .comment("Chance that Skirmishers will drop their axe when killed.")
                    .defineInRange("skirmisher_axeDropChance", 0.33F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.axeSharpnessLevel = this.axeSharpnessLevel_config.get();
            this.axeCriticalBurstLevel = this.axeCriticalBurstLevel_config.get();
            this.axeCriticalStrikeLevel = this.axeCriticalStrikeLevel_config.get();
            this.axeDropChance = this.axeDropChance_config.get().floatValue();
        }

        public Item getAxe()
        {
            return ForgeRegistries.ITEMS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_AXES_HERO;
                case LEGEND -> TAG_AXES_LEGEND;
                case MASTER -> TAG_AXES_MASTER;
                case GRANDMASTER -> TAG_AXES_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(Items.IRON_AXE);
        }
    }

    public static class Legioner extends RaiderConfig
    {
        public static final TagKey<Item> TAG_SWORDS_HERO = ItemTags.create(new ResourceLocation("difficultraids:legioner/swords_hero"));
        public static final TagKey<Item> TAG_SWORDS_LEGEND = ItemTags.create(new ResourceLocation("difficultraids:legioner/swords_legend"));
        public static final TagKey<Item> TAG_SWORDS_MASTER = ItemTags.create(new ResourceLocation("difficultraids:legioner/swords_master"));
        public static final TagKey<Item> TAG_SWORDS_GRANDMASTER = ItemTags.create(new ResourceLocation("difficultraids:legioner/swords_grandmaster"));

        private final ForgeConfigSpec.IntValue swordSharpnessLevel_config;
        public int swordSharpnessLevel;

        private final ForgeConfigSpec.IntValue swordFireAspectLevel_config;
        public int swordFireAspectLevel;

        private final ForgeConfigSpec.IntValue swordKnockbackLevel_config;
        public int swordKnockbackLevel;

        private final ForgeConfigSpec.IntValue swordCriticalStrikeLevel_config;
        public int swordCriticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue swordDropChance_config;
        public float swordDropChance;

        public Legioner(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.swordSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Legioner swords will be enchanted with. 0 to disable.")
                    .defineInRange("legioner_swordSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.swordFireAspectLevel_config = spec
                    .comment("Level of Fire Aspect that Legioner swords will be enchanted with. 0 to disable.")
                    .defineInRange("legioner_swordFireAspectLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.swordKnockbackLevel_config = spec
                    .comment("Level of Knockback that Legioner swords will be enchanted with. 0 to disable.")
                    .defineInRange("legioner_swordKnockbackLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.swordCriticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Legioner swords will be enchanted with. 0 to disable.")
                    .defineInRange("legioner_swordCriticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT, HERO, LEGEND -> 0;
                        case MASTER -> 1;
                        case GRANDMASTER -> 3;
                    }, 0, Integer.MAX_VALUE);

            this.swordDropChance_config = spec
                    .comment("Chance that Legioners will drop their sword when killed.")
                    .defineInRange("legioner_swordDropChance", 0.25F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.swordSharpnessLevel = this.swordSharpnessLevel_config.get();
            this.swordFireAspectLevel = this.swordFireAspectLevel_config.get();
            this.swordKnockbackLevel = this.swordKnockbackLevel_config.get();
            this.swordCriticalStrikeLevel = this.swordCriticalStrikeLevel_config.get();
            this.swordDropChance = this.swordDropChance_config.get().floatValue();
        }

        public Item getSword()
        {
            return ForgeRegistries.ITEMS.tags().getTag(switch(this.rd)
            {
                case DEFAULT, HERO -> TAG_SWORDS_HERO;
                case LEGEND -> TAG_SWORDS_LEGEND;
                case MASTER -> TAG_SWORDS_MASTER;
                case GRANDMASTER -> TAG_SWORDS_GRANDMASTER;
            }).getRandomElement(RandomSource.create()).orElse(Items.IRON_SWORD);
        }
    }

    public static class Executioner extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue cleaverSharpnessLevel_config;
        public int cleaverSharpnessLevel;

        private final ForgeConfigSpec.IntValue cleaverCriticalBurstLevel_config;
        public int cleaverCriticalBurstLevel;

        private final ForgeConfigSpec.DoubleValue cleaverDropChance_config;
        public float cleaverDropChance;

        public Executioner(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.cleaverSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Executioner cleavers will be enchanted with. 0 to disable.")
                    .defineInRange("executioner_cleaverSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 3;
                        case LEGEND, MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.cleaverCriticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Executioner cleavers will be enchanted with. 0 to disable.")
                    .defineInRange("executioner_cleaverCriticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 5;
                        case LEGEND -> 6;
                        case MASTER -> 8;
                        case GRANDMASTER -> 10;
                    }, 0, Integer.MAX_VALUE);

            this.cleaverDropChance_config = spec
                    .comment("Chance that Executioners will drop their cleaver when killed.")
                    .defineInRange("executioner_cleaverDropChance", 0.15F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.cleaverSharpnessLevel = this.cleaverSharpnessLevel_config.get();
            this.cleaverCriticalBurstLevel = this.cleaverCriticalBurstLevel_config.get();
            this.cleaverDropChance = this.cleaverDropChance_config.get().floatValue();
        }
    }

    public static class Mountaineer extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue pickSharpnessLevel_config;
        public int pickSharpnessLevel;

        private final ForgeConfigSpec.IntValue pickCriticalBurstLevel_config;
        public int pickCriticalBurstLevel;

        private final ForgeConfigSpec.DoubleValue pickDropChance_config;
        public float pickDropChance;

        public Mountaineer(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.pickSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Mountaineer picks will be enchanted with. 0 to disable.")
                    .defineInRange("mountaineer_pickSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 4;
                    }, 0, Integer.MAX_VALUE);

            this.pickCriticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Mountaineer picks will be enchanted with. 0 to disable.")
                    .defineInRange("mountaineer_pickCriticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 3;
                        case LEGEND -> 5;
                        case MASTER -> 6;
                        case GRANDMASTER -> 8;
                    }, 0, Integer.MAX_VALUE);

            this.pickDropChance_config = spec
                    .comment("Chance that Mountaineers will drop their pick when killed.")
                    .defineInRange("mountaineer_pickDropChance", 0.25F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.pickSharpnessLevel = this.pickSharpnessLevel_config.get();
            this.pickCriticalBurstLevel = this.pickCriticalBurstLevel_config.get();
            this.pickDropChance = this.pickDropChance_config.get().floatValue();
        }
    }

    public static class RoyalGuard extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue axeSharpnessLevel_config;
        public int axeSharpnessLevel;

        private final ForgeConfigSpec.IntValue axeCriticalBurstLevel_config;
        public int axeCriticalBurstLevel;

        private final ForgeConfigSpec.IntValue axeCriticalStrikeLevel_config;
        public int axeCriticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue axeDropChance_config;
        public float axeDropChance;

        public RoyalGuard(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.axeSharpnessLevel_config = spec
                    .comment("Level of Sharpness that Royal Guard axes will be enchanted with. 0 to disable.")
                    .defineInRange("royalguard_axeSharpnessLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 3;
                        case LEGEND, MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.axeCriticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Royal Guard axes will be enchanted with. 0 to disable.")
                    .defineInRange("royalguard_axeCriticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 5;
                        case LEGEND -> 6;
                        case MASTER -> 8;
                        case GRANDMASTER -> 10;
                    }, 0, Integer.MAX_VALUE);

            this.axeCriticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Royal Guard axes will be enchanted with. 0 to disable.")
                    .defineInRange("royalguard_axeCriticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER, GRANDMASTER -> 4;
                    }, 0, Integer.MAX_VALUE);

            this.axeDropChance_config = spec
                    .comment("Chance that Royal Guards will drop their axe when killed.")
                    .defineInRange("royalguard_axeDropChance", 0.085F, 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.axeSharpnessLevel = this.axeSharpnessLevel_config.get();
            this.axeCriticalBurstLevel = this.axeCriticalBurstLevel_config.get();
            this.axeCriticalStrikeLevel = this.axeCriticalStrikeLevel_config.get();
            this.axeDropChance = this.axeDropChance_config.get().floatValue();
        }
    }

    public static class VindicatorWithShield extends RaiderConfig
    {
        private final ForgeConfigSpec.IntValue sharpnessLevel_config;
        public int sharpnessLevel;

        private final ForgeConfigSpec.IntValue criticalBurstLevel_config;
        public int criticalBurstLevel;

        private final ForgeConfigSpec.IntValue criticalStrikeLevel_config;
        public int criticalStrikeLevel;

        private final ForgeConfigSpec.DoubleValue axeDropChance_config;
        public float axeDropChance;

        public VindicatorWithShield(RaidDifficulty rd, ForgeConfigSpec.Builder spec)
        {
            super(rd);

            this.sharpnessLevel_config = spec
                    .comment("Level of Sharpness that Shielded Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("shieldVindicator_sharpnessLevel", switch(rd)
                    {
                        case DEFAULT -> 0;
                        case HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 5;
                    }, 0, Integer.MAX_VALUE);

            this.criticalBurstLevel_config = spec
                    .comment("Level of Critical Burst that Shielded Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("shieldVindicator_criticalBurstLevel", switch(rd)
                    {
                        case DEFAULT, HERO -> 0;
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 6;
                    }, 0, Integer.MAX_VALUE);

            this.criticalStrikeLevel_config = spec
                    .comment("Level of Critical Strike that Shielded Vindicator axes will be enchanted with. 0 to disable.")
                    .defineInRange("shieldVindicator_criticalStrikeLevel", switch(rd)
                    {
                        case DEFAULT -> 0;
                        case HERO, LEGEND -> 1;
                        case MASTER, GRANDMASTER -> 2;
                    }, 0, Integer.MAX_VALUE);

            this.axeDropChance_config = spec
                    .comment("Chance that a Shielded Vindicator will drop their axe upon death. 0 to disable.")
                    .defineInRange("shieldVindicator_axeDropChance", 0., 0., 1.);
        }

        @Override
        public void initialize()
        {
            this.sharpnessLevel = this.sharpnessLevel_config.get();
            this.criticalBurstLevel = this.criticalBurstLevel_config.get();
            this.criticalStrikeLevel = this.criticalStrikeLevel_config.get();
            this.axeDropChance = this.axeDropChance_config.get().floatValue();
        }
    }

    private static abstract class RaiderConfig
    {
        protected RaidDifficulty rd;

        RaiderConfig(RaidDifficulty rd)
        {
            this.rd = rd;
        }

        public abstract void initialize();
    }
}
