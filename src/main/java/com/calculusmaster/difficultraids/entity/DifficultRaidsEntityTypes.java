package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.WarriorIllagerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DifficultRaidsEntityTypes
{
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, DifficultRaids.MODID);

    public static final RegistryObject<EntityType<WarriorIllagerEntity>> WARRIOR_ILLAGER =
            ENTITY_TYPES.register("warrior_illager",
                    () -> EntityType.Builder.of(WarriorIllagerEntity::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.95F).clientTrackingRange(8)
                            .build(new ResourceLocation(DifficultRaids.MODID, "warrior_illager").toString()));

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
