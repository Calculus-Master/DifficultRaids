package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
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
            enchants.put(Enchantments.SHARPNESS, switch(raidDifficulty) {
                case DEFAULT -> 0;
                case HERO -> 2;
                case LEGEND -> 3;
                case MASTER -> 4;
                case GRANDMASTER -> 5;
            });

            //Critical Burst
            if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
            {
                enchants.put(DifficultRaidsEnchantments.CRITICAL_BURST.get(), switch(raidDifficulty) {
                    case LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                    default -> 0;
                });
            }

            //Critical Strike
            enchants.put(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), switch(raidDifficulty) {
                case HERO, LEGEND -> 1;
                case MASTER, GRANDMASTER -> 2;
                default -> 0;
            });

            enchants.put(Enchantments.VANISHING_CURSE, 1);

            EnchantmentHelper.setEnchantments(enchants, axe);
            this.setItemSlot(EquipmentSlot.MAINHAND, axe);
        }
    }
}
