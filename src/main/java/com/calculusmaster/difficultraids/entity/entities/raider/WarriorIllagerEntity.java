package com.calculusmaster.difficultraids.entity.entities.raider;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractVindicatorVariant;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
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
import org.jetbrains.annotations.Nullable;

public class WarriorIllagerEntity extends AbstractVindicatorVariant
{
    public WarriorIllagerEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
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
        RaidDifficulty raidDifficulty = this.getRaidDifficulty();
        RaidDifficultyConfig cfg = raidDifficulty.config();

        ItemStack sword = new ItemStack(cfg.warrior.getSword());

        if(!raidDifficulty.isDefault())
        {
            //Sharpness
            sword.enchant(Enchantments.SHARPNESS, cfg.warrior.sharpnessLevel);

            //Fire Aspect
            if(this.random.nextFloat() < cfg.warrior.fireAspectChance)
                sword.enchant(Enchantments.FIRE_ASPECT, cfg.warrior.fireAspectLevel);

            //Knockback
            if(this.random.nextFloat() < cfg.warrior.knockbackChance)
                sword.enchant(Enchantments.KNOCKBACK, cfg.warrior.knockbackLevel);

            //Critical Strike
            sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.warrior.criticalStrikeLevel);

            this.setDropChance(EquipmentSlot.MAINHAND, cfg.warrior.swordDropChance);
        }

        this.setItemSlot(EquipmentSlot.MAINHAND, sword);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag)
    {
        if(!this.isInRaid()) this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
