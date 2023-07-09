package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.component.ShamanDebuffBulletEntity;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.*;
import java.util.function.Predicate;

public class ShamanIllagerEntity extends AbstractEvokerVariant
{
    public ShamanIllagerEntity(EntityType<? extends AbstractEvokerVariant> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new ShamanCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(3, new ShamanMoveToRaidersGoal());
        this.goalSelector.addGoal(4, new ShamanAttackBoostSpellGoal());
        this.goalSelector.addGoal(4, new ShamanDefenseBoostSpellGoal());
        this.goalSelector.addGoal(5, new ShamanDebuffSpellGoal());
        this.goalSelector.addGoal(6, new ShamanInvisibilitySpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.3D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        if(Compat.GUARD_VILLAGERS.isLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 8.0F, 0.7D, 1.0D));
    }

    private List<Raider> getNearbyRaiders(double distance)
    {
        return this.level.getNearbyEntities(
                Raider.class,
                TargetingConditions.forNonCombat().range(distance),
                this,
                new AABB(this.blockPosition()).inflate(distance)
        );
    }

    private Raider getNearestRaider()
    {
        return this.getCurrentRaid() == null ? null : this.level.getNearestEntity(
                Raider.class,
                TargetingConditions.forNonCombat().ignoreLineOfSight(),
                this,
                this.blockPosition().getX(), this.blockPosition().getY(), this.blockPosition().getZ(),
                new AABB(this.blockPosition()).inflate(Math.sqrt(Raid.VALID_RAID_RADIUS_SQR)));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {

    }

    private class ShamanMoveToRaidersGoal extends Goal
    {
        private Raider target;

        private ShamanMoveToRaidersGoal() { this.setFlags(EnumSet.of(Flag.MOVE)); }

        @Override
        public boolean canUse()
        {
            this.target = ShamanIllagerEntity.this.getNearestRaider();
            return ShamanIllagerEntity.this.getNearbyRaiders(5.0D).isEmpty() && this.target != null;
        }

        @Override
        public boolean canContinueToUse()
        {
            return super.canContinueToUse() && this.target.isAlive();
        }

        @Override
        public void start()
        {
            ShamanIllagerEntity.this.getNavigation().moveTo(this.target, 1.3D);
        }

        @Override
        public void stop()
        {
            ShamanIllagerEntity.this.getNavigation().stop();
        }
    }

    private class ShamanCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private ShamanCastSpellGoal() {}

        @Override
        public void tick()
        {
            if(ShamanIllagerEntity.this.getTarget() != null)
                ShamanIllagerEntity.this.getLookControl().setLookAt(ShamanIllagerEntity.this.getTarget(), (float)ShamanIllagerEntity.this.getMaxHeadYRot(), (float)ShamanIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class ShamanInvisibilitySpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        protected void castSpell()
        {
            int duration = ShamanIllagerEntity.this.config().shaman.invisibilityDuration;

            ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 1));
        }

        @Override
        public boolean canUse()
        {
            if(ShamanIllagerEntity.this.getHealth() < ShamanIllagerEntity.this.getMaxHealth() / 2)
            {
                if(ShamanIllagerEntity.this.isCastingSpell()) return false;
                else return ShamanIllagerEntity.this.tickCount >= this.spellCooldown;
            }
            else return false;
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 600;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 20;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_INVISIBILITY;
        }
    }

    private class ShamanDebuffSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private ShamanDebuffSpellGoal() {}

        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();
            LivingEntity target = ShamanIllagerEntity.this.getTarget();
            Random random = new Random();

            List<MobEffect> debuffPool = new ArrayList<>(ShamanIllagerEntity.this.config().shaman.getEffectPool());

