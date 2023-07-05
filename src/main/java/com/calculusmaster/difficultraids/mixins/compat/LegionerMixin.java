package com.calculusmaster.difficultraids.mixins.compat;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.izofar.takesapillage.entity.Legioner;
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

@Mixin(Legioner.class)
public abstract class LegionerMixin extends AbstractIllager
{
    private LegionerMixin(EntityType<? extends AbstractIllager> entitytype, Level world) {
        super(entitytype, world);
    }

    @Inject(at = @At("HEAD"), method = "applyRaidBuffs")
    private void difficultraids_applyRaidBuffsITAPLegioner(int round, boolean b, CallbackInfo callback)
    {
        if(this.getCurrentRaid() != null)
        {
            RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getCurrentRaid().getBadOmenLevel());

            if(!raidDifficulty.isDefault())
            {
                List<Item> swordPool = switch(raidDifficulty)
                {
                    case DEFAULT, HERO -> List.of(Items.IRON_SWORD);
                    case LEGEND -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD);
                    case MASTER -> List.of(Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
                    case GRANDMASTER -> List.of(Items.NETHERITE_SWORD);
                };

                ItemStack sword = new ItemStack(swordPool.get(this.random.nextInt(swordPool.size())));

                //Sharpness
                sword.enchant(Enchantments.SHARPNESS, switch(raidDifficulty)
                {
                    case DEFAULT -> 0;
                    case HERO, LEGEND -> 1;
                    case MASTER -> 2;
                    case GRANDMASTER -> 3;
                });

                //Fire Aspect
                if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
                {
                    sword.enchant(Enchantments.FIRE_ASPECT, switch(raidDifficulty)
                    {
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                        default -> 0;
                    });
                }

                //Knockback
                if(raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
                {
                    sword.enchant(Enchantments.KNOCKBACK, switch(raidDifficulty)
                    {
                        case LEGEND -> 1;
                        case MASTER -> 2;
                        case GRANDMASTER -> 3;
                        default -> 0;
                    });
                }

                //Critical Strike
                if(raidDifficulty.is(RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER)) sword.enchant(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), raidDifficulty.is(RaidDifficulty.MASTER) ? 1 : 2);

                this.setItemSlot(EquipmentSlot.MAINHAND, sword);
                this.setDropChance(EquipmentSlot.MAINHAND, 0);
            }
        }
    }
}
