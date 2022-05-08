package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.ModurEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.raider.*;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DREntityAttributeRegistry
{
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), WarriorIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.DART_ILLAGER.get(), DartIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), ElectroIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), NecromancerIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get(), ShamanIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.TANK_ILLAGER.get(), TankIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get(), AssassinIllagerEntity.createAttributes().build());
        event.put(DifficultRaidsEntityTypes.FROST_ILLAGER.get(), FrostIllagerEntity.createAttributes().build());

        event.put(DifficultRaidsEntityTypes.NUAOS_ELITE.get(), NuaosEliteEntity.createEliteAttributes().build());
        event.put(DifficultRaidsEntityTypes.XYDRAX_ELITE.get(), XydraxEliteEntity.createEliteAttributes().build());
        event.put(DifficultRaidsEntityTypes.MODUR_ELITE.get(), ModurEliteEntity.createEliteAttributes().build());
        event.put(DifficultRaidsEntityTypes.VOLDON_ELITE.get(), VoldonEliteEntity.createEliteAttributes().build());

        event.put(DifficultRaidsEntityTypes.VOLDON_FAMILIAR.get(), VoldonFamiliarEntity.createAttributes().build());
    }
}
