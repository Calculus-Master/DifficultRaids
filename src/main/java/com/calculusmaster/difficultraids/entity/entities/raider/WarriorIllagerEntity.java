package com.calculusmaster.difficultraids.entity.entities.raider;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        List<Item> swordPool = switch(raidDifficulty) {
            case DEFAULT -> List.of(Items.STONE_SWORD);
            case HERO -> List.of(Items.IRON_SWORD);
            case LEGEND -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD);
            case MASTER -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
            case GRANDMASTER -> List.of(Items.NETHERITE_SWORD);
        };

        ItemStack sword = new ItemStack(swordPool.get(this.random.nextInt(swordPool.size())));

        if(!raidDifficulty.isDefault())
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Sharpness
            if(this.random.nextInt() < 0.75)
            {
                enchants.put(Enchantments.SHARPNESS, switch(raidDifficulty) {
                    case DEFAULT -> 0;
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                });
            }

            //Fire Aspect
            if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
            {
                enchants.put(Enchantments.FIRE_ASPECT, switch(raidDifficulty) {
                    case LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 0;
                });
            }

            //Knockback
            if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
            {
                enchants.put(Enchantments.KNOCKBACK, switch(raidDifficulty) {
                    case LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 0;
                });
            }

            //Critical Strike
            if(raidDifficulty.is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER)) enchants.put(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), raidDifficulty.is(RaidDifficulty.MASTER) ? 1 : 2);

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

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
