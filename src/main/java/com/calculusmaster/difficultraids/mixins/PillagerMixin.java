package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
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
import java.util.Map;

@Mixin(Pillager.class)
public abstract class PillagerMixin extends AbstractIllager
{
    //Default Constructor
    protected PillagerMixin(EntityType<? extends Pillager> p_33262_, Level p_33263_)
    {
        super(p_33262_, p_33263_);
    }

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs", cancellable = true)
    public void applyRaidBuffs(int p_37844_, boolean p_37845_, CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

        if(!raidDifficulty.isDefault() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.CROSSBOW))
        {
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);

            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Quick Charge
            int quickChargeChance = switch(raidDifficulty) {
                case HERO -> 20;
                case LEGEND -> 40;
                case MASTER -> 50;
                case GRANDMASTER -> 90;
                default -> 0;
            };

            if(this.random.nextInt(100) < quickChargeChance)
            {
                int quickChargeLevel = switch(raidDifficulty) {
                    case HERO -> this.random.nextInt(1, 3);
                    case LEGEND -> this.random.nextInt(1, 5);
                    case MASTER -> this.random.nextInt(3, 6);
                    case GRANDMASTER -> 5;
                    default -> 0;
                };

                enchants.put(Enchantments.QUICK_CHARGE, quickChargeLevel);
            }

            //Piercing or Multishot (or Nothing)
            int piercingMultishotChance = switch(raidDifficulty) {
                case HERO -> 25;
                case LEGEND -> 30;
                case MASTER -> 40;
                case GRANDMASTER -> 50;
                default -> 0;
            };

            int rand = this.random.nextInt(100);
            if(rand < piercingMultishotChance) enchants.put(Enchantments.PIERCING, this.random.nextInt(1, 4));
            else if(rand < piercingMultishotChance * 2) enchants.put(Enchantments.MULTISHOT, 1);

            EnchantmentHelper.setEnchantments(enchants, crossbow);
            this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);

            callbackInfo.cancel();
        }
    }
}
