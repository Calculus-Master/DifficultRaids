package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tuple;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin extends Raider
{
    //Default Constructor
    protected AbstractIllagerMixin(EntityType<? extends Raider> p_37839_, Level p_37840_)
    {
        super(p_37839_, p_37840_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
                                        MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.current();
        Random random = new Random();

        if(!raidDifficulty.isDefault())
        {
            if(this.getCurrentRaid() != null && mobSpawnType.equals(MobSpawnType.EVENT) && !this.getType().equals(DifficultRaidsEntityTypes.TANK_ILLAGER.get()))
            {
                List<ArmorMaterials> tiers = raidDifficulty.config().validArmorTiers();
                int armorChance = raidDifficulty.config().armorChance();
                int protectionChance = raidDifficulty.config().protectionChance();
                int maxArmorPieces = raidDifficulty.config().maxArmorPieces();

                if(!tiers.isEmpty() && armorChance > 0 && maxArmorPieces > 0)
                {
                    int armorCount = 0;

                    for(EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET))
                    {
                        if(armorCount < maxArmorPieces && !tiers.isEmpty() && random.nextInt(100) < armorChance)
                        {
                            ArmorMaterials mat = tiers.get(random.nextInt(tiers.size()));
                            ItemStack armor = DifficultRaidsUtil.getArmorPiece(slot, mat);

                            if(!armor.getItem().equals(Items.AIR))
                            {
                                Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(armor);

                                if(random.nextInt(100) < protectionChance)
                                {
                                    Tuple<Integer, Integer> minMaxLevel = raidDifficulty.config().protectionLevel();

                                    if(minMaxLevel.getB() > minMaxLevel.getA())
                                    {
                                        minMaxLevel.setA(1);
                                        minMaxLevel.setB(1);
                                        LogUtils.getLogger().warn("Invalid config option for Abstract Illager Protection Level! Minimum is greater than the maximum! Defaulting to a Protection Level of 1.");
                                    }

                                    enchants.put(Enchantments.ALL_DAMAGE_PROTECTION, minMaxLevel.getA().equals(minMaxLevel.getB()) ? minMaxLevel.getA() : random.nextInt(minMaxLevel.getA(), minMaxLevel.getB() + 1));
                                }

                                //So the armor doesn't drop on death
                                enchants.put(Enchantments.VANISHING_CURSE, 1);

                                EnchantmentHelper.setEnchantments(enchants, armor);
                                this.setItemSlot(slot, armor);
                            }

                            armorCount++;
                        }
                    }
                }
            }
        }

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, tag);
    }
}
