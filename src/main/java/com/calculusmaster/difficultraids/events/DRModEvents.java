package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.WarriorIllagerEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DRModEvents
{
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), WarriorIllagerEntity.createAttributes().build());
    }
}
