package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TankIllagerEntity extends AbstractVindicatorVariant
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String TAG_TANK_ARMOR_MODIFIER = "DifficultRaids Tank Armor Modifier";
    private static final String TAG_TANK_TOUGHNESS_MODIFIER = "DifficultRaids Tank Armor Toughness Modifier";

    public TankIllagerEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new HoldGroundAttackGoal(this, 10.0F));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.2D, false));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public void applyRaidBuffs(int p_37844_, boolean p_37845_)
    {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));

        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance armorToughness = this.getAttribute(Attributes.ARMOR_TOUGHNESS);

        if(armor != null)
        {
            AttributeModifier armorModifier = new AttributeModifier(TAG_TANK_ARMOR_MODIFIER,
                    this.config().tank.extraArmor,
                    AttributeModifier.Operation.ADDITION);

            armor.addPermanentModifier(armorModifier);
        }
        else LOGGER.warn("DifficultRaids: Tank Illager has a null Armor Attribute!");

        if(armorToughness != null && this.config().tank.extraArmorToughness > 0)
        {
            AttributeModifier toughnessModifier = new AttributeModifier(TAG_TANK_TOUGHNESS_MODIFIER,
                    this.config().tank.extraArmorToughness,
                    AttributeModifier.Operation.ADDITION);

            armorToughness.addPermanentModifier(toughnessModifier);
        }
        else LOGGER.warn("DifficultRaids: Tank Illager has a null Armor Toughness Attribute!");

        if(this.random.nextFloat() < this.config().tank.thornsChance)
        {
            ItemStack chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE);
            chestplate.enchant(Enchantments.THORNS, 3);
            this.setItemSlot(EquipmentSlot.CHEST, chestplate);
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(!this.isInRaid()) this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
