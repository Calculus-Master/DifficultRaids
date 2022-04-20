package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallestegg.guardvillagers.entities.Guard;

@Mixin(Guard.class)
public abstract class GuardMixin extends PathfinderMob
{
    private GuardMixin(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_)
    {
        super(p_21683_, p_21684_);
    }

    @Inject(at = @At("HEAD"), method = "thunderHit", cancellable = true)
    private void difficultraids_thunderHit(ServerLevel level, LightningBolt lightning, CallbackInfo callback)
    {
        if(lightning.getCustomName() != null && lightning.getCustomName().getString().equals(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG))
        {
            super.thunderHit(level, lightning);
            callback.cancel();
        }
    }
}
