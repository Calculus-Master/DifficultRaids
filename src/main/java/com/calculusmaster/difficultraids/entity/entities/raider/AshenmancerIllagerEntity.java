package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.config.RaiderConfigs;
import com.calculusmaster.difficultraids.entity.entities.component.AshenadoObject;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AshenmancerIllagerEntity extends AbstractEvokerVariant
{
    private static final double SUMMON_CHECK_RADIUS = 30.0;

    private static final EntityDataAccessor<Boolean> TURRET_ACTIVE = SynchedEntityData.defineId(AshenmancerIllagerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> LAST_SKULL_TYPE = SynchedEntityData.defineId(AshenmancerIllagerEntity.class, EntityDataSerializers.STRING);

    private int maxMinions;
    private List<WitherSkeleton> minions = new ArrayList<>();
    private String minionTag;
    private boolean checkMinions = false;

    private AshenadoObject ashenado = null;
    private int ashenadoCooldown = 0;

    private int turretCount = 0;
    private int turretInterval = 0;
    private int ticksNoTarget = 0;

    public AshenmancerIllagerEntity(EntityType<? extends AbstractEvokerVariant> type, Level world)
    {
        super(type, world);

        this.minionTag = IntStream.generate(() -> this.getRandom().nextInt(10)).limit(6).mapToObj(String::valueOf).collect(Collectors.joining()) + "_witherminion";
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AshenmancerCastSpellGoal());
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 0.6D, 1.0D));
        if(Compat.GUARD_VILLAGERS.isLoaded()) this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Guard.class, 6.0F, 0.7D, 1.0D));
        this.goalSelector.addGoal(3, new AshenmancerSummonMinionsSpellGoal());
        //TODO: Ashenado Rework this.goalSelector.addGoal(4, new AshenmancerAshenadoSpellGoal());
        this.goalSelector.addGoal(5, new AshenmancerWitherSkullTurretSpellGoal());
        this.goalSelector.addGoal(6, new AshenmancerShootSkullsSpellGoal());

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        if(Compat.GUARD_VILLAGERS.isLoaded()) this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Guard.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true)).setUnseenMemoryTicks(300));

    }

    @Override
    public void applyRaidBuffs(int pWave, boolean pUnusedFalse)
    {
        this.maxMinions = this.config().ashenmancer.maxMinionCount;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound)
    {
        super.addAdditionalSaveData(pCompound);

        pCompound.putInt("MaxMinions", this.maxMinions);
        pCompound.putString("MinionTag", this.minionTag);
        pCompound.putBoolean("HasMinions", !this.minions.isEmpty());
        pCompound.putBoolean("AshenadoActive", this.ashenado != null);
        if(this.ashenado != null) this.ashenado.save(pCompound);
        pCompound.putInt("TurretCount", this.turretCount);
        pCompound.putInt("TicksNoTarget", this.ticksNoTarget);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound)
    {
        super.readAdditionalSaveData(pCompound);

        this.maxMinions = pCompound.getInt("MaxMinions");
        this.minionTag = pCompound.getString("MinionTag");
        this.checkMinions = pCompound.getBoolean("HasMinions");
        this.ashenado = pCompound.getBoolean("AshenadoActive") ? new AshenadoObject(this, pCompound) : null;
        this.turretCount = pCompound.getInt("TurretCount");
        this.ticksNoTarget = pCompound.getInt("TicksNoTarget");
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(TURRET_ACTIVE, false);
        this.entityData.define(LAST_SKULL_TYPE, WitherSkullType.STANDARD.toString());
    }

    public boolean canSpawnMinion()
    {
        return this.minions.size() < this.maxMinions;
    }

    public void setMinionTargets(WitherSkeleton minion)
    {
        minion.targetSelector.removeAllGoals();
        minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, Player.class, true));
        minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, IronGolem.class, true));
        minion.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(minion, AbstractVillager.class, true));
        if(Compat.GUARD_VILLAGERS.isLoaded()) minion.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(minion, Guard.class, true));
    }

    public void setTurretActive(boolean value)
    {
        this.entityData.set(TURRET_ACTIVE, value);
    }

    public boolean isTurretActive()
    {
        return this.entityData.get(TURRET_ACTIVE);
    }

    public void setLastSkullType(WitherSkullType type)
    {
        this.entityData.set(LAST_SKULL_TYPE, type.toString());
    }

    public WitherSkullType getLastSkullType()
    {
        return WitherSkullType.valueOf(this.entityData.get(LAST_SKULL_TYPE));
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();

        //Loading
        if(this.checkMinions)
        {
            this.minions.addAll(this.level.getEntitiesOfClass(WitherSkeleton.class, this.getBoundingBox().inflate(SUMMON_CHECK_RADIUS), e -> e.getTags().contains(this.minionTag)));

            this.checkMinions = this.tickCount <= 100 && this.minions.isEmpty();

            if(!this.checkMinions) this.minions.forEach(this::setMinionTargets);
        }

        //Minion Updates
        if(this.tickCount % 20 == 0)
        {
            this.minions.removeIf(WitherSkeleton::isDeadOrDying);

            for(WitherSkeleton ws : this.minions)
            {
                //Return to Ashenmancer if wandering away
                if(ws.getTarget() == null && ws.distanceTo(this) >= 20.0) ws.getNavigation().moveTo(this, 1.5);

                //Heal if close enough to Ashenmancer
                if(this.tickCount % 40 == 0 && ws.getHealth() < ws.getMaxHealth() * 0.75 && ws.distanceTo(this) <= 3.0)
                {
                    ws.heal(1.0F);
                    ((ServerLevel)this.level).sendParticles(ParticleTypes.HAPPY_VILLAGER, ws.getX(), ws.getEyeY() + 0.25, ws.getZ(), 3, this.random.nextFloat() / 2, this.random.nextFloat() / 2, this.random.nextFloat() / 2, 0.4);
                }
            }
        }

        //Ashenado
        if(this.ashenadoCooldown > 0) this.ashenadoCooldown--;

        if(this.ashenado != null)
        {
            this.ashenado.tick();

            if(this.ashenado.isComplete())
            {
                this.ashenado = null;
                this.ashenadoCooldown = 20 * 20;
            }
        }

        //Turret
        if(this.turretCount > 0)
        {
            //Stop movement
            this.getNavigation().stop();

            //If for some reason the interval is 0, check config again
            if(this.turretInterval == 0) this.turretInterval = this.config().ashenmancer.turretInterval;

            //Wither Skull Logic
            if(this.tickCount % this.turretInterval == 0 && this.getTarget() != null)
            {
                this.ticksNoTarget = 0;

                LivingEntity target = this.getTarget();
                double tX = target.getX(); double tY = target.getY() + target.getEyeHeight() / 2; double tZ = target.getZ();
                double aX = this.getX(); double aY = this.getEyeY() + 0.35; double aZ = this.getZ();

                double dX = tX - aX, dY = tY - aY, dZ = tZ - aZ;

                if(Math.abs(dX) > Math.abs(dZ)) aZ += (AshenmancerIllagerEntity.this.random.nextInt(7) - 3);
                else aX += (AshenmancerIllagerEntity.this.random.nextInt(7) - 3);

                dX = tX - aX; dZ = tZ - aZ;

                Predicate<Entity> validTarget = e -> !(e instanceof Raider) && !e.isAlliedTo(this) && !(e instanceof WitherSkeleton);

                WitherSkull witherSkull = new WitherSkull(this.level, this, dX, dY, dZ)
                {
                    @Override
                    protected void onHitEntity(EntityHitResult pResult)
                    {
                        if(validTarget.test(pResult.getEntity()))
                        {
                            super.onHitEntity(pResult);
                        }
                    }
                };

                witherSkull.setOwner(this);
                witherSkull.setPos(aX, aY, aZ);
                this.level.addFreshEntity(witherSkull);

                this.turretCount--;
            }
            else if(this.getTarget() == null) this.ticksNoTarget++;

            //If grace period (time without target) ends, stop turret early
            if(this.ticksNoTarget >= 40 && this.turretCount > 0) this.turretCount = 0;

            //Particles
            if(this.tickCount % 5 == 0 && this.level instanceof ServerLevel serverLevel) for(int i = 0; i < 4; i++)
                serverLevel.sendParticles(ParticleTypes.SMOKE, this.getX() + (0.25 - this.random.nextFloat() * 0.5), this.getEyeY(), this.getZ() + (0.25 - this.random.nextFloat() * 0.5), 1, 0, 0, 0, 0.2);
        }
        else if(this.isTurretActive() && this.turretCount == 0) this.setTurretActive(false);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(this.random.nextBoolean() && pSource.getEntity() instanceof LivingEntity living)
            for(WitherSkeleton ws : this.minions) if(ws.getTarget() == null)
            {
                ws.getNavigation().moveTo(living, 1.4);
                ws.setTarget(living);
            }

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void die(DamageSource pCause)
    {
        super.die(pCause);

        this.minions.forEach(LivingEntity::kill);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        this.maxMinions = this.config().ashenmancer.maxMinionCount;
        this.turretInterval = this.config().ashenmancer.turretInterval;

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    @Override
    public IllagerArmPose getArmPose()
    {
        return this.isTurretActive() ? IllagerArmPose.SPELLCASTING : super.getArmPose();
    }

    private class AshenmancerCastSpellGoal extends SpellcastingIllagerCastSpellGoal
    {
        private AshenmancerCastSpellGoal() {}

        @Override
        public void tick()
        {
            if(AshenmancerIllagerEntity.this.getTarget() != null)
                AshenmancerIllagerEntity.this.getLookControl().setLookAt(AshenmancerIllagerEntity.this.getTarget(), (float) AshenmancerIllagerEntity.this.getMaxHeadYRot(), (float) AshenmancerIllagerEntity.this.getMaxHeadXRot());
        }
    }

    private class AshenmancerSummonMinionsSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private AshenmancerSummonMinionsSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = AshenmancerIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel) AshenmancerIllagerEntity.this.getLevel();

            if(target != null)
            {
                RandomSource random = AshenmancerIllagerEntity.this.random;
                RaiderConfigs.Ashenmancer cfg = AshenmancerIllagerEntity.this.config().ashenmancer;

                WitherSkeleton minion = new WitherSkeleton(EntityType.WITHER_SKELETON, level)
                {
                    @Override
                    protected boolean shouldDropLoot() { return false; }
                };

                BlockPos pos = AshenmancerIllagerEntity.this.blockPosition().offset(-1 + random.nextInt(3), 0, -1 + random.nextInt(3));

                final int maxProtection = cfg.minionMaxProtectionLevel;
                Function<Item, ItemStack> buildArmor = item ->
                {
                    ItemStack out = item.getDefaultInstance();

                    if(maxProtection > 0)
                        out.enchant(Enchantments.ALL_DAMAGE_PROTECTION, maxProtection == 1 ? maxProtection : random.nextInt(1, maxProtection + 1));

                    return out;
                };

                minion.setItemSlot(EquipmentSlot.HEAD, buildArmor.apply(cfg.getMinionHelmet()));
                minion.setItemSlot(EquipmentSlot.CHEST, buildArmor.apply(cfg.getMinionChestplate()));
                minion.setItemSlot(EquipmentSlot.LEGS, buildArmor.apply(cfg.getMinionLeggings()));
                minion.setItemSlot(EquipmentSlot.FEET, buildArmor.apply(cfg.getMinionBoots()));

                ItemStack sword = cfg.getMinionSword().getDefaultInstance();

                final int maxSharpness = cfg.minionMaxSharpnessLevel;
                if(maxSharpness > 0) sword.enchant(Enchantments.SHARPNESS, maxSharpness == 1 ? maxSharpness : random.nextInt(1, maxSharpness + 1));

                minion.setItemSlot(EquipmentSlot.MAINHAND, sword);

                minion.moveTo(pos, 0, 0);
                minion.setTarget(target);
                minion.getLookControl().setLookAt(target);
                minion.addTag(AshenmancerIllagerEntity.this.minionTag);

                AshenmancerIllagerEntity.this.setMinionTargets(minion);

                level.addFreshEntity(minion);

                AshenmancerIllagerEntity.this.minions.add(minion);
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && AshenmancerIllagerEntity.this.canSpawnMinion();
        }

        @Override
        protected int getCastingTime()
        {
            return 50;
        }

        @Override
        protected int getCastingInterval()
        {
            return 500;
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
            return SoundEvents.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ASHENMANCER_SUMMON_MINIONS;
        }
    }

    public enum WitherSkullType { PUSHER, FLAMER, MULTISHOTTER, SPEEDER, STANDARD, BLINDER }

    private class AshenmancerShootSkullsSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private AshenmancerShootSkullsSpellGoal() {}

        @Override
        protected void castSpell()
        {
            LivingEntity target = AshenmancerIllagerEntity.this.getTarget();
            ServerLevel level = (ServerLevel)AshenmancerIllagerEntity.this.getLevel();
            AshenmancerIllagerEntity ashenmancer = AshenmancerIllagerEntity.this;

            List<WitherSkullType> pool = new ArrayList<>(List.of(WitherSkullType.values()));
            pool.add(WitherSkullType.STANDARD); pool.add(WitherSkullType.STANDARD);

            if(target != null)
            {
                double tX = target.getX(); double tY = target.getY() + target.getEyeHeight() / 2; double tZ = target.getZ();
                double aX = ashenmancer.getX(); double aY = ashenmancer.getEyeY() + 0.35; double aZ = ashenmancer.getZ();

                Predicate<Entity> validTarget = e -> !(e instanceof Raider) && !e.isAlliedTo(ashenmancer) && !(e instanceof WitherSkeleton);

                WitherSkullType type = pool.get(ashenmancer.random.nextInt(pool.size()));

                //Initial vector calculation
                double dX = tX - aX, dY = tY - aY, dZ = tZ - aZ;

                if(type == WitherSkullType.MULTISHOTTER)
                {
                    int count = AshenmancerIllagerEntity.this.config().ashenmancer.multishotSkullCount;
                    for(int i = 0; i < count; i++)
                    {
                        if(Math.abs(dX) > Math.abs(dZ)) aZ += (AshenmancerIllagerEntity.this.random.nextInt(7) - 3);
                        else aX += (AshenmancerIllagerEntity.this.random.nextInt(7) - 3);

                        //Add inaccuracy
                        if(i != 0)
                        {
                            tX += (0.25 - AshenmancerIllagerEntity.this.random.nextFloat() * 0.5);
                            tZ += (0.25 - AshenmancerIllagerEntity.this.random.nextFloat() * 0.5);
                        }

                        //Recalculate after spacing out
                        dX = tX - aX; dZ = tZ - aZ;

                        WitherSkull witherSkull = new WitherSkull(level, ashenmancer, dX, dY, dZ)
                        {
                            @Override
                            protected void onHitEntity(EntityHitResult pResult)
                            {
                                if(validTarget.test(pResult.getEntity())) super.onHitEntity(pResult);
                            }
                        };

                        witherSkull.setOwner(ashenmancer);
                        witherSkull.setPos(aX, aY, aZ);

                        level.addFreshEntity(witherSkull);
                    }
                }
                else
                {
                    //Speeder Inaccuracy
                    if(type == WitherSkullType.SPEEDER)
                    {
                        tX += (0.125 - AshenmancerIllagerEntity.this.random.nextFloat() * 0.25);
                        tZ += (0.125 - AshenmancerIllagerEntity.this.random.nextFloat() * 0.25);

                        dX = tX - aX; dY = tY - aY; dZ = tZ - aZ;
                    }

                    WitherSkull witherSkull = new WitherSkull(level, ashenmancer, dX, dY, dZ)
                    {
                        @Override
                        protected void onHitEntity(EntityHitResult pResult)
                        {
                            if(validTarget.test(pResult.getEntity()))
                            {
                                super.onHitEntity(pResult);

                                if(ashenmancer.ashenado != null)
                                {
                                    Vec3 dir = new Vec3(pResult.getEntity().getX() - ashenmancer.ashenado.getCenter().x(), pResult.getEntity().getY() - ashenmancer.ashenado.getCenter().y(), pResult.getEntity().getZ() - ashenmancer.ashenado.getCenter().z()).normalize();

                                    final float force = type == WitherSkullType.PUSHER ? ashenmancer.config().ashenmancer.pusherSkullForce : 1.25F;
                                    pResult.getEntity().push(dir.x() * force, dir.y() * force, dir.z() * force);
                                    pResult.getEntity().hurtMarked = true;
                                }
                                else if(type == WitherSkullType.PUSHER)
                                {
                                    final float force = ashenmancer.config().ashenmancer.pusherSkullForce;

                                    pResult.getEntity().push(this.random.nextFloat(), force, this.random.nextFloat());
                                    pResult.getEntity().hurtMarked = true;
                                }
                                else if(type == WitherSkullType.FLAMER)
                                    pResult.getEntity().setSecondsOnFire(ashenmancer.config().ashenmancer.flamerSkullFireDuration);
                                else if(type == WitherSkullType.BLINDER && pResult.getEntity() instanceof LivingEntity living)
                                    living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, ashenmancer.config().ashenmancer.blinderSkullBlindnessDuration, 0, false, true));
                            }
                        }

                        @Override
                        protected float getInertia()
                        {
                            return type == WitherSkullType.SPEEDER ? AshenmancerIllagerEntity.this.config().ashenmancer.speederSkullSpeed : super.getInertia();
                        }
                    };

                    witherSkull.setOwner(ashenmancer);
                    witherSkull.setPos(aX, aY, aZ);

                    AshenmancerIllagerEntity.this.setLastSkullType(type);
                    level.addFreshEntity(witherSkull);
                }
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !AshenmancerIllagerEntity.this.isTurretActive();
        }

        @Override
        protected int getCastingTime()
        {
            return 30;
        }

        @Override
        protected int getCastingInterval()
        {
            return 60;
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
            return SoundEvents.WITHER_SHOOT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ASHENMANCER_SUMMON_MINIONS;
        }
    }

    private class AshenmancerWitherSkullTurretSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private AshenmancerWitherSkullTurretSpellGoal() {}

        @Override
        protected void castSpell()
        {
            if(AshenmancerIllagerEntity.this.getTarget() != null)
            {
                AshenmancerIllagerEntity.this.turretCount = AshenmancerIllagerEntity.this.config().ashenmancer.turretSkullCount;
                AshenmancerIllagerEntity.this.setTurretActive(true);
            }
        }

        @Override
        protected int getCastingTime()
        {
            return 70;
        }

        @Override
        protected int getCastingInterval()
        {
            return 1000;
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
            return SoundEvents.WITHER_AMBIENT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ASHENMANCER_TURRET;
        }
    }

    private class AshenmancerAshenadoSpellGoal extends SpellcastingIllagerUseSpellGoal
    {
        private AshenmancerAshenadoSpellGoal() {}

        @Override
        protected void castSpell()
        {
            AshenmancerIllagerEntity ashenmancer = AshenmancerIllagerEntity.this;
            LivingEntity target = ashenmancer.getTarget();

            if(target != null)
            {
                BlockPos targetCenter = target.blockPosition().offset(-3 + ashenmancer.random.nextInt(7), 0, -3 + ashenmancer.random.nextInt(7));
                Vec3 center = Vec3.atCenterOf(targetCenter).subtract(0, -0.5, 0);

                ashenmancer.ashenado = new AshenadoObject(ashenmancer,
                        ashenmancer.isInDifficultRaid() ? ashenmancer.getRaidDifficulty() : RaidDifficulty.DEFAULT,
                        center,
                        ashenmancer.config().ashenmancer.ashenadoDuration
                );
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && AshenmancerIllagerEntity.this.ashenado == null && AshenmancerIllagerEntity.this.ashenadoCooldown == 0;
        }

        @Override
        protected int getCastingTime()
        {
            return 60;
        }

        @Override
        protected int getCastingInterval()
        {
            return 1500;
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
            return SoundEvents.WITHER_AMBIENT;
        }

        @Override
        protected SpellType getSpellType()
        {
            return SpellType.ASHENMANCER_ASHENADO;
        }
    }
}
