package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.izofar.takesapillage.entity.Legioner;
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

@Mixin(Legioner.class)
public abstract class LegionerMixin extends AbstractIllager
{
    private LegionerMixin(EntityType<? extends AbstractIllager> entitytype, Level world) {
        super(entitytype, world);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffsITAPLegioner(int round, boolean b, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

        if(!raidDifficulty.isDefault())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();

            ItemStack sword = new ItemStack(cfg.legioner.getSword());

            //Sharpness
            sword.enchant(Enchantments.SHARPNESS, cfg.legioner.swordSharpnessLevel);

            //Fire Aspect
            sword.enchant(Enchantments.FIRE_ASPECT, cfg.legioner.swordFireAspectLevel);

            //Knockback
            sword.enchant(Enchantments.KNOCKBACK, cfg.legioner.swordKnockbackLevel);

            //Critical Strike
            sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), cfg.legioner.swordCriticalStrikeLevel);

            this.setItemSlot(EquipmentSlot.MAINHAND, sword);
            this.setDropChance(EquipmentSlot.MAINHAND, cfg.legioner.swordDropChance);
        }
    }
}
