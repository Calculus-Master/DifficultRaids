package com.calculusmaster.difficultraids.mixins.compat;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import baguchan.hunterillager.init.HunterItems;
import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.Compat;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HunterIllagerEntity.class)
public abstract class HunterIllagerMixin extends AbstractIllager
{
    @Shadow @Final private SimpleContainer inventory;

    private HunterIllagerMixin(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs", cancellable = true)
    private void difficultraids_applyRaidBuffs(int p_213660_1_, boolean p_213660_2_, CallbackInfo callback)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault() && Compat.HUNTER_ILLAGER.isLoaded())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();

            //Main Weapon
            ItemStack weapon = new ItemStack(this.random.nextBoolean() ? Items.BOW : cfg.hunter.getSword());

            if(weapon.getItem().equals(Items.BOW))
            {
                weapon.enchant(Enchantments.POWER_ARROWS, cfg.hunter.bowPowerLevel);

                if(this.random.nextFloat() < cfg.hunter.bowPunchChance)
                    weapon.enchant(Enchantments.PUNCH_ARROWS, cfg.hunter.bowPunchLevel);
            }
            else
            {
                weapon.enchant(Enchantments.SHARPNESS, cfg.hunter.bowPowerLevel);

                if(this.random.nextFloat() < cfg.hunter.swordKnockbackChance)
                    weapon.enchant(Enchantments.KNOCKBACK, cfg.hunter.swordKnockbackLevel);
            }

            //Offhand Boomerang
            ItemStack boomerang = new ItemStack(HunterItems.BOOMERANG.get());

            if(this.random.nextFloat() < cfg.hunter.boomerangLoyaltyChance)
                boomerang.enchant(Enchantments.LOYALTY, 1);

            boomerang.enchant(Enchantments.SHARPNESS, cfg.hunter.boomerangSharpnessLevel);

            //Food
            Item food = this.random.nextFloat() < cfg.hunter.foodGoldenAppleChance ? Items.GOLDEN_APPLE : Items.COOKED_MUTTON;
            this.inventory.addItem(new ItemStack(food, cfg.hunter.foodCount));

            //Final Stuff
            this.setItemInHand(InteractionHand.MAIN_HAND, weapon);
            this.setItemInHand(InteractionHand.OFF_HAND, boomerang);

            this.setDropChance(EquipmentSlot.MAINHAND, cfg.hunter.mainItemDropChance);
            this.setDropChance(EquipmentSlot.OFFHAND, cfg.hunter.boomerangDropChance);

            callback.cancel();
        }
    }
}
