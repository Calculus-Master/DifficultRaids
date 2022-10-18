package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractPillagerVariant;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
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
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import tallestegg.guardvillagers.entities.Guard;

import java.util.HashMap;
import java.util.Map;

public class AssassinIllagerEntity extends AbstractPillagerVariant
{
    private int invisibilityCooldown = 0;

    public AssassinIllagerEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.40F)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.6D, true));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Pig.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Chicken.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount)
    {
        if(pSource.getDirectEntity() instanceof IronGolem) pAmount *= 0.1;
        else if(DifficultRaidsUtil.isGuardVillagersLoaded() && pSource.getEntity() instanceof Guard) pAmount *= 0.25;

        return super.hurt(pSource, pAmount);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if(this.invisibilityCooldown > 0)
        {
            this.invisibilityCooldown--;

            if(this.invisibilityCooldown == 0) this.addInvisibilityEffect();
        }

        LivingEntity target = this.getTarget();

        if(target != null)
        {
            //TODO: Balancing pass on Assassin Teleport
            if(this.distanceTo(target) > 5 && this.random.nextInt(100) < 25)
            {
                target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 1));

                BlockPos targetPos = target.eyeBlockPosition();
                this.randomTeleport(targetPos.getX(), targetPos.getY(), targetPos.getZ(), true);
            }

            if(this.hasEffect(MobEffects.INVISIBILITY))
            {
                this.removeEffect(MobEffects.INVISIBILITY);
                this.invisibilityCooldown = switch(this.level.getDifficulty()) {
                    case PEACEFUL -> 0;
                    case EASY -> 20 * 60 * 5;
                    case NORMAL -> 20 * 60 * 3;
                    case HARD -> 20 * 60 * 1;
                };
            }
        }
    }

    private void addInvisibilityEffect()
    {
        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 60 * 60 * 24));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        ItemStack sword = new ItemStack(Items.IRON_SWORD);

        if(this.isInDifficultRaid())
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            enchants.put(Enchantments.SHARPNESS, this.getRaidDifficulty().config().assassin().sharpnessLevel());
            enchants.put(Enchantments.VANISHING_CURSE, 1);

            EnchantmentHelper.setEnchantments(enchants, sword);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
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
