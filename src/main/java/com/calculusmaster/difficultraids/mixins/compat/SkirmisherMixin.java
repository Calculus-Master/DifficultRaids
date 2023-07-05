package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.izofar.takesapillage.entity.Skirmisher;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Skirmisher.class)
public abstract class SkirmisherMixin extends AbstractIllager
{
    private SkirmisherMixin(EntityType<? extends AbstractIllager> entitytype, Level world) {
        super(entitytype, world);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffsITAPSkirmisher(int round, boolean b, CallbackInfo callback)
    {
        if(this.getCurrentRaid() != null)
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

            if(!raidDifficulty.isDefault())
            {
                List<Item> axePool = switch(raidDifficulty)
                {
                    case DEFAULT, HERO -> List.of(Items.IRON_AXE);
                    case LEGEND -> List.of(Items.IRON_AXE, Items.DIAMOND_AXE);
                    case MASTER -> List.of(Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
                    case GRANDMASTER -> List.of(Items.NETHERITE_AXE);
                };

                ItemStack axe = new ItemStack(axePool.get(this.random.nextInt(axePool.size())));

                //Sharpness
                axe.enchant(Enchantments.SHARPNESS, switch(raidDifficulty) {
                    case DEFAULT -> 0;
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case GRANDMASTER -> 5;
                });

                //Critical Burst
                if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
                {
                    axe.enchant(DifficultRaidsEnchantments.CRITICAL_BURST.get(), switch(raidDifficulty)
                    {
                        case LEGEND -> 2;
                        case MASTER -> 3;
                        case GRANDMASTER -> 6;
                        default -> 0;
                    });
                }

                //Critical Strike
                axe.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), switch(raidDifficulty)
                {
                    case HERO, LEGEND -> 1;
                    case MASTER, GRANDMASTER -> 2;
                    default -> 0;
                });

                this.setItemSlot(EquipmentSlot.MAINHAND, axe);
                this.setDropChance(EquipmentSlot.MAINHAND, 0);
            }
        }
    }
}
