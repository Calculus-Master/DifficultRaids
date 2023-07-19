package com.calculusmaster.difficultraids.data.raiderentries;

import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.io.Reader;
import java.util.*;
import java.util.function.Function;

import static com.calculusmaster.difficultraids.data.raiderentries.DifficultyRaiderEntries.CODEC;

public class RaidWaveReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, RaiderEntriesHolder>>
{
    private static final Logger LOGGER = LogUtils.getLogger();

    protected static final String JSON_EXTENSION = ".json";
    protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();

    //Loaded Data
    protected Map<ResourceLocation, RaiderEntriesHolder> data = new HashMap<>();

    private static final String FOLDER_NAME = "raids";
    private static final Function<List<DifficultyRaiderEntries>, RaiderEntriesHolder> MERGER = RaiderEntriesHolder::new;

    public Map<ResourceLocation, RaiderEntriesHolder> getData()
    {
        return this.data;
    }

    // Off-thread processing (can include reading files from hard drive)
    @Override
    protected Map<ResourceLocation, RaiderEntriesHolder> prepare(final ResourceManager resourceManager, final ProfilerFiller profiler)
    {
        LOGGER.info("DifficultRaids: Starting Data Loading for Raid Waves ({}).", FOLDER_NAME);
        final Map<ResourceLocation, RaiderEntriesHolder> map = new HashMap<>();

        Map<ResourceLocation, List<Resource>> resourceStacks = resourceManager.listResourceStacks(FOLDER_NAME, id -> id.getPath().endsWith(JSON_EXTENSION));
        for (var entry : resourceStacks.entrySet())
        {
            List<DifficultyRaiderEntries> raws = new ArrayList<>();
            ResourceLocation fullId = entry.getKey();
            String fullPath = fullId.getPath(); // includes folderName/ and .json
            ResourceLocation id = new ResourceLocation(
                    fullId.getNamespace(),
                    fullPath.substring(FOLDER_NAME.length() + 1, fullPath.length() - JSON_EXTENSION_LENGTH));

            for(Resource resource : entry.getValue())
            {
                try(Reader reader = resource.openAsReader())
                {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    CODEC.parse(JsonOps.INSTANCE, jsonElement)
                            .resultOrPartial(errorMsg -> LOGGER.error("DifficultRaids: Error deserializing json {} in folder {} from pack {}: {}", id, FOLDER_NAME, resource.sourcePackId(), errorMsg))
                            .ifPresent(raws::add);
                }
                catch(Exception e)
                {
                    LOGGER.error(String.format(Locale.ENGLISH, "DifficultRaids: Error reading resource %s in folder %s from pack %s: ", id, FOLDER_NAME, resource.sourcePackId()), e);
                }
            }

            map.put(id, MERGER.apply(raws));
        }

        LOGGER.info("DifficultRaids: Data Loader for {} loaded {} finalized objects", FOLDER_NAME, map.size());
        return Map.copyOf(map);
    }

    // Main-thread processing, runs after prepare concludes
    @Override
    protected void apply(final Map<ResourceLocation, RaiderEntriesHolder> processedData, final ResourceManager resourceManager, final ProfilerFiller profiler)
    {
        // now that we're on the main thread, we can finalize the data
        this.data = processedData;

        RaidEnemyRegistry.compileWaveData(this.data);
    }
}
