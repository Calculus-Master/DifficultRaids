package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin extends Entity
{
    private LightningBoltMixin(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Inject(at = @At("HEAD"), method = "spawnFire", cancellable = true)
    private void difficultraids_spawnFire(int extraIgnitions, CallbackInfo callback)
    {
        if(this.getCustomName() != null && this.getCustomName().getString().equals(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG))
            callback.cancel();
    }
}
