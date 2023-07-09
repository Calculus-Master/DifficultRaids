package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.infamous.dungeons_mobs.entities.illagers.RoyalGuardEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(RoyalGuardEntity.class)
public abstract class RoyalGuardMixin extends AbstractIllager
{
    private RoyalGuardMixin(EntityType<? extends AbstractIllager> entitytype, Level world)
    {
        super(entitytype, world);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs", cancellable = true)
    private void difficultraids_applyRaidBuffsDMRoyalGuard(int round, boolean b, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

        if(!raidDifficulty.isDefault())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();
            ItemStack axe = this.getItemInHand(InteractionHand.MAIN_HAND);
            EnchantmentHelper.setEnchantments(new HashMap<>(), axe); //Clear enchants

            axe.enchant(Enchantments.SHARPNESS, cfg.royalguard.axeSharpnessLevel);
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), cfg.royalguard.axeCriticalBurstLevel);
            axe.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.royalguard.axeCriticalStrikeLevel);

            this.setDropChance(EquipmentSlot.MAINHAND, cfg.royalguard.axeDropChance);
            callback.cancel();
        }
    }
}
