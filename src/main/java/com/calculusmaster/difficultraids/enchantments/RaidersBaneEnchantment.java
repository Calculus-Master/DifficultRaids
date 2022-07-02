package com.calculusmaster.difficultraids.enchantments;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class RaidersBaneEnchantment extends Enchantment
{
    public RaidersBaneEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots)
    {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public float getDamageBonus(int pLevel, MobType pType)
    {
        return pType.equals(MobType.ILLAGER) ? 1.0F + pLevel * 0.25F : 0.0F;
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    //Bane of Arthropods Values

    @Override
    public int getMinCost(int pLevel)
    {
        return 5 + (pLevel - 1) * 8;
    }

    @Override
    public int getMaxCost(int pLevel)
    {
        return this.getMinCost(pLevel) + 20;
    }

    @Override
    public int getMaxLevel()
    {
        return 5;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther)
    {
        return !(pOther instanceof DamageEnchantment);
    }

    @Override
    public boolean canEnchant(ItemStack pStack)
    {
        return pStack.getItem() instanceof AxeItem || super.canEnchant(pStack);
    }
}
