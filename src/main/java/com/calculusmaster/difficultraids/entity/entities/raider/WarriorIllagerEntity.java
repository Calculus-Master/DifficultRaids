package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.mojang.logging.LogUtils;
import net.minecraft.util.Tuple;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarriorIllagerEntity extends AbstractVindicatorVariant
{
    public WarriorIllagerEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AbstractIllager.RaiderOpenDoorGoal(this));
        this.goalSelector.addGoal(2, new Raider.HoldGroundAttackGoal(this, 10.0F));
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
        RaidDifficulty raidDifficulty = RaidDifficulty.current();

        List<Item> swordPool = raidDifficulty.config().warrior().possibleSwords();
        ItemStack sword = new ItemStack(swordPool.get(this.random.nextInt(swordPool.size())));

        if(!raidDifficulty.isDefault())
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Sharpness
            if(this.random.nextInt(100) < raidDifficulty.config().warrior().sharpnessChance())
            {
                Tuple<Integer, Integer> minMaxLevel = raidDifficulty.config().warrior().sharpnessLevel();

                if(minMaxLevel.getB() > minMaxLevel.getA())
                {
                    minMaxLevel.setA(1);
                    minMaxLevel.setB(1);
                    LogUtils.getLogger().warn("Invalid config option for Warrior Illager Sharpness Level! Minimum is greater than the maximum! Defaulting to a Sharpness Level of 1.");
                }

                int sharpnessLevel = minMaxLevel.getA().equals(minMaxLevel.getB()) ? minMaxLevel.getA() : this.random.nextInt(minMaxLevel.getA(), minMaxLevel.getB() + 1);

                enchants.put(Enchantments.SHARPNESS, sharpnessLevel);
            }

            //Fire Aspect
            if(this.random.nextInt(100) < raidDifficulty.config().warrior().fireAspectChance())
                enchants.put(Enchantments.FIRE_ASPECT, raidDifficulty.config().warrior().fireAspectLevel());

            //Knockback
            if(this.random.nextInt(100) < raidDifficulty.config().warrior().knockbackChance())
                enchants.put(Enchantments.KNOCKBACK, raidDifficulty.config().warrior().knockbackLevel());

            if(!sword.is(Items.IRON_SWORD) && !sword.is(Items.STONE_SWORD)) enchants.put(Enchantments.VANISHING_CURSE, 1);

            EnchantmentHelper.setEnchantments(enchants, sword);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }
}
