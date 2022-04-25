package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.VillageFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DifficultRaidsStructures
{
    public static final DeferredRegister<StructureFeature<?>> STRUCTURE_FEATURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, DifficultRaids.MODID);
    private static final Codec<JigsawConfiguration> LARGER_VILLAGE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(JigsawConfiguration::startPool), Codec.intRange(0, 20).fieldOf("size").forGetter(JigsawConfiguration::maxDepth)).apply(instance, JigsawConfiguration::new));

    public static final RegistryObject<StructureFeature<JigsawConfiguration>> LARGER_VILLAGE = STRUCTURE_FEATURES
            .register("larger_village", () -> new VillageFeature(LARGER_VILLAGE_CODEC) {
                @Override
                public GenerationStep.Decoration step() { return GenerationStep.Decoration.SURFACE_STRUCTURES; }
            });

    public static void register(IEventBus eventBus)
    {
        STRUCTURE_FEATURES.register(eventBus);
    }
}
