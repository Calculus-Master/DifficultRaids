package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.infamous.dungeons_mobs.entities.illagers.MountaineerEntity;
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

@Mixin(MountaineerEntity.class)
public abstract class MountaineerMixin extends AbstractIllager
{
    private MountaineerMixin(EntityType<? extends AbstractIllager> entitytype, Level world)
    {
        super(entitytype, world);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs", cancellable = true)
    private void difficultraids_applyRaidBuffsDMMountaineer(int round, boolean b, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

        if(!raidDifficulty.isDefault())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();
            ItemStack pick = this.getItemInHand(InteractionHand.MAIN_HAND);
            EnchantmentHelper.setEnchantments(new HashMap<>(), pick); //Clear enchants

            pick.enchant(Enchantments.SHARPNESS, cfg.mountaineer.pickSharpnessLevel);
            pick.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), cfg.mountaineer.pickCriticalBurstLevel);

            this.setDropChance(EquipmentSlot.MAINHAND, cfg.mountaineer.pickDropChance);
            callback.cancel();
        }
    }
}
