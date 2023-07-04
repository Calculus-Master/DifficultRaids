package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
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
        if(pSource.getEntity() instanceof IronGolem || (DifficultRaidsUtil.isGuardVillagersLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= 0.4;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        RaidDifficulty raidDifficulty = this.isInDifficultRaid() ? this.getRaidDifficulty() : RaidDifficulty.DEFAULT;

        //Weapons
        ItemStack sword = this.getMainHandItem();
        sword.enchant(Enchantments.SHARPNESS, switch(raidDifficulty)
        {
            case DEFAULT, HERO -> 2;
            case LEGEND -> 3;
            case MASTER -> 4;
            case GRANDMASTER -> 5;
        });

        sword.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), switch(raidDifficulty)
        {
            case DEFAULT, HERO -> 5;
            case LEGEND -> 6;
            case MASTER -> 8;
            case GRANDMASTER -> 10;
        });

        if(raidDifficulty.is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
            sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), switch(raidDifficulty)
            {
                case DEFAULT, HERO, LEGEND -> 0;
                case MASTER -> 1;
                case GRANDMASTER -> 3;
            });

        this.setItemInHand(InteractionHand.MAIN_HAND, sword);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND));

        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        ItemStack sword = new ItemStack(this.isInRaid() && this.getRaidDifficulty().is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER) ? Items.NETHERITE_SWORD : Items.DIAMOND_SWORD);

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);

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
            AttributeModifier damageBoost = new AttributeModifier("NUAOS_CHARGED_DAMAGE_BOOST", switch(NuaosEliteEntity.this.getChargeState())
            {
                case NO_CHARGE -> 1.0F;
                case LOW_CHARGE -> 1.25F;
                case HIGH_CHARGE -> 1.75F;
                case MAX_CHARGE -> 2.25F;
            }, AttributeModifier.Operation.MULTIPLY_TOTAL);

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
        return 50.0F;
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

        if(this.ticksLastDamageTaken++ > 20 * 10)
        {
            float decayPercent = (this.random.nextFloat() * 0.1F) + 0.005F;

            float stored = this.getStoredChargeDamage() - (this.getMaxChargeDamage() * decayPercent);

            if(stored <= 0) this.resetStoredChargeDamage();
            else this.entityData.set(STORED_CHARGE_DAMAGE, stored);
        }

        if(this.getStoredChargeDamage() >= this.getMaxChargeDamage())
        {
            this.resetStoredChargeDamage();

            this.playSound(SoundEvents.GENERIC_EXPLODE, 0.8F, 0.8F);

            List<EntityType<? extends LivingEntity>> canReceiveDamage = new ArrayList<>(List.of(EntityType.VILLAGER, EntityType.PLAYER, EntityType.IRON_GOLEM));
            if(DifficultRaidsUtil.isGuardVillagersLoaded()) canReceiveDamage.add(GuardEntityType.GUARD.get());

            double shockwaveRadius;
            if(this.isInRaid()) shockwaveRadius = switch(this.getRaidDifficulty()) {
                case DEFAULT, HERO, LEGEND -> 4.0D;
                case MASTER -> 5.0D;
                case GRANDMASTER -> 7.0D;
            };
            else shockwaveRadius = 4.0D;

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
            AABB auraRadius = new AABB(this.blockPosition()).inflate(8.0D);

            List<EntityType<? extends AbstractIllager>> effectTargets = List.of(
                    EntityType.VINDICATOR, EntityType.PILLAGER,
                    DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), DifficultRaidsEntityTypes.TANK_ILLAGER.get(),
                    DifficultRaidsEntityTypes.DART_ILLAGER.get()
            );

            int strengthAmplifier = this.isInDifficultRaid() ? switch(this.getRaidDifficulty()) {
                case DEFAULT, HERO, LEGEND -> 1;
                case MASTER -> 2;
                case GRANDMASTER -> 3;
            } : 1;

            this.level.getEntitiesOfClass(Raider.class, auraRadius, r -> effectTargets.contains(r.getType())).forEach(raider ->
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
