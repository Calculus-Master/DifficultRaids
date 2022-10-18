package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

import java.util.HashMap;
import java.util.Map;

public class DartIllagerEntity extends AbstractVindicatorVariant
{
    private static final AttributeModifier LAST_RESORT_MOVEMENT_BOOST = new AttributeModifier("last_resort_movement_boost", 1.5, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier CONDUCTOR_LIGHTNING_MOVEMENT_BOOST = new AttributeModifier("conductor_lightining_movement_boost", 1.2, AttributeModifier.Operation.MULTIPLY_TOTAL);

    public DartIllagerEntity(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.42F)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.MAX_HEALTH, 12.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AbstractIllager.RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.3D, true));

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
    public void thunderHit(ServerLevel pLevel, LightningBolt pLightning)
    {
        AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);

        boolean isConductorLightning = pLightning.getCustomName() != null && pLightning.getCustomName().getString().equalsIgnoreCase(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG);
        if(isConductorLightning && movementSpeed != null && !movementSpeed.hasModifier(CONDUCTOR_LIGHTNING_MOVEMENT_BOOST))
            movementSpeed.addPermanentModifier(CONDUCTOR_LIGHTNING_MOVEMENT_BOOST);

        super.thunderHit(pLevel, pLightning);
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        AttributeInstance movementSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED);

        if(this.getHealth() < this.getMaxHealth() / 2 && movementSpeed != null && !movementSpeed.hasModifier(LAST_RESORT_MOVEMENT_BOOST))
            movementSpeed.addPermanentModifier(LAST_RESORT_MOVEMENT_BOOST);

        if(!this.hasEffect(MobEffects.GLOWING) && this.isAggressive())
            this.addEffect(new MobEffectInstance(MobEffects.GLOWING, 50 * 20));
        else if(this.hasEffect(MobEffects.GLOWING) && !this.isAggressive())
            this.removeEffect(MobEffects.GLOWING);
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        ItemStack sword = new ItemStack(Items.GOLDEN_SWORD);

        if(this.isInDifficultRaid())
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            enchants.put(Enchantments.SHARPNESS, this.getRaidDifficulty().config().dart().sharpnessLevel());
            enchants.put(Enchantments.VANISHING_CURSE, 1);

            EnchantmentHelper.setEnchantments(enchants, sword);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(!this.isInRaid()) this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GOLDEN_SWORD));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
