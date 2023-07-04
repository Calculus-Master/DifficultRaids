package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.effects.WindCurseEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DifficultRaidsEffects
{
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DifficultRaids.MODID);

    public static final RegistryObject<MobEffect> WIND_CURSE_EFFECT = EFFECTS.register("winds_curse", () -> new WindCurseEffect(MobEffectCategory.HARMFUL, 0x00FFFF));

    public static void register(IEventBus bus)
    {
        EFFECTS.register(bus);
    }
}
