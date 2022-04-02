package com.calculusmaster.difficultraids.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(Vex.class)
public abstract class VexMixin
{
    @Shadow public abstract void setLimitedLife(int pLimitedLifeTicks);

    @Inject(at = @At("RETURN"), method = "finalizeSpawn")
    private void difficultraids_finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, SpawnGroupData pSpawnData, CompoundTag pDataTag, CallbackInfoReturnable<SpawnGroupData> cir)
    {
        Random random = new Random();

        if(pReason.equals(MobSpawnType.MOB_SUMMONED))
            this.setLimitedLife(20 * (10 + random.nextInt(20)));
    }

    @Inject(at = @At("HEAD"), method = "createAttributes", cancellable = true)
    private static void difficultraids_createVexAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> callbackInfoReturnable)
    {
        AttributeSupplier.Builder builder = Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.ATTACK_DAMAGE, 4.0D);

        callbackInfoReturnable.setReturnValue(builder);
    }
}
