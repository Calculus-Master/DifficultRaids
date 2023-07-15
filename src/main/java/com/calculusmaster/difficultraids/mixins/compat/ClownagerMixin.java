package com.calculusmaster.difficultraids.mixins.compat;

import cn.leolezury.leosillagers.entity.Clownager;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Clownager.class)
public abstract class ClownagerMixin extends AbstractIllager
{
    private final Component ELITE_NAME = Component.translatable("entity.leosillagers.clownager");
    private final ServerBossEvent ELITE_EVENT = new ServerBossEvent(ELITE_NAME, BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);

    private ClownagerMixin(EntityType<? extends AbstractIllager> entitytype, Level world)
    {
        super(entitytype, world);
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();

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
