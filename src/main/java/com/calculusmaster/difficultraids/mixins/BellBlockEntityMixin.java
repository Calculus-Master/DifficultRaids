package com.calculusmaster.difficultraids.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BellBlockEntity.class)
public class BellBlockEntityMixin
{
    @Inject(at = @At("HEAD"), method = "isRaiderWithinRange", cancellable = true)
    private static void difficultraids_largerBellSearchRange(BlockPos p_155197_, LivingEntity p_155198_, CallbackInfoReturnable<Boolean> callback)
    {
        callback.setReturnValue(p_155198_.isAlive() && !p_155198_.isRemoved() && p_155197_.closerToCenterThan(p_155198_.position(), 256.0D) && p_155198_.getType().is(EntityTypeTags.RAIDERS));
    }

    @Inject(at = @At("HEAD"), method = "glow", cancellable = true)
    private static void difficultraids_longerGlowEffect(LivingEntity p_58841_, CallbackInfo callback)
    {
        p_58841_.addEffect(new MobEffectInstance(MobEffects.GLOWING, 120));
        callback.cancel();
    }
}