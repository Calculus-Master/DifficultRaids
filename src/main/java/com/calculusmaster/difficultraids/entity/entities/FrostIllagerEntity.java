package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.component.FrostSnowballEntity;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class FrostIllagerEntity extends AbstractSpellcastingIllager
{
    private int barrageTicks = 0;

    public FrostIllagerEntity(EntityType<? extends AbstractSpellcastingIllager> p_33724_, Level p_33725_)
    {
        super(p_33724_, p_33725_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FrostIllagerCastSpell());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new FrostIllagerFreezeSpellGoal());
        this.goalSelector.addGoal(4, new FrostIllagerBarrageSpellGoal());
        this.goalSelector.addGoal(5, new FrostIllagerSnowballBlastSpellGoal());
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.7D));
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

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.isProjectile()) pAmount *= 2;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("SnowballBarrageTicks", this.barrageTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.barrageTicks = pCompound.getInt("SnowballBarrageTicks");
    }

    @Override
    public void tick()
    {
        super.tick();

        //Slowness Aura
        final AABB checkRadius = new AABB(this.blockPosition()).inflate(6.0D);
        List<Player> nearbyPlayers = this.level.getEntitiesOfClass(Player.class, checkRadius, p -> !p.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) && (!p.isCreative() && !p.isSpectator()));
        List<AbstractVillager> nearbyVillagers = this.level.getEntitiesOfClass(AbstractVillager.class, checkRadius, v -> !v.hasEffect(MobEffects.MOVEMENT_SLOWDOWN));
        List<IronGolem> nearbyGolems = this.level.getEntitiesOfClass(IronGolem.class, checkRadius, g -> !g.hasEffect(MobEffects.MOVEMENT_SLOWDOWN));

        Supplier<MobEffectInstance> slowness = () -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3);
        nearbyPlayers.forEach(p -> p.addEffect(slowness.get()));
        nearbyVillagers.forEach(v -> v.addEffect(slowness.get()));
        nearbyGolems.forEach(g -> g.addEffect(slowness.get()));

        //Barrage
        if(this.barrageTicks > 0)
        {
            this.barrageTicks--;

            LivingEntity target = this.getTarget();

            if(target != null)
            {
                int size = this.random.nextInt(20, 41);

                for(int i = 0; i < size; i++)
                {
                    FrostSnowballEntity snowball = DifficultRaidsEntityTypes.FROST_SNOWBALL.get().create(this.level);
                    snowball.setOwner(this);
                    snowball.setPos(this.eyeBlockPosition().getX(), this.eyeBlockPosition().getY() - 0.2, this.eyeBlockPosition().getZ());
                    snowball.setDamage(switch(this.level.getDifficulty()) {
                        case PEACEFUL -> 0.0F;
                        case EASY -> 1.8F;
                        case NORMAL -> 2.4F;
                        case HARD -> 2.9F;
                    });

                    //Helps with client lag a little
                    if(this.random.nextInt(100) < 40) snowball.setInvisible(true);

                    double targetY = target.getEyeY() - 1.1D;
                    double targetX = target.getX() - this.getX();
                    double targetSnowballY = targetY - snowball.getY();
                    double targetZ = target.getZ() - this.getZ();
                    double distanceY = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;

                    snowball.shoot(targetX, targetSnowballY + distanceY, targetZ, 1.6F, 14.0F);
                    this.level.addFreshEntity(snowball);
                }

                this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (this.random.nextFloat() * 0.4F + 0.8F));
            }
            else this.barrageTicks = 0;
        }
    }

    private class FrostIllagerCastSpell extends SpellcastingIllagerCastSpellGoal
    {
        private FrostIllagerCastSpell() {}

        @Override
        public void tick()
        {
            if(FrostIllagerEntity.this.getTarget() != null)
                FrostIllagerEntity.this.getLookControl().setLookAt(FrostIllagerEntity.this.getTarget(), (float)FrostIllagerEntity.this.getMaxHeadYRot(), (float)FrostIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class FrostIllagerSnowballBlastSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private FrostIllagerSnowballBlastSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = FrostIllagerEntity.this.getTarget();
            int size = 5;

            if(target != null)
            {
                float damage = FrostIllagerEntity.this.getCurrentRaid() != null ? switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                    case DEFAULT -> 1.5F;
                    case HERO -> 2.5F;
                    case LEGEND -> 3.5F;
                    case MASTER -> 5.0F;
                    case APOCALYPSE -> 8.0F;
                } + switch(FrostIllagerEntity.this.level.getDifficulty()) {
                    case PEACEFUL, NORMAL -> 0.0F;
                    case EASY -> -0.5F;
                    case HARD -> 0.5F;
                } : switch(FrostIllagerEntity.this.level.getDifficulty()) {
                    case PEACEFUL -> 0.0F;
                    case EASY -> 0.75F;
                    case NORMAL -> 1.3F;
                    case HARD -> 2.0F;
                };

                for(int i = 0; i < size; i++)
                {
                    FrostSnowballEntity snowball = DifficultRaidsEntityTypes.FROST_SNOWBALL.get().create(FrostIllagerEntity.this.level);
                    snowball.setOwner(FrostIllagerEntity.this);
                    snowball.setPos(FrostIllagerEntity.this.eyeBlockPosition().getX(), FrostIllagerEntity.this.eyeBlockPosition().getY() - 0.2, FrostIllagerEntity.this.eyeBlockPosition().getZ());
                    snowball.setDamage(damage);

                    double targetY = target.getEyeY() - 1.1D;
                    double targetX = target.getX() - FrostIllagerEntity.this.getX();
                    double targetSnowballY = targetY - snowball.getY();
                    double targetZ = target.getZ() - FrostIllagerEntity.this.getZ();
                    double distanceY = Math.sqrt(targetX * targetX + targetZ * targetZ) * (double)0.2F;

                    snowball.shoot(targetX, targetSnowballY + distanceY, targetZ, 1.6F, 6.0F);
                    FrostIllagerEntity.this.level.addFreshEntity(snowball);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && FrostIllagerEntity.this.barrageTicks == 0;
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return 80;
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
            return SoundEvents.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.FROST_SNOWBALL_BLAST;
        }
    }

    private class FrostIllagerBarrageSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private FrostIllagerBarrageSpellGoal() {}

        @Override
        protected void castSpell()
        {
            boolean raid = FrostIllagerEntity.this.getCurrentRaid() != null;
            FrostIllagerEntity.this.barrageTicks = raid ? switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                case DEFAULT -> 20 * 3;
                case HERO -> 20 * 7;
                case LEGEND -> 20 * 12;
                case MASTER -> 20 * 15;
                case APOCALYPSE -> 20 * 20;
            } : switch(FrostIllagerEntity.this.level.getDifficulty()) {
                case PEACEFUL -> 0;
                case EASY -> 20 * 2;
                case NORMAL -> 20 * 5;
                case HARD -> 20 * 9;
            };
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && FrostIllagerEntity.this.barrageTicks == 0;
        }

        @Override
        protected int getCastingTime()
        {
            return 20;
        }

        @Override
        protected int getCastingInterval()
        {
            return 600;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 50;
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
            return SpellType.FROST_BARRAGE;
        }
    }

    private class FrostIllagerFreezeSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private FrostIllagerFreezeSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = FrostIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)FrostIllagerEntity.this.getLevel();
            boolean raid = FrostIllagerEntity.this.getCurrentRaid() != null;

            if(target != null)
            {
                int duration = raid ? switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
                    case DEFAULT -> 20 * 5;
                    case HERO -> 20 * 8;
                    case LEGEND -> 20 * 10;
                    case MASTER -> 20 * 15;
                    case APOCALYPSE -> 20 * 30;
                } : switch(level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 20 * 3;
                    case NORMAL -> 20 * 5;
                    case HARD -> 20 * 7;
                };

                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 10));
                target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration / 4, 10));
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 50;
        }

        @Override
        protected int getCastingInterval()
        {
            return 430;
        }

        @Override
        protected int getCastWarmupTime()
        {
            return 30;
        }

        @Nullable
        @Override
        protected SoundEvent getSpellPrepareSound()
        {
            return SoundEvents.EVOKER_CAST_SPELL;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.FROST_FREEZE;
        }
    }
}
