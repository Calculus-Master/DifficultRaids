package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.projectile.EvokerFangs;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(EvokerFangs.class)
public abstract class EvokerFangsMixin extends Entity
{
    public EvokerFangsMixin(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Shadow @Nullable public abstract LivingEntity getOwner();

    @Inject(at = @At("HEAD"), method = "dealDamageTo", cancellable = true)
    private void difficultRaids_dealDamageTo(LivingEntity pTarget, CallbackInfo callback)
    {
        LivingEntity owner = this.getOwner();

        boolean canHit = pTarget.isAlive() && !pTarget.isInvulnerable() && pTarget != owner;

        if(canHit && owner instanceof Evoker evoker && evoker.getCurrentRaid() != null)
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.get(evoker.getCurrentRaid().getBadOmenLevel());

            float damage = switch(raidDifficulty) {
                case DEFAULT -> 6.0F;
                case HERO -> 7.0F;
                case LEGEND -> 9.0F;
                case MASTER -> 13.0F;
                case GRANDMASTER -> 18.0F;
            };

            pTarget.hurt(DamageSource.indirectMagic(this, owner), damage);
            callback.cancel();
        }
    }
}
