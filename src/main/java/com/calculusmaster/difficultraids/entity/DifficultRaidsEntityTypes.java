package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.component.FrostSnowballEntity;
import com.calculusmaster.difficultraids.entity.entities.component.ShamanDebuffBulletEntity;
import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.ModurEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.raider.*;
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
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, DifficultRaids.MODID);

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

    public static final RegistryObject<EntityType<FrostIllagerEntity>> FROST_ILLAGER =
            registerIllager("frost_illager", FrostIllagerEntity::new);

    public static final RegistryObject<EntityType<AshenmancerIllagerEntity>> ASHENMANCER_ILLAGER =
            registerIllager("ashenmancer_illager", AshenmancerIllagerEntity::new);

    //Component Entities
    public static final RegistryObject<EntityType<FrostSnowballEntity>> FROST_SNOWBALL = ENTITY_TYPES.register("frost_snowball",
            () -> EntityType.Builder.of(FrostSnowballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10)
                    .build(new ResourceLocation(DifficultRaids.MODID, "frost_snowball").toString()));

    public static final RegistryObject<EntityType<ShamanDebuffBulletEntity>> SHAMAN_DEBUFF_BULLET = ENTITY_TYPES.register("shaman_debuff_bullet",
            () -> EntityType.Builder.of(ShamanDebuffBulletEntity::new, MobCategory.MISC)
                    .sized(0.3125F, 0.3125F).clientTrackingRange(8)
                    .build(new ResourceLocation(DifficultRaids.MODID, "shaman_debuff_bullet").toString()));

    public static final RegistryObject<EntityType<VoldonFamiliarEntity>> VOLDON_FAMILIAR =
            registerIllager("voldon_familiar", VoldonFamiliarEntity::new);

    //Elites
    public static final RegistryObject<EntityType<NuaosEliteEntity>> NUAOS_ELITE =
            registerIllager("nuaos_elite", NuaosEliteEntity::new);

    public static final RegistryObject<EntityType<XydraxEliteEntity>> XYDRAX_ELITE =
            registerIllager("xydrax_elite", XydraxEliteEntity::new);

    public static final RegistryObject<EntityType<ModurEliteEntity>> MODUR_ELITE =
            registerIllager("modur_elite", ModurEliteEntity::new);

    public static final RegistryObject<EntityType<VoldonEliteEntity>> VOLDON_ELITE =
            registerIllager("voldon_elite", VoldonEliteEntity::new);

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
