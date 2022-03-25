package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.util.RaidDifficulty;
import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
                List<ArmorMaterials> tiers = List.of(ArmorMaterials.LEATHER, ArmorMaterials.CHAIN, ArmorMaterials.IRON, ArmorMaterials.DIAMOND, ArmorMaterials.NETHERITE);

                int[] subListIndices = switch(raidDifficulty) {
                    case HERO -> new int[]{0, 2};
                    case LEGEND -> new int[]{1, 4};
                    case MASTER -> new int[]{2, 4};
                    case APOCALYPSE -> new int[]{3, 4};
                    default -> new int[]{0, 1};
                };
                subListIndices[1]++;

                int armorChance = switch(raidDifficulty) {
                    case HERO -> 25;
                    case LEGEND -> 45;
                    case MASTER -> 50;
                    case APOCALYPSE -> 80;
                    default -> 0;
                };

                StringJoiner armorLog = new StringJoiner(", ");
                for(EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET))
                {
                    if(random.nextInt(100) < armorChance)
                    {
                        List<ArmorMaterials> pool = tiers.subList(subListIndices[0], subListIndices[1]);
                        ArmorMaterials mat = pool.get(random.nextInt(pool.size()));

                        ItemStack armor = this.getArmorPiece(slot, mat);

                        if(!armor.getItem().equals(Items.AIR))
                        {
                            int protectionChance = switch(raidDifficulty) {
                                case HERO -> 5;
                                case LEGEND -> 15;
                                case MASTER -> 33;
                                case APOCALYPSE -> 50;
                                default -> 0;
                            };

                            if(random.nextInt(100) < protectionChance) armor.enchant(Enchantments.ALL_DAMAGE_PROTECTION,
                                    switch(raidDifficulty) {
                                        case HERO -> random.nextInt(1, 3);
                                        case LEGEND -> random.nextInt(1, 5);
                                        case MASTER -> random.nextInt(3, 6);
                                        case APOCALYPSE -> random.nextInt(4, 6);
                                        default -> 1;
                                    });

                            if(raidDifficulty.equals(RaidDifficulty.LEGEND) && random.nextInt(100) < 15)
                                armor.enchant(Enchantments.THORNS, random.nextInt(1, 4));

                            armorLog.add(armor.getDisplayName() + " [" + armor.getEnchantmentTags().stream().map(Tag::getAsString).collect(Collectors.joining(", ")) + "]");
                            this.setItemSlot(slot, armor);
                        }
                    }

                    LogUtils.getLogger().info("Equipping Armor: Raider {%s}, Armor Pieces {%s}".formatted(this.getDisplayName(), armorLog.toString()));
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
