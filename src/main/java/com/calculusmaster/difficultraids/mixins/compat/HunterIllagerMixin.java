package com.calculusmaster.difficultraids.mixins.compat;

import baguchan.hunterillager.entity.HunterIllagerEntity;
import baguchan.hunterillager.init.HunterItems;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(HunterIllagerEntity.class)
public abstract class HunterIllagerMixin extends AbstractIllager
{
    @Shadow @Final private SimpleContainer inventory;

    private HunterIllagerMixin(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs", cancellable = true)
    private void difficultraids_applyRaidBuffs(int p_213660_1_, boolean p_213660_2_, CallbackInfo callback)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault() && DifficultRaidsUtil.isHunterIllagerLoaded())
        {
            //TODO: Add Config for this stuff
            //Main Weapon
            Item sword = switch(raidDifficulty) {
                case HERO -> Items.STONE_SWORD;
                case LEGEND -> Items.IRON_SWORD;
                case MASTER -> Items.DIAMOND_SWORD;
                case GRANDMASTER -> Items.NETHERITE_SWORD;
                default -> Items.GOLDEN_SWORD;
            };

            ItemStack weapon = new ItemStack(this.random.nextBoolean() ? Items.BOW : sword);

            Map<Enchantment, Integer> weaponEnchants = new HashMap<>();
            if(weapon.getItem().equals(Items.BOW))
            {
                //Power
                int powerChance = switch(raidDifficulty) {
                    case HERO -> 25;
                    case LEGEND -> 35;
                    case MASTER -> 45;
                    case GRANDMASTER -> 90;
                    default -> 0;
                };

                if(this.random.nextInt(100) < powerChance)
                {
                    int powerLevel = switch(raidDifficulty) {
                        case HERO -> this.random.nextInt(1, 3);
                        case LEGEND -> this.random.nextInt(1, 5);
                        case MASTER -> this.random.nextInt(2, 5);
                        case GRANDMASTER -> this.random.nextInt(5, 7);
                        default -> 0;
                    };

                    weaponEnchants.put(Enchantments.POWER_ARROWS, powerLevel);
                }

                //Punch
                int punchChance = switch(raidDifficulty) {
                    case HERO -> 15;
                    case LEGEND -> 20;
                    case MASTER -> 25;
                    case GRANDMASTER -> 50;
                    default -> 0;
                };

                if(this.random.nextInt(100) < punchChance)
                {
                    int punchLevel = switch(raidDifficulty) {
                        case HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                        default -> 0;
                    };

                    weaponEnchants.put(Enchantments.PUNCH_ARROWS, punchLevel);
                }

                //Flame
                int fireChance = switch(raidDifficulty) {
                    case HERO -> 5;
                    case LEGEND -> 10;
                    case MASTER -> 15;
                    case GRANDMASTER -> 50;
                    default -> 0;
                };

                if(this.random.nextInt(100) < fireChance)
                {
                    int fireLevel = switch(raidDifficulty) {
                        case HERO, LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                        default -> 0;
                    };

                    weaponEnchants.put(Enchantments.FLAMING_ARROWS, fireLevel);
                }
            }
            else
            {
                int sharpnessChance = switch(raidDifficulty) {
                    case HERO -> 60;
                    case LEGEND -> 70;
                    case MASTER -> 80;
                    case GRANDMASTER -> 90;
                    default -> 0;
                };

                if(this.random.nextInt(100) < sharpnessChance)
                {
                    int sharpnessLevel = switch(raidDifficulty) {
                        case HERO -> 2;
                        case LEGEND -> 3;
                        case MASTER -> 4;
                        case GRANDMASTER -> 6;
                        default -> 0;
                    };

                    weaponEnchants.put(Enchantments.SHARPNESS, sharpnessLevel);
                }
            }

            EnchantmentHelper.setEnchantments(weaponEnchants, weapon);

            //Offhand Boomerang
            ItemStack boomerang = new ItemStack(HunterItems.BOOMERANG.get());

            Map<Enchantment, Integer> boomerangEnchants = new HashMap<>();

            int loyaltyChance = switch(raidDifficulty) {
                case HERO -> 33;
                case LEGEND -> 45;
                case MASTER -> 55;
                case GRANDMASTER -> 90;
                default -> 0;
            };

            if(this.random.nextInt(100) < loyaltyChance) boomerangEnchants.put(Enchantments.LOYALTY, 1);

            int sharpnessChance = switch(raidDifficulty) {
                case HERO -> 20;
                case LEGEND -> 30;
                case MASTER -> 45;
                case GRANDMASTER -> 90;
                default -> 0;
            };

            if(this.random.nextInt(100) < sharpnessChance)
            {
                int sharpnessLevel = switch(raidDifficulty) {
                    case HERO -> 1;
                    case LEGEND -> 2;
                    case MASTER -> 3;
                    case GRANDMASTER -> 7;
                    default -> 0;
                };

                boomerangEnchants.put(Enchantments.SHARPNESS, sharpnessLevel);
            }

            EnchantmentHelper.setEnchantments(boomerangEnchants, boomerang);

            //Food
            int foodCount = switch(raidDifficulty) {
                case HERO -> 6;
                case LEGEND -> 8;
                case MASTER -> 10;
                case GRANDMASTER -> 20;
                default -> 0;
            };

            this.inventory.addItem(new ItemStack(Items.COOKED_MUTTON, foodCount));

            //Final Stuff
            this.setItemInHand(InteractionHand.MAIN_HAND, weapon);
            this.setItemInHand(InteractionHand.OFF_HAND, boomerang);

            callback.cancel();
        }
    }
}
