package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractPillagerVariant;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

public class AssassinIllagerEntity extends AbstractPillagerVariant
{
    private int invisibilityCooldown = 0;
    private int teleportCooldown = 0;

    public AssassinIllagerEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, true));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Pig.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        if(Compat.GUARD_VILLAGERS.isLoaded()) this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Guard.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getDirectEntity() instanceof IronGolem) pAmount *= 0.25;
        else if(Compat.GUARD_VILLAGERS.isLoaded() && pSource.getEntity() instanceof Guard) pAmount *= 0.5;

        if(!this.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 10, 1, false, true, true));

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        //Cooldowns
        if(this.invisibilityCooldown > 0)
        {
            this.invisibilityCooldown--;

            if(this.invisibilityCooldown == 0) this.addInvisibilityEffect();
        }

        if(this.teleportCooldown > 0) this.teleportCooldown--;

        LivingEntity target = this.getTarget();

        //Teleport Attack
        if(target != null)
        {
            if(this.distanceTo(target) > 8 && this.canTeleport() && this.random.nextFloat() < 0.25F)
            {
                BlockPos targetPos = new BlockPos(target.getEyePosition()).offset(this.random.nextInt(5) - 2, 0, this.random.nextInt(5) - 2);
                this.randomTeleport(targetPos.getX(), targetPos.getY(), targetPos.getZ(), true);

                this.teleportCooldown = this.config().assassin.teleportCooldown;
            }

            if(this.hasEffect(MobEffects.INVISIBILITY))
            {
                this.removeEffect(MobEffects.INVISIBILITY);
                this.invisibilityCooldown = this.config().assassin.invisibilityCooldown;
            }
        }
    }

    private boolean canTeleport()
    {
        return this.teleportCooldown == 0;
    }

    private void addInvisibilityEffect()
    {
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 60 * 60 * 24));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        ItemStack sword = new ItemStack(Items.STONE_SWORD);

        sword.enchant(Enchantments.SHARPNESS, Enchantments.SHARPNESS.getMaxLevel());
        sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), DifficultRaidsEnchantments.CRITICAL_STRIKE.get().getMaxLevel());
        sword.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), DifficultRaidsEnchantments.CRITICAL_BURST.get().getMaxLevel());

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
        this.setDropChance(EquipmentSlot.MAINHAND, 0);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(!this.isInRaid()) this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));

        this.addInvisibilityEffect();
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
