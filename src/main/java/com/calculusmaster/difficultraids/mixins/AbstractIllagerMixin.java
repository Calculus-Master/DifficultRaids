package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Mixin(AbstractIllager.class)
public abstract class AbstractIllagerMixin extends Raider
{
    //Default Constructor
    protected AbstractIllagerMixin(EntityType<? extends Raider> p_37839_, Level p_37840_)
    {
        super(p_37839_, p_37840_);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
                                        MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();
        Random random = new Random();

        if(!List.of(RaidDifficulty.DEFAULT, RaidDifficulty.DEBUG).contains(raidDifficulty))
        {
            if(this.getCurrentRaid() != null && mobSpawnType.equals(MobSpawnType.EVENT))
            {
                List<ArmorMaterials> tiers = raidDifficulty.armorMaterials;
                int armorChance = raidDifficulty.armorChance;
                int protectionChance = raidDifficulty.protectionChance;

                StringJoiner armorLog = new StringJoiner(", ");
                for(EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET))
                {
                    if(!tiers.isEmpty() && random.nextInt(100) < armorChance)
                    {
                        ArmorMaterials mat = tiers.get(random.nextInt(tiers.size()));

                        ItemStack armor = this.getArmorPiece(slot, mat);

                        if(!armor.getItem().equals(Items.AIR))
                        {
                            if(random.nextInt(100) < protectionChance)
                                armor.enchant(Enchantments.ALL_DAMAGE_PROTECTION,
                                        raidDifficulty.protectionLevelFunction.apply(random));

                            if(raidDifficulty.equals(RaidDifficulty.LEGEND) && random.nextInt(100) < 15)
                                armor.enchant(Enchantments.THORNS, random.nextInt(1, 4));

                            armorLog.add(((TranslatableComponent)(armor.getHoverName())).getKey() + " [" + armor.getEnchantmentTags().stream().map(Tag::getAsString).collect(Collectors.joining(", ")) + "]");
                            this.setItemSlot(slot, armor);
                        }
                    }

                    LogUtils.getLogger().debug("Equipping Armor: Raider {%s}, Armor Pieces {%s}".formatted(((TranslatableComponent)(this.getName())).getKey(), armorLog.toString()));
                }
            }
        }

        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, tag);
    }

    private ItemStack getArmorPiece(EquipmentSlot slot, ArmorMaterials material)
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
