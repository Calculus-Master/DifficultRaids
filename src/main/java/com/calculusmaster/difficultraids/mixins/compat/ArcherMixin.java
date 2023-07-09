package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.izofar.takesapillage.entity.Archer;
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

@Mixin(Archer.class)
public abstract class ArcherMixin extends AbstractIllager
{
    private ArcherMixin(EntityType<? extends AbstractIllager> entitytype, Level world) {
        super(entitytype, world);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffsITAPArcher(int round, boolean b, CallbackInfo callback)
    {
        if(this.getCurrentRaid() != null)
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

            if(!raidDifficulty.isDefault())
            {
                RaidDifficultyConfig cfg = raidDifficulty.config();
                ItemStack bow = this.getItemInHand(InteractionHand.MAIN_HAND);

                bow.enchant(Enchantments.POWER_ARROWS, cfg.archer.bowPowerLevel);
                bow.enchant(Enchantments.PUNCH_ARROWS, cfg.archer.bowPunchLevel);

                this.setDropChance(EquipmentSlot.MAINHAND, cfg.archer.bowDropChance);
            }
        }
    }
}
