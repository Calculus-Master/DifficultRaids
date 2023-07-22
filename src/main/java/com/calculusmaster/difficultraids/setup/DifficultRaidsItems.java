package com.calculusmaster.difficultraids.setup;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.items.GMArmorItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
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

    //Spawn Eggs
    public static final RegistryObject<Item> SPAWN_EGG_WARRIOR_ILLAGER =
            registerSpawnEgg("warrior_illager", DifficultRaidsEntityTypes.WARRIOR_ILLAGER, 0xA4AEB0);

    public static final RegistryObject<Item> SPAWN_EGG_DART_ILLAGER =
            registerSpawnEgg("dart_illager", DifficultRaidsEntityTypes.DART_ILLAGER, 0xB7B89E);

    public static final RegistryObject<Item> SPAWN_EGG_ELECTRO_ILLAGER =
            registerSpawnEgg("electro_illager", DifficultRaidsEntityTypes.ELECTRO_ILLAGER, 0xA9AD09);

    public static final RegistryObject<Item> SPAWN_EGG_NECROMANCER_ILLAGER =
            registerSpawnEgg("necromancer_illager", DifficultRaidsEntityTypes.NECROMANCER_ILLAGER, 0x3C2D57);

    public static final RegistryObject<Item> SPAWN_EGG_SHAMAN_ILLAGER =
            registerSpawnEgg("shaman_illager", DifficultRaidsEntityTypes.SHAMAN_ILLAGER, 0x3B9438);

    public static final RegistryObject<Item> SPAWN_EGG_TANK_ILLAGER =
            registerSpawnEgg("tank_illager", DifficultRaidsEntityTypes.TANK_ILLAGER, 0x2B2B2B);

    public static final RegistryObject<Item> SPAWN_EGG_ASSASSIN_ILLAGER =
            registerSpawnEgg("assassin_illager", DifficultRaidsEntityTypes.ASSASSIN_ILLAGER, 0xF7F7F0);

    public static final RegistryObject<Item> SPAWN_EGG_FROST_ILLAGER =
            registerSpawnEgg("frost_illager", DifficultRaidsEntityTypes.FROST_ILLAGER, 0x5AB1C4);

    public static final RegistryObject<Item> SPAWN_EGG_ASHENMANCER_ILLAGER =
            registerSpawnEgg("ashenmancer_illager", DifficultRaidsEntityTypes.ASHENMANCER_ILLAGER, 0x5E5746);

    private static <T extends Mob> RegistryObject<Item> registerSpawnEgg(String name, RegistryObject<EntityType<T>> entityType, int highlight)
    {
        return ITEMS.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, 0x565B5C, highlight, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    }


    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
