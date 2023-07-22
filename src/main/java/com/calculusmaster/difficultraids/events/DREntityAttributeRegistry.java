package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DREntityAttributeRegistry
{
    private static final Supplier<AttributeSupplier.Builder> DEFAULT = () -> Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED)
            .add(Attributes.FOLLOW_RANGE)
            .add(Attributes.MAX_HEALTH)
            .add(Attributes.ATTACK_DAMAGE)
            .add(Attributes.ATTACK_SPEED)
            .add(Attributes.ARMOR)
            .add(Attributes.ARMOR_TOUGHNESS);

    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(DifficultRaidsEntityTypes.WARRIOR_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.TANK_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.25F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 36.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 0.5)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.ASSASSIN_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.40F)
                .add(Attributes.FOLLOW_RANGE, 30.0D)
                .add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.ELECTRO_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 12.0D)
                .add(Attributes.MAX_HEALTH, 25.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.NECROMANCER_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.SHAMAN_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.FROST_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 30.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.DART_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.42F)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.MAX_HEALTH, 16.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.ASHENMANCER_ILLAGER.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.35F)
                .add(Attributes.FOLLOW_RANGE, 18.0D)
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .build()
        );

        //Elites

        event.put(DifficultRaidsEntityTypes.NUAOS_ELITE.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.30F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.XYDRAX_ELITE.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.39F)
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MAX_HEALTH, 85.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.ARMOR, 8.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.MODUR_ELITE.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.45F)
                .add(Attributes.FOLLOW_RANGE, 14.0D)
                .add(Attributes.MAX_HEALTH, 60.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 7.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.VOLDON_ELITE.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.42F)
                .add(Attributes.FOLLOW_RANGE, 15.0D)
                .add(Attributes.MAX_HEALTH, 90.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3D)
                .build()
        );

        event.put(DifficultRaidsEntityTypes.VOLDON_FAMILIAR.get(), DEFAULT.get()
                .add(Attributes.MOVEMENT_SPEED, 0.33F)
                .add(Attributes.FOLLOW_RANGE, 7.0D)
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.ARMOR, 20.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .build()
        );
    }

    // Vanilla Raiders
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event)
    {
        List.of(EntityType.VINDICATOR, EntityType.PILLAGER, EntityType.WITCH, EntityType.EVOKER, EntityType.ILLUSIONER, EntityType.RAVAGER).forEach(type -> {
            event.add(type, Attributes.ARMOR);
            event.add(type, Attributes.ARMOR_TOUGHNESS);
        });
    }
}
