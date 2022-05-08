package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DREntityEvents
{
    @SubscribeEvent
    public static void onEntityDamaged(LivingDamageEvent event)
    {
        if(event.getEntityLiving() instanceof NuaosEliteEntity nuaos && event.getAmount() > 0) nuaos.increaseChargedDamage(event.getAmount());
    }
}
