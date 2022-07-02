package com.calculusmaster.difficultraids.enchantments;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class InvisibilityEnchantment extends Enchantment
{
    public InvisibilityEnchantment(Rarity rarity, EnchantmentCategory category, EquipmentSlot... slots)
    {
        super(rarity, category, slots);
    }

    @Override
    public boolean isTreasureOnly()
    {
        return true;
    }

    @Override
    public void doPostHurt(LivingEntity pUser, Entity pAttacker, int pLevel)
    {
        float chance = switch(pLevel) {
            case 1 -> 0.05F;
            case 2 -> 0.1F;
            case 3 -> 0.2F;
            default -> 0.0F;
        };

        int duration = 20 * (5 * pLevel) + (pAttacker instanceof Raider ? 20 * 3 : 0);

        if(!pUser.hasEffect(MobEffects.INVISIBILITY) && pUser.getRandom().nextFloat() < chance)
            pUser.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 1));
    }

    @Override
    public int getMaxLevel()
    {
        return 3;
    }

    @Override
    public int getMinCost(int pLevel)
    {
        return 5 + (pLevel - 1) * 10;
    }

    @Override
    public int getMaxCost(int pLevel)
    {
        return this.getMinCost(pLevel) + 30;
    }
}
