package com.calculusmaster.difficultraids.util;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;

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

    //Returns the correct armor piece given a slot and ArmorMaterial. Returns AIR if none are found.
    public static ItemStack getArmorPiece(EquipmentSlot slot, ArmorMaterials material)
    {
        Item item;

        if(material.equals(ArmorMaterials.LEATHER))
        {
            item = switch(slot) {
                case HEAD -> Items.LEATHER_HELMET;
                case CHEST -> Items.LEATHER_CHESTPLATE;
                case LEGS -> Items.LEATHER_LEGGINGS;
                case FEET -> Items.LEATHER_BOOTS;
                default -> null;
            };
        }
        else if(material.equals(ArmorMaterials.CHAIN))
        {
            item = switch(slot) {
                case HEAD -> Items.CHAINMAIL_HELMET;
                case CHEST -> Items.CHAINMAIL_CHESTPLATE;
                case LEGS -> Items.CHAINMAIL_LEGGINGS;
                case FEET -> Items.CHAINMAIL_BOOTS;
                default -> null;
            };
        }
        else if(material.equals(ArmorMaterials.GOLD))
        {
            item = switch(slot) {
                case HEAD -> Items.GOLDEN_HELMET;
                case CHEST -> Items.GOLDEN_CHESTPLATE;
                case LEGS -> Items.GOLDEN_LEGGINGS;
                case FEET -> Items.GOLDEN_BOOTS;
                default -> null;
            };
        }
        else if(material.equals(ArmorMaterials.IRON))
        {
            item = switch(slot) {
                case HEAD -> Items.IRON_HELMET;
                case CHEST -> Items.IRON_CHESTPLATE;
                case LEGS -> Items.IRON_LEGGINGS;
                case FEET -> Items.IRON_BOOTS;
                default -> null;
            };
        }
        else if(material.equals(ArmorMaterials.DIAMOND))
        {
            item = switch(slot) {
                case HEAD -> Items.DIAMOND_HELMET;
                case CHEST -> Items.DIAMOND_CHESTPLATE;
                case LEGS -> Items.DIAMOND_LEGGINGS;
                case FEET -> Items.DIAMOND_BOOTS;
                default -> null;
            };
        }
        else if(material.equals(ArmorMaterials.NETHERITE))
        {
            item = switch(slot) {
                case HEAD -> Items.NETHERITE_HELMET;
                case CHEST -> Items.NETHERITE_CHESTPLATE;
                case LEGS -> Items.NETHERITE_LEGGINGS;
                case FEET -> Items.NETHERITE_BOOTS;
                default -> null;
            };
        }
        else item = Items.AIR;

        return new ItemStack(item);
    }
}