            if(target != null)
            {
                int debuffCount = ShamanIllagerEntity.this.config().shaman.maxDebuffCount;

                Set<MobEffect> apply = new HashSet<>();

                //Main Effect
                MobEffect mainEffect = debuffPool.get(random.nextInt(debuffPool.size()));
                apply.add(mainEffect);
                debuffPool.remove(mainEffect);

                //Additional Effects
                for(int i = 0; i < debuffCount - 1 && !debuffPool.isEmpty(); i++)
                {
                    if(ShamanIllagerEntity.this.random.nextFloat() < ShamanIllagerEntity.this.config().shaman.additionalDebuffChance)
                    {
                        MobEffect effect = debuffPool.get(random.nextInt(debuffPool.size()));
                        apply.add(effect);
                        debuffPool.remove(effect);
                    }
                    else break;
                }

                ShamanDebuffBulletEntity projectile = ShamanDebuffBulletEntity.create(level, ShamanIllagerEntity.this, target, ShamanIllagerEntity.this.getDirection().getAxis());

                apply.forEach(effect -> {
                    int duration = ShamanIllagerEntity.this.config().shaman.debuffDuration;
                    int amplifier = ShamanIllagerEntity.this.config().shaman.debuffAmplifier;

                    projectile.loadDebuff(new MobEffectInstance(effect, duration, amplifier));
                });

                projectile.moveTo(new BlockPos(ShamanIllagerEntity.this.getEyePosition()).offset(0.0, ShamanIllagerEntity.this.getEyeHeight() + 1, 0.0), 0.0F, 0.0F);
                level.addFreshEntity(projectile);
                ShamanIllagerEntity.this.playSound(SoundEvents.SHULKER_SHOOT, 2.0F, 1.0F);
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
            return 120;
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
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_DEBUFF;
        }
    }

    private class ShamanDefenseBoostSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        public boolean canUse()
        {
            return super.canUse() && ShamanIllagerEntity.this.isInRaid();
        }

        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();

            AABB buffAABB = new AABB(ShamanIllagerEntity.this.blockPosition()).inflate(ShamanIllagerEntity.this.config().shaman.allyBuffRadius);

            Predicate<AbstractIllager> canReceiveBuff = illager -> !illager.is(ShamanIllagerEntity.this) && !illager.hasEffect(MobEffects.DAMAGE_RESISTANCE);
            List<AbstractIllager> raiders = level.getEntitiesOfClass(AbstractIllager.class, buffAABB, canReceiveBuff);

            int duration = ShamanIllagerEntity.this.config().shaman.allyResistanceDuration;
            int amplifier = ShamanIllagerEntity.this.config().shaman.allyResistanceAmplifier;

            raiders.forEach(r -> {
                r.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_RESISTANCE,
                        duration < 40 ? duration : random.nextInt(duration - 20, duration + 21),
                        amplifier));
                r.playSound(SoundEvents.BREWING_STAND_BREW, 0.5F, 1.0F);
            });

            ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration / 2, amplifier / 2));
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 40;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_DEFENSE_BOOST;
        }
    }

    private class ShamanAttackBoostSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        @Override
        public boolean canUse()
        {
            return super.canUse() && ShamanIllagerEntity.this.isInRaid();
        }

        @Override
        protected void castSpell()
        {
            ServerLevel level = (ServerLevel)ShamanIllagerEntity.this.getLevel();

            AABB buffAABB = new AABB(ShamanIllagerEntity.this.blockPosition()).inflate(ShamanIllagerEntity.this.config().shaman.allyBuffRadius);

            Predicate<AbstractIllager> canReceiveBuff = illager -> !illager.is(ShamanIllagerEntity.this) && !illager.hasEffect(MobEffects.DAMAGE_BOOST);
            List<AbstractIllager> raiders = level.getEntitiesOfClass(AbstractIllager.class, buffAABB, canReceiveBuff);

            int duration = ShamanIllagerEntity.this.config().shaman.allyStrengthDuration;
            int amplifier = ShamanIllagerEntity.this.config().shaman.allyStrengthAmplifier;

            raiders.forEach(r -> {
                r.addEffect(new MobEffectInstance(
                        MobEffects.DAMAGE_BOOST,
                        duration < 40 ? duration : random.nextInt(duration - 20, duration + 21),
                        amplifier));
                r.playSound(SoundEvents.BREWING_STAND_BREW, 0.5F, 1.0F);
            });

            ShamanIllagerEntity.this.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration / 2, amplifier / 2));
        }

        @Override
        protected int getCastingTime()
        {
            return 40;
        }

        @Override
        protected int getCastingInterval()
        {
            return 400;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 40;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.WITCH_DRINK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.SHAMAN_ATTACK_BOOST;
        }
    }
}
