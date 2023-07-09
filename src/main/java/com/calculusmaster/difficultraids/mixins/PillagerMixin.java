package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllager
{
    //Default Constructor
    protected PillagerMixin(EntityType<? extends Pillager> p_33262_, Level p_33263_)
    {
        super(p_33262_, p_33263_);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs")
    public void applyRaidBuffs(int p_37844_, boolean p_37845_, CallbackInfo callbackInfo)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.CROSSBOW))
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);

            crossbow.enchant(Enchantments.POWER_ARROWS, cfg.pillager.powerLevel);
            crossbow.enchant(Enchantments.QUICK_CHARGE, cfg.pillager.quickChargeLevel);

            if(this.random.nextFloat() < cfg.pillager.multishotChance)
                crossbow.enchant(Enchantments.MULTISHOT, 1);

            this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
            this.setDropChance(EquipmentSlot.MAINHAND, cfg.pillager.crossbowDropChance);
        }
    }
}
