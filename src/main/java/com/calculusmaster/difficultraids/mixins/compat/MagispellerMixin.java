package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.yellowbrossproductions.illageandspillage.entities.MagispellerEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MagispellerEntity.class)
public abstract class MagispellerMixin extends AbstractIllager
{
    private final Component ELITE_NAME = Component.translatable("entity.illageandspillage.magispeller");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    private MagispellerMixin(EntityType<? extends AbstractIllager> entitytype, Level world)
    {
        super(entitytype, world);
    }

    @Inject(at = @At("TAIL"), method = "customServerAiStep")
    public void difficultraids_tickMagispellerBossBar(CallbackInfo ci)
    {
        this.ELITE_EVENT.setProgress(this.getHealth() / this.getMaxHealth());
    }

    @Override
    public void startSeenByPlayer(ServerPlayer pPlayer)
    {
        super.startSeenByPlayer(pPlayer);
        if(DifficultRaidsConfig.BOSS_BARS.get()) this.ELITE_EVENT.addPlayer(pPlayer);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer pPlayer)
    {
        super.stopSeenByPlayer(pPlayer);
        this.ELITE_EVENT.removePlayer(pPlayer);
    }
}
