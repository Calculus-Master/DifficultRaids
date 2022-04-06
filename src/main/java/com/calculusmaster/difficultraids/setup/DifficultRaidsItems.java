package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DifficultRaidsItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DifficultRaids.MODID);

    private static final Supplier<Item.Properties> DEFAULT_TOTEM_PROPERTIES = () -> new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_COMBAT).rarity(Rarity.UNCOMMON).fireResistant();

    public static final RegistryObject<Item> TOTEM_OF_INVISIBILITY =
            ITEMS.register("invisibility_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_LIGHTNING =
            ITEMS.register("lightning_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_POISON =
            ITEMS.register("poison_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_SPEED =
            ITEMS.register("speed_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_VENGEANCE =
            ITEMS.register("vengeance_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_DESTINY =
            ITEMS.register("destiny_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> TOTEM_OF_LEVITATION =
            ITEMS.register("levitation_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_PROTECTION =
            ITEMS.register("protection_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get().rarity(Rarity.RARE)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
