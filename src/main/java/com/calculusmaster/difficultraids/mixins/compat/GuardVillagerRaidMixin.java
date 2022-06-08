package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tallestegg.guardvillagers.GuardEntityType;
import tallestegg.guardvillagers.entities.Guard;

@Pseudo
@Mixin(Raid.class)
// TODO: FIXME: 4/12/22
public abstract class GuardVillagerRaidMixin
{
    @Shadow private BlockPos center;
    @Shadow @Final private ServerLevel level;

    @Shadow public abstract int getBadOmenLevel();

    @Inject(at = @At("HEAD"), method = "spawnGroup")
    private void difficultraids_spawnGroupGuardReinforcements(BlockPos pos, CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        //Spawn Extra Guards
        if(DifficultRaidsUtil.isGuardVillagersLoaded() && !raidDifficulty.isDefault())
        {
            int amount = switch(raidDifficulty) {
                case HERO -> 2;
                case LEGEND -> 3;
                case MASTER -> 4;
                case GRANDMASTER -> 7;
                default -> 0;
            };

            BlockPos adjustedSpawnPos = this.center.above(2);
            for(int i = 0; i < amount; i++)
            {
                Guard guard = GuardEntityType.GUARD.get().create(this.level);
                guard.moveTo(adjustedSpawnPos, 1.0F, 1.0F);
                guard.setGuardVariant(Guard.getRandomTypeForBiome(this.level, adjustedSpawnPos));
                guard.setPersistenceRequired();

                guard.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 1));
                this.level.addFreshEntity(guard);
            }
        }
    }
}
