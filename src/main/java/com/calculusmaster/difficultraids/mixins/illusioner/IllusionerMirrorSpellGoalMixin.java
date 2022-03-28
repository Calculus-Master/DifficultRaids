package com.calculusmaster.difficultraids.mixins.illusioner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Illusioner$IllusionerMirrorSpellGoal")
public class IllusionerMirrorSpellGoalMixin
{
    @Inject(at = @At("HEAD"), method = "getCastingTime", cancellable = true)
    private void difficultraids_IllusionerMirrorSpellGoal_getCastingTime(CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        callbackInfoReturnable.setReturnValue(60);
    }

    @Inject(at = @At("HEAD"), method = "getCastingInterval", cancellable = true)
    private void difficultraids_IllusionerBlindnessSpellGoal_getCastingInterval(CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        callbackInfoReturnable.setReturnValue(720);
    }
}
