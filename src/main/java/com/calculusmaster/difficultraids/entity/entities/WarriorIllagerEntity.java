package com.calculusmaster.difficultraids.entity.entities;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WarriorIllagerEntity extends AbstractIllager
{
    public WarriorIllagerEntity(EntityType<? extends AbstractIllager> entityType, Level level)
    {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.45F)
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
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        //TODO: DR RaidDifficulty Raids versus Normal Vanilla Raids (RaidDifficulty.DEFAULT)
        List<Item> swordPool = switch(raidDifficulty) {
            case HERO -> List.of(Items.STONE_SWORD, Items.IRON_SWORD);
            case LEGEND -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD);
            case MASTER -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
            case APOCALYPSE -> List.of(Items.NETHERITE_SWORD);
            default -> List.of(Items.GOLDEN_SWORD);
        };

        ItemStack sword = new ItemStack(swordPool.get(this.random.nextInt(swordPool.size())));

        if(!raidDifficulty.equals(RaidDifficulty.DEFAULT) && !raidDifficulty.equals(RaidDifficulty.DEBUG))
        {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Sharpness
            int sharpnessChance = switch(raidDifficulty) {
                case HERO -> 20;
                case LEGEND -> 35;
                case MASTER -> 45;
                case APOCALYPSE -> 90;
                default -> 0;
            };

            if(this.random.nextInt(100) < sharpnessChance)
            {
                int sharpnessLevel = switch(raidDifficulty) {
                    case HERO -> this.random.nextInt(1, 4);
                    case LEGEND -> this.random.nextInt(2, 5);
                    case MASTER -> this.random.nextInt(3, 6);
                    case APOCALYPSE -> this.random.nextInt(5, 7);
                    default -> 0;
                };

                enchants.put(Enchantments.SHARPNESS, sharpnessLevel);
            }

            //Fire Aspect
            int fireAspectChance = switch(raidDifficulty) {
                case HERO -> 5;
                case LEGEND -> 10;
                case MASTER -> 15;
                case APOCALYPSE -> 50;
                default -> 0;
            };

            if(this.random.nextInt(100) < fireAspectChance)
            {
                int fireAspectLevel = switch(raidDifficulty) {
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case APOCALYPSE -> 3;
                    default -> 0;
                };

                enchants.put(Enchantments.FIRE_ASPECT, fireAspectLevel);
            }

            //Knockback
            int knockbackChance = switch(raidDifficulty) {
                case HERO -> 10;
                case LEGEND -> 15;
                case MASTER -> 20;
                case APOCALYPSE -> 90;
                default -> 0;
            };

            if(this.random.nextInt(100) < knockbackChance)
            {
                int knockbackLevel = switch(raidDifficulty) {
                    case HERO -> 1;
                    case LEGEND, MASTER -> 2;
                    case APOCALYPSE -> 3;
                    default -> 0;
                };

                enchants.put(Enchantments.KNOCKBACK, knockbackLevel);
            }

            if(!sword.is(Items.IRON_SWORD) && !sword.is(Items.STONE_SWORD)) enchants.put(Enchantments.VANISHING_CURSE, 1);

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
