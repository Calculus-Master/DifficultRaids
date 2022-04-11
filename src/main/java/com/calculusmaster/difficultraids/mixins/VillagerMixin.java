package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager
{
    public VillagerMixin(EntityType<? extends AbstractVillager> p_35267_, Level p_35268_)
    {
        super(p_35267_, p_35268_);
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
