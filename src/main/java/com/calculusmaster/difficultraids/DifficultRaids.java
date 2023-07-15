package com.calculusmaster.difficultraids;

import com.calculusmaster.difficultraids.data.RaidWaveReloadListener;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEffects;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.setup.DifficultRaidsItems;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
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
    public static RaidWaveReloadListener RAID_DATA_LOADER = new RaidWaveReloadListener();

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public DifficultRaids()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        DifficultRaidsItems.register(eventBus);

        DifficultRaidsConfig.register();

        DifficultRaidsEntityTypes.register(eventBus);

        DifficultRaidsEnchantments.register(eventBus);
        DifficultRaidsEffects.register(eventBus);

        // Register the setup method for modloading
        eventBus.addListener(this::setup);
        eventBus.addListener(this::clientSetup);
        eventBus.addListener(this::onLoadComplete);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListener);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

    }

    private void clientSetup(final FMLClientSetupEvent event)
    {

    }

    private void onLoadComplete(final FMLLoadCompleteEvent event)
    {
        DifficultRaidsConfig.initializeConfigs();

        RaidEnemyRegistry.registerRaiders();
        RaidEnemyRegistry.registerWaves();

        DifficultRaidsUtil.registerArmorModifierRaiderLists();
    }

    private void onAddReloadListener(AddReloadListenerEvent event)
    {
        event.addListener(DifficultRaids.RAID_DATA_LOADER);
    }
}