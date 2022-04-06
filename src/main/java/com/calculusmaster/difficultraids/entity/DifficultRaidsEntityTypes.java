package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DifficultRaidsEntityTypes
{
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, DifficultRaids.MODID);

    public static final RegistryObject<EntityType<WarriorIllagerEntity>> WARRIOR_ILLAGER =
            registerIllager("warrior_illager", WarriorIllagerEntity::new);

    public static final RegistryObject<EntityType<DartIllagerEntity>> DART_ILLAGER =
            registerIllager("dart_illager", DartIllagerEntity::new);

    public static final RegistryObject<EntityType<ElectroIllagerEntity>> ELECTRO_ILLAGER =
            registerIllager("electro_illager", ElectroIllagerEntity::new);

    public static final RegistryObject<EntityType<NecromancerIllagerEntity>> NECROMANCER_ILLAGER =
            registerIllager("necromancer_illager", NecromancerIllagerEntity::new);

    public static final RegistryObject<EntityType<ShamanIllagerEntity>> SHAMAN_ILLAGER =
            registerIllager("shaman_illager", ShamanIllagerEntity::new);

    public static final RegistryObject<EntityType<TankIllagerEntity>> TANK_ILLAGER =
            registerIllager("tank_illager", TankIllagerEntity::new);

    public static final RegistryObject<EntityType<AssassinIllagerEntity>> ASSASSIN_ILLAGER =
            registerIllager("assassin_illager", AssassinIllagerEntity::new);

    private static <T extends AbstractIllager> RegistryObject<EntityType<T>> registerIllager(String registryName, EntityType.EntityFactory<T> entityFactory)
    {
        return ENTITY_TYPES.register(registryName,
                () -> EntityType.Builder.of(entityFactory, MobCategory.MONSTER)
                        .sized(0.6F, 1.95F).clientTrackingRange(8).fireImmune()
                        .build(new ResourceLocation(DifficultRaids.MODID, registryName).toString())
        );
    }

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
