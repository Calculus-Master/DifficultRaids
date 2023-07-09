package com.calculusmaster.difficultraids.mixins.raider;

import com.calculusmaster.difficultraids.config.RaidDifficultyConfig;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Ravager.class)
public abstract class RavagerMixin extends Raider
{
    protected RavagerMixin(EntityType<? extends Ravager> p_33262_, Level p_33263_)
    {
        super(p_33262_, p_33263_);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs")
    public void applyRaidBuffs(int p_37844_, boolean p_37845_, CallbackInfo callbackInfo)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault())
        {
            RaidDifficultyConfig cfg = raidDifficulty.config();

            AttributeInstance damageInstance = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if(damageInstance != null) damageInstance.addPermanentModifier(new AttributeModifier(
                    "difficultraids_RavagerDamageBoost",
                    cfg.ravager.damageMultiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));

            AttributeInstance speedInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if(speedInstance != null) damageInstance.addPermanentModifier(new AttributeModifier(
                    "difficultraids_RavagerSpeedBoost",
                    cfg.ravager.speedMultiplier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            ));
        }
    }
}
