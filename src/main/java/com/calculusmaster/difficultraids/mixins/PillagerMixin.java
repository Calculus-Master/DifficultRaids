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

    @Inject(at = @At("TAIL"), method = "applyRaidBuffs")
    public void applyRaidBuffs(int p_37844_, boolean p_37845_, CallbackInfo callbackInfo)
    {
        boolean inRaid = this.getCurrentRaid() != null;
        RaidDifficulty raidDifficulty = inRaid ? RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel()) : null;

        if(inRaid && !raidDifficulty.isDefault() && this.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.CROSSBOW))
        {
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);

            Map<Enchantment, Integer> enchants = new HashMap<>();

            //Quick Charge
            enchants.put(Enchantments.QUICK_CHARGE, switch(raidDifficulty) {
                case HERO -> 1;
                case LEGEND -> 2;
                case MASTER -> 3;
                case GRANDMASTER -> 5;
                default -> 0;
            });

            //Multishot
            if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
            {
                int chance = switch(raidDifficulty) {
                    case LEGEND -> 33;
                    case MASTER -> 50;
                    case GRANDMASTER -> 90;
                    default -> 0;
                };

                if(this.random.nextInt() < chance) enchants.put(Enchantments.MULTISHOT, 1);
            }

            EnchantmentHelper.setEnchantments(enchants, crossbow);
            this.setItemSlot(EquipmentSlot.MAINHAND, crossbow);
        }
    }
}
