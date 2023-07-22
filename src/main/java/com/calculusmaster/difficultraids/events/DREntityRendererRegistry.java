package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.renderer.elite.ModurEliteRenderer;
import com.calculusmaster.difficultraids.entity.renderer.elite.NuaosEliteRenderer;
import com.calculusmaster.difficultraids.entity.renderer.elite.VoldonEliteRenderer;
import com.calculusmaster.difficultraids.entity.renderer.elite.XydraxEliteRenderer;
import com.calculusmaster.difficultraids.entity.renderer.misc.VoldonFamiliarRenderer;
import com.calculusmaster.difficultraids.entity.renderer.raider.*;
import net.minecraft.client.renderer.entity.ShulkerBulletRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DREntityRendererRegistry
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
        event.registerEntityRenderer(DifficultRaidsEntityTypes.ASHENMANCER_ILLAGER.get(), AshenmancerIllagerRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.FROST_SNOWBALL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.SHAMAN_DEBUFF_BULLET.get(), ShulkerBulletRenderer::new);

        event.registerEntityRenderer(DifficultRaidsEntityTypes.NUAOS_ELITE.get(), NuaosEliteRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.XYDRAX_ELITE.get(), XydraxEliteRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.MODUR_ELITE.get(), ModurEliteRenderer::new);
        event.registerEntityRenderer(DifficultRaidsEntityTypes.VOLDON_ELITE.get(), VoldonEliteRenderer::new);

        event.registerEntityRenderer(DifficultRaidsEntityTypes.VOLDON_FAMILIAR.get(), VoldonFamiliarRenderer::new);
    }
}
