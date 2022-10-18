package com.calculusmaster.difficultraids.entity.entities.elite;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NuaosEliteEntity extends AbstractIllagerVariant
{
    private final TextComponent ELITE_NAME = new TextComponent("Nuaos, The Chosen");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.PROGRESS);

    private final float maxChargedDamage;
    private float chargedDamage;
    private int ticksLastDamageTaken;

    private static final AttributeModifier NUAOS_CHARGED_DAMAGE_BOOST = new AttributeModifier("Nuaos Charged Damage Boost", 1.25D, AttributeModifier.Operation.MULTIPLY_BASE);

    public NuaosEliteEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);

        this.chargedDamage = 0;
        this.maxChargedDamage = 30.0F;
        this.ticksLastDamageTaken = 0;
    }

    public static AttributeSupplier.Builder createEliteAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 120.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
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
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getEntity() instanceof IronGolem || (DifficultRaidsUtil.isGuardVillagersLoaded() && pSource.getEntity() instanceof Guard))
            pAmount *= 0.4;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        //TODO: Unique Nuaos Armor and Weapon

        //Armor
        Map<Enchantment, Integer> generalEnchants = new HashMap<>();
        generalEnchants.put(Enchantments.ALL_DAMAGE_PROTECTION, 3);
        generalEnchants.put(Enchantments.VANISHING_CURSE, 1);

        ItemStack helm = new ItemStack(Items.DIAMOND_HELMET);
        ItemStack chest = new ItemStack(Items.DIAMOND_CHESTPLATE);
        ItemStack legs = new ItemStack(Items.DIAMOND_LEGGINGS);
        ItemStack boots = new ItemStack(Items.DIAMOND_BOOTS);

        EnchantmentHelper.setEnchantments(generalEnchants, helm);
        EnchantmentHelper.setEnchantments(generalEnchants, chest);
        EnchantmentHelper.setEnchantments(generalEnchants, legs);
        EnchantmentHelper.setEnchantments(generalEnchants, boots);

        this.setItemSlot(EquipmentSlot.HEAD, helm);
        this.setItemSlot(EquipmentSlot.CHEST, chest);
        this.setItemSlot(EquipmentSlot.LEGS, legs);
        this.setItemSlot(EquipmentSlot.FEET, boots);

        //Weapons
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 2);

        this.setItemInHand(InteractionHand.MAIN_HAND, sword);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit)
    {
        this.spawnAtLocation(this.getItemInHand(InteractionHand.MAIN_HAND));

        //TODO: Nuaos Unique Raid Loot - the Totem reward is temporary
        this.spawnAtLocation(new ItemStack(DifficultRaidsItems.TOTEM_OF_PROTECTION.get()));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        ItemStack sword = new ItemStack(this.isInRaid() && this.getRaidDifficulty().is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER) ? Items.NETHERITE_SWORD : Items.DIAMOND_SWORD);

        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantments.SHARPNESS, 3);
        EnchantmentHelper.setEnchantments(enchants, sword);

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
            int attackTimes = NuaosEliteEntity.this.random.nextInt(2, 6);
            for(int i = 0; i < attackTimes; i++) super.checkAndPerformAttack(pEnemy, pDistToEnemySqr);
        }
    }

    //Elite Event Stuff

    public enum ChargeState { NO_CHARGE, LOW_CHARGE, HIGH_CHARGE, MAX_CHARGE }

    public void increaseChargedDamage(float amount)
    {
        ChargeState prevState = this.getChargeState();
        this.chargedDamage += amount;

        if(prevState != this.getChargeState()) this.playSound(SoundEvents.SHROOMLIGHT_PLACE, 1.0F, 1.0F);
    }

    public void resetChargedDamage()
    {
        this.chargedDamage = 0.0F;
    }

    public void resetLastDamageTakenTicks()
    {
        this.ticksLastDamageTaken = 0;
    }

    public ChargeState getChargeState()
    {
        double percentCharged = this.chargedDamage / this.maxChargedDamage;

        if(percentCharged < 0.15D) return ChargeState.NO_CHARGE;
        else if(percentCharged < 0.5D) return ChargeState.LOW_CHARGE;
        else if(percentCharged < 0.9D) return ChargeState.HIGH_CHARGE;
        else return ChargeState.MAX_CHARGE;
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        this.ELITE_EVENT.setProgress(this.getHealth() / this.getMaxHealth());

        AttributeInstance attackDamageAttribute = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if(attackDamageAttribute != null)
        {
            boolean hasModifier = attackDamageAttribute.hasModifier(NUAOS_CHARGED_DAMAGE_BOOST);
            if(!this.getChargeState().equals(ChargeState.NO_CHARGE) && !hasModifier)
                attackDamageAttribute.addTransientModifier(NUAOS_CHARGED_DAMAGE_BOOST);
            else if(hasModifier)
                attackDamageAttribute.removeModifier(NUAOS_CHARGED_DAMAGE_BOOST);
        }

        if(this.ticksLastDamageTaken++ > 20 * 5)
        {
            float decayPercent = this.random.nextFloat(0.1F) + 0.005F;
            this.chargedDamage -= this.maxChargedDamage * decayPercent;
            if(this.chargedDamage < 0) this.resetChargedDamage();
        }

        if(this.chargedDamage >= this.maxChargedDamage)
        {
            this.resetChargedDamage();

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
                float minDamage = this.maxChargedDamage * 0.25F;
                double falloffStart = shockwaveRadius * 0.1;
                
                for(LivingEntity t : targets)
                {
                    double distance = this.distanceTo(t);
                    float damage;

                    if(distance <= falloffStart) damage = this.maxChargedDamage;
                    else
                    {
                        float falloffDamage = (float)((1.0F - (distance - falloffStart) / shockwaveRadius) * this.maxChargedDamage);
                        damage = Math.max(falloffDamage, minDamage);
                    }

                    t.hurt(DamageSource.mobAttack(this), damage);
                }
            }
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
