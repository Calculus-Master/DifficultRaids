package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.izofar.takesapillage.entity.Skirmisher;
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

@Mixin(Skirmisher.class)
public abstract class SkirmisherMixin extends AbstractIllager
{
    private SkirmisherMixin(EntityType<? extends AbstractIllager> entitytype, Level world) {
        super(entitytype, world);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffsITAPSkirmisher(int round, boolean b, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

        if(!raidDifficulty.isDefault())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();

            ItemStack axe = new ItemStack(cfg.skirmisher.getAxe());

            //Sharpness
            axe.enchant(Enchantments.SHARPNESS, cfg.skirmisher.axeSharpnessLevel);

            //Critical Burst
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), cfg.skirmisher.axeCriticalBurstLevel);

            //Critical Strike
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.skirmisher.axeCriticalStrikeLevel);

            this.setItemSlot(EquipmentSlot.MAINHAND, axe);
            this.setDropChance(EquipmentSlot.MAINHAND, cfg.skirmisher.axeDropChance);
        }
    }
}
