package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.renderer.DartIllagerRenderer;
import com.calculusmaster.difficultraids.entity.renderer.WarriorIllagerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DRClientModEvents
{
    @SubscribeEvent
    public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), WarriorIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.DART_ILLAGER.get(), DartIllagerRenderer::new);
    }
}
