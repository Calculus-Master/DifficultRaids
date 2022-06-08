package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
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

import java.util.HashMap;
import java.util.Map;

public class TankIllagerEntity extends AbstractVindicatorVariant
{
    public TankIllagerEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
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
        RaidDifficulty raidDifficulty = this.getRaidDifficulty();
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));

        ItemStack helmet = null, chestplate = null, leggings = null, boots = null;

        if(raidDifficulty.is(RaidDifficulty.DEFAULT)) { helmet = new ItemStack(Items.LEATHER_HELMET); chestplate = new ItemStack(Items.LEATHER_CHESTPLATE); leggings = new ItemStack(Items.LEATHER_LEGGINGS); boots = new ItemStack(Items.LEATHER_BOOTS); }
        else if(raidDifficulty.is(RaidDifficulty.HERO, RaidDifficulty.LEGEND)) { helmet = new ItemStack(Items.IRON_HELMET); chestplate = new ItemStack(Items.IRON_CHESTPLATE); leggings = new ItemStack(Items.IRON_LEGGINGS); boots = new ItemStack(Items.IRON_BOOTS); }
        else if(raidDifficulty.is(RaidDifficulty.MASTER)) { helmet = new ItemStack(Items.DIAMOND_HELMET); chestplate = new ItemStack(Items.DIAMOND_CHESTPLATE); leggings = new ItemStack(Items.DIAMOND_LEGGINGS); boots = new ItemStack(Items.DIAMOND_BOOTS); }
        else if(raidDifficulty.is(RaidDifficulty.GRANDMASTER)) { helmet = new ItemStack(Items.NETHERITE_HELMET); chestplate = new ItemStack(Items.NETHERITE_CHESTPLATE); leggings = new ItemStack(Items.NETHERITE_LEGGINGS); boots = new ItemStack(Items.NETHERITE_BOOTS); }

        final Map<Enchantment, Integer> enchants = new HashMap<>();

        enchants.put(Enchantments.ALL_DAMAGE_PROTECTION, raidDifficulty.config().tank().protectionLevel());
        if(this.random.nextInt(100) < 10 && raidDifficulty.config().tank().thornsLevel() > 0) enchants.put(Enchantments.THORNS, raidDifficulty.config().tank().thornsLevel());

        if(helmet != null && chestplate != null && leggings != null && boots != null)
        {
            EnchantmentHelper.setEnchantments(Map.copyOf(enchants), helmet);
            EnchantmentHelper.setEnchantments(Map.copyOf(enchants), chestplate);
            EnchantmentHelper.setEnchantments(Map.copyOf(enchants), leggings);
            EnchantmentHelper.setEnchantments(Map.copyOf(enchants), boots);

            this.setItemSlot(EquipmentSlot.HEAD, helmet);
            this.setItemSlot(EquipmentSlot.CHEST, chestplate);
            this.setItemSlot(EquipmentSlot.LEGS, leggings);
            this.setItemSlot(EquipmentSlot.FEET, boots);
        }
    }
}
