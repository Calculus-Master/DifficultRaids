package com.calculusmaster.difficultraids.util;

import baguchan.enchantwithmob.registry.ModEntities;
import baguchan.hunterillager.init.HunterEntityRegistry;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.izofar.takesapillage.init.ModEntityTypes;
import com.teamabnormals.savage_and_ravage.core.registry.SREntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raider;

import java.util.ArrayList;
import java.util.List;

public class DifficultRaidsUtil
{
    public static final String ELECTRO_ILLAGER_CUSTOM_BOLT_TAG = "DifficultRaids_Electro_Bolt";

    public enum OverflowHandlingMode { ZERO, REPEAT }

    //For Armor Modifiers
    public static final List<EntityType<? extends Raider>> STANDARD_RAIDERS = new ArrayList<>();
    public static final List<EntityType<? extends Raider>> ADVANCED_RAIDERS = new ArrayList<>();
    public static final List<EntityType<? extends Raider>> BASIC_MAGIC_RAIDERS = new ArrayList<>();
    public static final List<EntityType<? extends Raider>> ADVANCED_MAGIC_RAIDERS = new ArrayList<>();

    public static void registerArmorModifierRaiderLists()
    {
        //Default – Skipping: Assassin, Dart, Elites, Tank
        STANDARD_RAIDERS.addAll(List.of(EntityType.PILLAGER, EntityType.VINDICATOR, EntityType.WITCH, DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get()));
        ADVANCED_RAIDERS.add(EntityType.ILLUSIONER);
        BASIC_MAGIC_RAIDERS.addAll(List.of(EntityType.WITCH, EntityType.EVOKER, DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get()));
        ADVANCED_MAGIC_RAIDERS.addAll(List.of(DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), DifficultRaidsEntityTypes.FROST_ILLAGER.get()));

        //Mod Compat
        if(Compat.HUNTER_ILLAGER.isLoaded()) STANDARD_RAIDERS.add(HunterEntityRegistry.HUNTERILLAGER.get());

        if(Compat.ENCHANT_WITH_MOB.isLoaded()) ADVANCED_MAGIC_RAIDERS.add(ModEntities.ENCHANTER.get());

        if(Compat.IT_TAKES_A_PILLAGE.isLoaded())
        {
            STANDARD_RAIDERS.addAll(List.of(ModEntityTypes.ARCHER.get(), ModEntityTypes.SKIRMISHER.get()));
            ADVANCED_RAIDERS.add(ModEntityTypes.LEGIONER.get());
        }

        if(Compat.ILLAGE_AND_SPILLAGE.isLoaded()) //Skipping: Absorber, Magispeller/Freakager/Spiritcaller/Boss Randomizer
        {
            STANDARD_RAIDERS.addAll(List.of(
                    com.yellowbrossproductions.illageandspillage.init.ModEntityTypes.Preserver.get(),
                    com.yellowbrossproductions.illageandspillage.init.ModEntityTypes.Igniter.get()
            ));
            ADVANCED_RAIDERS.addAll(List.of(
                    com.yellowbrossproductions.illageandspillage.init.ModEntityTypes.Twittollager.get(),
                    com.yellowbrossproductions.illageandspillage.init.ModEntityTypes.Crocofang.get()
            ));
        }

        if(Compat.SAVAGE_AND_RAVAGE.isLoaded())
        {
            ADVANCED_RAIDERS.add(SREntityTypes.EXECUTIONER.get());
            BASIC_MAGIC_RAIDERS.addAll(List.of(SREntityTypes.GRIEFER.get(), SREntityTypes.TRICKSTER.get()));
            ADVANCED_MAGIC_RAIDERS.add(SREntityTypes.ICEOLOGER.get());
        }

        if(Compat.DUNGEONS_MOBS.isLoaded()) //Skipping: Squall Golem, Redstone Golem
        {
            STANDARD_RAIDERS.add(com.infamous.dungeons_mobs.mod.ModEntityTypes.MOUNTAINEER.get());
            ADVANCED_RAIDERS.addAll(List.of(
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.ROYAL_GUARD.get(),
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.ILLUSIONER.get()
            ));
            BASIC_MAGIC_RAIDERS.addAll(List.of(
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.MAGE.get(),
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.ICEOLOGER.get()
            ));
            ADVANCED_MAGIC_RAIDERS.addAll(List.of(
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.GEOMANCER.get(),
                    com.infamous.dungeons_mobs.mod.ModEntityTypes.WINDCALLER.get()
            ));
        }
    }
}
