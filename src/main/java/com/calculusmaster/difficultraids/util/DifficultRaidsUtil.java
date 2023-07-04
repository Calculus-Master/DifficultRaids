package com.calculusmaster.difficultraids.util;

import baguchan.enchantwithmob.registry.ModEntities;
import baguchan.hunterillager.init.HunterEntityRegistry;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public class DifficultRaidsUtil
{
    //Mod Checks
    public static boolean isGuardVillagersLoaded()
    {
        return ModList.get().isLoaded("guardvillagers");
    }
    public static boolean isHunterIllagerLoaded()
    {
        return ModList.get().isLoaded("hunterillager");
    }
    public static boolean isEnchantWithMobLoaded()
    {
        return ModList.get().isLoaded("enchantwithmob");
    }

    public static final String ELECTRO_ILLAGER_CUSTOM_BOLT_TAG = "DifficultRaids_Electro_Bolt";

    //For Armor Modifiers
    public static final List<EntityType<? extends Raider>> STANDARD_RAIDERS = new ArrayList<>();
    public static final List<EntityType<? extends Raider>> BASIC_MAGIC_RAIDERS = new ArrayList<>();
    public static final List<EntityType<? extends Raider>> ADVANCED_MAGIC_RAIDERS = new ArrayList<>();

    public static void registerArmorModifierRaiderLists()
    {
        //Default
        STANDARD_RAIDERS.addAll(List.of(EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.WITCH, DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get()));
        BASIC_MAGIC_RAIDERS.addAll(List.of(EntityType.WITCH, EntityType.EVOKER, DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get()));
        ADVANCED_MAGIC_RAIDERS.addAll(List.of(DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), DifficultRaidsEntityTypes.FROST_ILLAGER.get()));

        //Unaffected: Illusioner, Assassin, Dart, Elites

        //Mod Compat
        if(DifficultRaidsUtil.isHunterIllagerLoaded()) STANDARD_RAIDERS.add(HunterEntityRegistry.HUNTERILLAGER.get());
        if(DifficultRaidsUtil.isEnchantWithMobLoaded()) ADVANCED_MAGIC_RAIDERS.add(ModEntities.ENCHANTER.get());
    }
}
