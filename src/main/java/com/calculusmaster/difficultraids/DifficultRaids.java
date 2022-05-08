package com.calculusmaster.difficultraids;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.raids.RaidLoot;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.setup.DifficultRaidsStructures;
import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DifficultRaids.MODID)
public class DifficultRaids
{
    public static final String MODID = "difficultraids";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public DifficultRaids()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DifficultRaidsItems.register(eventBus);
        DifficultRaidsStructures.register(eventBus);

        DifficultRaidsConfig.register();

        DifficultRaidsEntityTypes.register(eventBus);

        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::onLoadComplete);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
    }

    private void clientSetup(final FMLClientSetupEvent event)
    {

    }

    private void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        RaidEnemyRegistry.registerRaiders();
        RaidEnemyRegistry.registerWaves();
        RaidEnemyRegistry.registerReinforcements();
        RaidEnemyRegistry.registerElites();

        RaidLoot.register();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents
    {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent)
        {
            // Register a new block here
        }
    }
}
