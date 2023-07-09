package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.Compat;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.List;

public class NuaosEliteEntity extends AbstractIllagerVariant
{
    private final Component ELITE_NAME = Component.translatable("com.calculusmaster.difficultraids.elite_event.nuaos");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private static final EntityDataAccessor<Float> STORED_CHARGE_DAMAGE = SynchedEntityData.defineId(NuaosEliteEntity.class, EntityDataSerializers.FLOAT);

    private int ticksLastDamageTaken;

    public NuaosEliteEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);

        this.ticksLastDamageTaken = 0;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(2, new NuaosMeleeAttackGoal(this, 1.0D, true));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(STORED_CHARGE_DAMAGE, 0.0F);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof IronGolem || (Compat.GUARD_VILLAGERS.isLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= this.config().nuaos.friendlyDamageReduction;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        RaidDifficultyConfig cfg = this.config();

        //Weapons
        ItemStack sword = this.getMainHandItem();

        sword.enchant(Enchantments.SHARPNESS, cfg.nuaos.sharpnessLevel);
        sword.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), cfg.nuaos.criticalBurstLevel);
        sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.nuaos.criticalStrikeLevel);

        this.setItemInHand(InteractionHand.MAIN_HAND, sword);
        this.setDropChance(EquipmentSlot.MAINHAND, cfg.nuaos.swordDropChance);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    class NuaosMeleeAttackGoal extends MeleeAttackGoal
    {
        private NuaosMeleeAttackGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen)
        {
            super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr)
        {
            AttributeModifier damageBoost = new AttributeModifier("NUAOS_CHARGED_DAMAGE_BOOST",
                    NuaosEliteEntity.this.config().nuaos.chargedDamageBoost.get(NuaosEliteEntity.this.getChargeState().ordinal()),
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            AttributeInstance attackDamageInstance = NuaosEliteEntity.this.getAttribute(Attributes.ATTACK_DAMAGE);

            if(attackDamageInstance != null) attackDamageInstance.addTransientModifier(damageBoost);
            super.checkAndPerformAttack(pEnemy, pDistToEnemySqr);
            if(attackDamageInstance != null) attackDamageInstance.removeModifier(damageBoost);
        }
    }

    //Elite Event Stuff

    public enum ChargeState { NO_CHARGE, LOW_CHARGE, HIGH_CHARGE, MAX_CHARGE }

    public void increaseChargedDamage(float amount)
    {
        ChargeState prevState = this.getChargeState();
        this.entityData.set(STORED_CHARGE_DAMAGE, this.getStoredChargeDamage() + amount);

        if(prevState != this.getChargeState()) this.playSound(SoundEvents.SHROOMLIGHT_PLACE, 2.0F, 1.0F);
    }

    public void resetStoredChargeDamage()
    {
        this.entityData.set(STORED_CHARGE_DAMAGE, 0.0F);
    }

    public void resetLastDamageTakenTicks()
    {
        this.ticksLastDamageTaken = 0;
    }

    public float getStoredChargeDamage()
    {
        return this.entityData.get(STORED_CHARGE_DAMAGE);
    }

    public float getMaxChargeDamage()
    {
        return this.isInDifficultRaid() ? this.getRaidDifficulty().config().nuaos.maxChargeDamage : 50.0F;
    }

    public ChargeState getChargeState()
    {
        double percentCharged = this.getStoredChargeDamage() / this.getMaxChargeDamage();

        if(percentCharged < 0.05F) return ChargeState.NO_CHARGE;
        else if(percentCharged < 0.3F) return ChargeState.LOW_CHARGE;
        else if(percentCharged < 0.8F) return ChargeState.HIGH_CHARGE;
        else return ChargeState.MAX_CHARGE;
    }

    @Override
    public void tick()
    {
        super.tick();

        if(this.tickCount % 8 == 0)
        {
            ChargeState charge = this.getChargeState();
            if(charge == ChargeState.HIGH_CHARGE || charge == ChargeState.MAX_CHARGE)
                for(int i = 0; i < 3; i++)
                {
                    BlockPos particlePos = new BlockPos(this.getEyePosition()).offset(0.25 + 0.4 - this.random.nextFloat() * 0.8, 0.25 + this.random.nextFloat() * 0.1,0.25 + 0.4 - this.random.nextFloat() * 0.8);
                    this.level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 0.05, 0.2, 0.05);
                }
        }
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.ELITE_EVENT.setProgress(this.getHealth() / this.getMaxHealth());

        RaidDifficulty rd = this.isInDifficultRaid() ? this.getRaidDifficulty() : RaidDifficulty.DEFAULT;

        if(this.config().nuaos.chargeDecay && this.ticksLastDamageTaken++ > 20 * 10)
        {
            float decayPercent = (this.random.nextFloat() * 0.1F) + 0.005F;

            float stored = this.getStoredChargeDamage() - (this.getMaxChargeDamage() * decayPercent);

            if(stored <= 0) this.resetStoredChargeDamage();
            else this.entityData.set(STORED_CHARGE_DAMAGE, stored);
        }

        if(this.getStoredChargeDamage() >= this.getMaxChargeDamage())
        {
            this.resetStoredChargeDamage();

            this.playSound(SoundEvents.GENERIC_EXPLODE, 1.2F, 0.8F);

            List<EntityType<? extends LivingEntity>> canReceiveDamage = new ArrayList<>(List.of(EntityType.VILLAGER, EntityType.PLAYER, EntityType.IRON_GOLEM));
            if(Compat.GUARD_VILLAGERS.isLoaded()) canReceiveDamage.add(GuardEntityType.GUARD.get());

            double shockwaveRadius = rd.config().nuaos.shockwaveRadius;

            AABB shockwaveAABB = new AABB(this.blockPosition()).inflate(shockwaveRadius);
            List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, shockwaveAABB, entity -> canReceiveDamage.stream().anyMatch(type -> entity.getType().equals(type)));

            if(!targets.isEmpty())
            {
                float minDamage = this.getMaxChargeDamage() * 0.25F;
                float falloffStartDistance = 0.05F;

                for(LivingEntity t : targets)
                {
                    double distance = this.distanceTo(t);
                    float damage;

                    if(distance <= falloffStartDistance) damage = this.getMaxChargeDamage();
                    else
                    {
                        float falloffDamage = (float)((1.0F - (distance - falloffStartDistance) / shockwaveRadius) * this.getMaxChargeDamage());
                        damage = Math.max(falloffDamage, minDamage);
                    }

                    t.hurt(DamageSource.mobAttack(this), damage);
                }
            }
        }

        if(this.tickCount % 80 == 0)
        {
            AABB auraRadius = new AABB(this.blockPosition()).inflate(rd.config().nuaos.buffAuraRadius);

            int strengthAmplifier = rd.config().nuaos.buffAuraStrengthLevel;

            this.level.getEntitiesOfClass(Raider.class, auraRadius, r -> DifficultRaidsUtil.STANDARD_RAIDERS.contains(r.getType())).forEach(raider ->
            {
                if(!raider.hasEffect(MobEffects.DAMAGE_BOOST))
                    raider.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 10, strengthAmplifier, false, true));
            });

            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SPLASH_POTION_BREAK, SoundSource.HOSTILE, 3.0F, 1.0F, false);
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer)
    {
        super.startSeenByPlayer(pPlayer);
        this.ELITE_EVENT.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer)
    {
        super.stopSeenByPlayer(pPlayer);
        this.ELITE_EVENT.removePlayer(pPlayer);
    }

    //Arm Pose & Sounds

    @Override
    public IllagerArmPose getArmPose()
    {
        return this.isAggressive() ? IllagerArmPose.ATTACKING : super.getArmPose();
    }

    //Default Sounds
    @Override
    public SoundEvent getCelebrateSound()
    {
        return SoundEvents.VINDICATOR_CELEBRATE;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.VINDICATOR_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.VINDICATOR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_)
    {
        return SoundEvents.VINDICATOR_HURT;
    }
}
