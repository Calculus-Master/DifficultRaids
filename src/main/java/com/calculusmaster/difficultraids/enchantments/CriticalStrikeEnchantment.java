package com.calculusmaster.difficultraids.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class CriticalStrikeEnchantment extends Enchantment
{
    public CriticalStrikeEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots)
    {
        super(rarity, category, slots);
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @Override
    public int getMaxLevel()
    {
        return 2;
    }

    @Override
    public int getMinCost(int pLevel)
    {
        return 5 + (pLevel - 1) * 9;
    }

    @Override
    public int getMaxCost(int pLevel)
    {
        return this.getMinCost(pLevel) + 10;
    }
}
