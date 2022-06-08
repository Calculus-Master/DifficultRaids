package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Vindicator.class)
public abstract class VindicatorMixin extends AbstractIllager
{
    //Default Constructor
    protected VindicatorMixin(EntityType<? extends AbstractIllager> p_32105_, Level p_32106_)
    {
        super(p_32105_, p_32106_);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffs(int p_34079_, boolean p_34080_, CallbackInfo callbackInfo)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault())
        {
            List<Item> axePool = switch(raidDifficulty) {
                case DEFAULT -> List.of(Items.GOLDEN_AXE);
                case HERO -> List.of(Items.IRON_AXE);
                case LEGEND -> List.of(Items.IRON_AXE, Items.DIAMOND_AXE);
                case MASTER -> List.of(Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
                case GRANDMASTER -> List.of(Items.NETHERITE_AXE);
            };

            ItemStack axe = new ItemStack(axePool.get(this.random.nextInt(axePool.size())));
            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Sharpness
            int sharpnessChance = switch(raidDifficulty) {
                case DEFAULT -> 0;
                case HERO -> 20;
                case LEGEND -> 40;
                case MASTER -> 60;
                case GRANDMASTER -> 90;
            };

            if(this.random.nextInt(100) < sharpnessChance)
            {
                int sharpnessLevel = switch(raidDifficulty) {
                    case DEFAULT -> 0;
                    case HERO -> this.random.nextInt(1, 3);
                    case LEGEND -> this.random.nextInt(2, 4);
                    case MASTER -> this.random.nextInt(3, 6);
                    case GRANDMASTER -> this.random.nextInt(5, 7);
                };

                enchants.put(Enchantments.SHARPNESS, sharpnessLevel);
            }

            //Fire Aspect
            int fireAspectChance = switch(raidDifficulty) {
                case DEFAULT -> 0;
                case HERO -> 5;
                case LEGEND -> 10;
                case MASTER -> 15;
                case GRANDMASTER -> 50;
            };

            if(this.random.nextInt(100) < fireAspectChance)
            {
                int fireAspectLevel = switch(raidDifficulty) {
                    case DEFAULT -> 0;
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                };

                enchants.put(Enchantments.FIRE_ASPECT, fireAspectLevel);
            }

            if(!axe.is(Items.IRON_AXE)) enchants.put(Enchantments.VANISHING_CURSE, 1);

            EnchantmentHelper.setEnchantments(enchants, axe);
            this.setItemSlot(EquipmentSlot.MAINHAND, axe);

            //Vindicator Swiftness
            int swiftnessChance = switch(raidDifficulty) {
                case DEFAULT -> 0;
                case HERO -> 2;
                case LEGEND -> 5;
                case MASTER -> 7;
                case GRANDMASTER -> 25;
            };

            if(this.random.nextInt(100) < swiftnessChance)
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 20, 3));
        }
    }
}
