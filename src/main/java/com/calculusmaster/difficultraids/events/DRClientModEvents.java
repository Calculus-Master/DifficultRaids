package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.renderer.*;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
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
        event.registerEntityRenderer(DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), ElectroIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), NecromancerIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get(), ShamanIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.TANK_ILLAGER.get(), TankIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get(), AssassinIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.FROST_ILLAGER.get(), FrostIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.FROST_SNOWBALL.get(), ThrownItemRenderer::new);
    }
}
