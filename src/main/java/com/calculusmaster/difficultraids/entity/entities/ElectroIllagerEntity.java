package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ElectroIllagerEntity extends AbstractEvokerVariant
{
    public ElectroIllagerEntity(EntityType<? extends AbstractEvokerVariant> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.50F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 25.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ElectroIllagerCastingSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new ElectroIllagerSummonLightningSpellGoal());
        this.goalSelector.addGoal(4, new ElectroIllagerSlownessLightningBoltSpellGoal());
        this.goalSelector.addGoal(5, new ElectroIllagerConcentratedLightningBoltSpellGoal());
        this.goalSelector.addGoal(5, new ElectroIllagerRingLightningSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {

    }

    private class ElectroIllagerCastingSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private ElectroIllagerCastingSpellGoal() {}

        @Override
        public void tick()
        {
            if(ElectroIllagerEntity.this.getTarget() != null)
                ElectroIllagerEntity.this.getLookControl().setLookAt(ElectroIllagerEntity.this.getTarget(), (float)ElectroIllagerEntity.this.getMaxHeadYRot(), (float)ElectroIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class ElectroIllagerSlownessLightningBoltSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ElectroIllagerSlownessLightningBoltSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = ElectroIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)ElectroIllagerEntity.this.getLevel();

            if(target != null)
            {
                BlockPos targetPos = target.blockPosition();

                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                lightning.setCustomName(new TextComponent(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));
                lightning.setDamage(2.0F);
                lightning.moveTo(targetPos, 0.0F, 0.0F);

                level.addFreshEntity(lightning);
                //System.out.println("Spell Cast! Summon Lightning Slowness");

                if(target.isAlive())
                {
                    boolean rain = level.isRainingAt(ElectroIllagerEntity.this.blockPosition());
                    boolean thunder = level.isThundering();

                    int slownessLevel = 2 + (rain ? 1 : 0) + (thunder ? 2 : 0);
                    int miningFatigueLevel = 1 + (rain ? 1 : 0);

                    MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, slownessLevel);
                    MobEffectInstance miningFatigue = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, miningFatigueLevel);

                    target.addEffect(slowness);
                    target.addEffect(miningFatigue);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            LivingEntity target = ElectroIllagerEntity.this.getTarget();
            boolean hasSlowness = target != null && target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN);
            boolean hasMiningFatigue = target != null && target.hasEffect(MobEffects.DIG_SLOWDOWN);
            boolean hasWeakness = target != null && target.hasEffect(MobEffects.WEAKNESS);
            return super.canUse() && (!hasSlowness || !hasMiningFatigue || !hasWeakness);
        }

        @Override
        protected int getCastingTime()
        {
            return 100;
        }

        @Override
        protected int getCastingInterval()
        {
            return 250;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 15;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ELECTRO_SLOWNESS_BOLT;
        }
    }

    private class ElectroIllagerConcentratedLightningBoltSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ElectroIllagerConcentratedLightningBoltSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = ElectroIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)ElectroIllagerEntity.this.getLevel();
            boolean raid = ElectroIllagerEntity.this.getCurrentRaid() != null;
            boolean rain = level.isRainingAt(ElectroIllagerEntity.this.blockPosition());
            boolean thunder = level.isThundering();

            if(target != null)
            {
                BlockPos targetPos = target.blockPosition();

                LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                lightning.setCustomName(new TextComponent(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));

                float damage = raid
                        ? RaidDifficulty.current().config().electro().concentratedBoltDamage()
                        : RaidDifficulty.DEFAULT.config().electro().concentratedBoltDamage();

                damage += switch(level.getDifficulty()) {
                    case PEACEFUL -> -damage;
                    case EASY -> -2.0F;
                    case NORMAL -> 0.0F;
                    case HARD -> 2.0F;
                };

                if(rain) damage++;

                if(thunder) damage *= switch(level.getDifficulty()) {
                    case PEACEFUL -> 0.0;
                    case EASY -> 1.05;
                    case NORMAL -> 1.1;
                    case HARD -> 1.2;
                };

                lightning.setDamage(damage);
                lightning.moveTo(targetPos, 0.0F, 0.0F);
                level.addFreshEntity(lightning);
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && ElectroIllagerEntity.this.getHealth() > ElectroIllagerEntity.this.getMaxHealth() / 2;
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 10;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ELECTRO_CONCENTRATED_BOLT;
        }
    }

    private class ElectroIllagerSummonLightningSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ElectroIllagerSummonLightningSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = ElectroIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)ElectroIllagerEntity.this.getLevel();
            boolean raid = ElectroIllagerEntity.this.getCurrentRaid() != null;
            boolean rain = level.isRainingAt(ElectroIllagerEntity.this.blockPosition());
            boolean thunder = level.isThundering();

            if(target != null)
            {
                BlockPos targetPos = target.blockPosition();
                Random random = new Random();

                int strikes = raid
                    ? RaidDifficulty.current().config().electro().genericLightningStrikeCount()
                    : RaidDifficulty.DEFAULT.config().electro().genericLightningStrikeCount();

                strikes += switch(level.getDifficulty()) {
                    case PEACEFUL -> -strikes;
                    case EASY -> -2;
                    case NORMAL -> 0;
                    case HARD -> 2;
                };

                strikes = random.nextInt(strikes - 1, strikes + 2);
                strikes = Math.max(1, strikes);

                for(int i = 0; i < strikes; i++)
                {
                    BlockPos offsetPos = targetPos.offset(-2 + random.nextInt(5), 0, -2 + random.nextInt(5));

                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    lightning.setCustomName(new TextComponent(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));

                    float damage = switch(level.getDifficulty()) {
                        case PEACEFUL -> 0.0F;
                        case EASY -> 6.0F;
                        case NORMAL -> 8.0F;
                        case HARD -> 10.0F;
                    } + (rain ? 1.0F : 0.0F) + (thunder ? 2.0F : 0.0F);

                    lightning.setDamage(damage);
                    lightning.moveTo(offsetPos, 0.0F, 0.0F);

                    level.addFreshEntity(lightning);
                }
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 10;
        }

        @Override
        protected int getCastingInterval()
        {
            return 200;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 10;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ELECTRO_SUMMON_BASIC_LIGHTNING_BOLTS;
        }
    }

    private class ElectroIllagerRingLightningSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ElectroIllagerRingLightningSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = ElectroIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)ElectroIllagerEntity.this.getLevel();
            boolean thunder = level.isThundering();

            if(target != null)
            {
                BlockPos targetPos = target.blockPosition();

                int dist = 10;
                List<BlockPos> offsets = new ArrayList<>(List.of(
                        targetPos.offset(dist, 0, 0),
                        targetPos.offset(-dist, 0, 0),
                        targetPos.offset(0, 0, dist),
                        targetPos.offset(0, 0, dist),
                        targetPos.offset(dist, 0, dist),
                        targetPos.offset(dist, 0, -dist),
                        targetPos.offset(-dist, 0, dist),
                        targetPos.offset(-dist, 0, -dist)
                ));

                if(ElectroIllagerEntity.this.getCurrentRaid() != null)
                {
                    List<BlockPos> extraOffsets = new ArrayList<>();
                    RaidDifficulty raidDifficulty = RaidDifficulty.current();

                    if(raidDifficulty.config().electro().extraRingBolts()) offsets.forEach(pos -> {
                        BlockPos farPos = new BlockPos(pos);
                        if(pos.getX() != 0) farPos = pos.offset(pos.getX(), 0, 0);
                        if(pos.getZ() != 0) farPos = pos.offset(0, 0, pos.getZ());
                        extraOffsets.add(farPos);
                    });

                    offsets.addAll(extraOffsets);
                }

                offsets.forEach(pos -> {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    lightning.setCustomName(new TextComponent(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG));
                    lightning.moveTo(pos, 0, 0);
                    lightning.setDamage(thunder ? 3.0F : 1.0F);

                    level.addFreshEntity(lightning);
                });
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && ElectroIllagerEntity.this.getHealth() < ElectroIllagerEntity.this.getMaxHealth() / 2;
        }

        @Override
        protected int getCastingTime()
        {
            return 100;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 80;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ELECTRO_LIGHTNING_RING;
        }
    }
}
