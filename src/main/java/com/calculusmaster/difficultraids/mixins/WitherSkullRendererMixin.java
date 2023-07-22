package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.entity.entities.raider.AshenmancerIllagerEntity;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.WitherSkull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WitherSkullRenderer.class)
public class WitherSkullRendererMixin
{
    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/world/entity/projectile/WitherSkull;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void difficultraids_getAshenmancerWitherSkullTextureLocation(WitherSkull pEntity, CallbackInfoReturnable<ResourceLocation> cir)
    {
        if(pEntity.getOwner() instanceof AshenmancerIllagerEntity ashenmancer && !ashenmancer.isTurretActive())
        {
            String filename = "wither_skull_" + ashenmancer.getLastSkullType().toString().toLowerCase() + ".png";

            cir.setReturnValue(new ResourceLocation("difficultraids:textures/entity/ashenmancer/" + filename));
        }
    }
}
