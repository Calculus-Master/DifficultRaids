package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class DartIllagerEntity extends AbstractIllager
{
    public DartIllagerEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.60F)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AbstractIllager.RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.8D, true));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Pig.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Chicken.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if(this.getHealth() < this.getMaxHealth() / 2 && !this.hasEffect(MobEffects.MOVEMENT_SPEED))
            this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3));

        if(!this.hasEffect(MobEffects.GLOWING) && this.isAggressive())
            this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 50 * 20));
        else if(this.hasEffect(MobEffects.GLOWING) && !this.isAggressive())
            this.removeEffect(MobEffects.GLOWING);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);

        if(!raidDifficulty.ignore())
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            int sharpnessLevel = switch(raidDifficulty) {
                case HERO -> 2;
                case LEGEND -> 3;
                case MASTER -> 4;
                case APOCALYPSE -> 5;
                default -> 0;
            };

            enchants.put(Enchantments.SHARPNESS, sharpnessLevel);

            EnchantmentHelper.setEnchantments(enchants, sword);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }

    @Override
    public IllagerArmPose getArmPose()
    {
        return this.isAggressive() ? IllagerArmPose.ATTACKING : (this.isCelebrating() ? IllagerArmPose.CELEBRATING : IllagerArmPose.CROSSED);
    }

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
