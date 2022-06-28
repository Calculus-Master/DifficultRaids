package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.items.GMArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
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

    //Totems

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

    public static final RegistryObject<Item> TOTEM_OF_FREEZING =
            ITEMS.register("freezing_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_PERSISTENCE =
            ITEMS.register("persistence_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> TOTEM_OF_TELEPORTATION =
            ITEMS.register("teleportation_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    public static final RegistryObject<Item> TOTEM_OF_FIREBALLS =
            ITEMS.register("fireball_totem", () -> new Item(DEFAULT_TOTEM_PROPERTIES.get()));

    //Armor

    public static final RegistryObject<Item> GRANDMASTER_HELMET =
            ITEMS.register("grandmaster_helmet", () -> new GMArmorItem(EquipmentSlot.HEAD));

    public static final RegistryObject<Item> GRANDMASTER_CHESTPLATE =
            ITEMS.register("grandmaster_chestplate", () -> new GMArmorItem(EquipmentSlot.CHEST));

    public static final RegistryObject<Item> GRANDMASTER_LEGGINGS =
            ITEMS.register("grandmaster_leggings", () -> new GMArmorItem(EquipmentSlot.LEGS));

    public static final RegistryObject<Item> GRANDMASTER_BOOTS =
            ITEMS.register("grandmaster_boots", () -> new GMArmorItem(EquipmentSlot.FEET));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
