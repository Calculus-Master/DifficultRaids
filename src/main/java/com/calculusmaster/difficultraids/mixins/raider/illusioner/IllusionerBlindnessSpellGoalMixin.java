package com.calculusmaster.difficultraids.mixins.raider.illusioner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.monster.Illusioner$IllusionerBlindnessSpellGoal")
public class IllusionerBlindnessSpellGoalMixin
{
    @Inject(at = @At("HEAD"), method = "getCastingTime", cancellable = true)
    private void difficultraids_IllusionerBlindnessSpellGoal_getCastingTime(CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        callbackInfoReturnable.setReturnValue(60);
    }

    @Inject(at = @At("HEAD"), method = "getCastingInterval", cancellable = true)
    private void difficultraids_IllusionerBlindnessSpellGoal_getCastingInterval(CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        callbackInfoReturnable.setReturnValue(360);
    }
}
