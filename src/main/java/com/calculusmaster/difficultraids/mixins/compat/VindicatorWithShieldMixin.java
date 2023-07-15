package com.calculusmaster.difficultraids.mixins.compat;

import cn.leolezury.leosillagers.entity.VindicatorWithShield;
import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VindicatorWithShield.class)
public abstract class VindicatorWithShieldMixin extends AbstractIllager
{
    //Default Constructor
    protected VindicatorWithShieldMixin(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffs(int p_34079_, boolean p_34080_, CallbackInfo callbackInfo)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty rd = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !rd.isDefault())
        {
            RaidDifficultyConfig cfg = rd.config();
            ItemStack axe = this.getItemInHand(InteractionHand.MAIN_HAND);

            axe.enchant(Enchantments.SHARPNESS, cfg.vindicatorWithShield.sharpnessLevel);
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), cfg.vindicatorWithShield.criticalBurstLevel);
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.vindicatorWithShield.criticalStrikeLevel);

            this.setItemSlot(EquipmentSlot.MAINHAND, axe);
            this.setDropChance(EquipmentSlot.MAINHAND, cfg.vindicatorWithShield.axeDropChance);
        }
    }
}
