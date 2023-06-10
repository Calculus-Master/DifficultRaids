package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.items.GMArmorItem;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DREntityEvents
{
    @SubscribeEvent
    public static void onEntityDamaged(LivingDamageEvent event)
    {
        Random random = new Random();

        //Nuaos Chargewave
        if(event.getEntityLiving() instanceof NuaosEliteEntity nuaos && event.getAmount() > 0)
        {
            nuaos.increaseChargedDamage(event.getAmount());
            nuaos.resetLastDamageTakenTicks();
        }

        //Grandmaster Armor Damage Reduction
        if(event.getEntityLiving() instanceof Player player && event.getAmount() > 0 && event.getSource().getEntity() instanceof Raider)
        {
            float damageReduction = 0.0F;
            for(ItemStack armor : player.getArmorSlots()) if(armor.getItem() instanceof GMArmorItem gmArmor) damageReduction += gmArmor.getRaiderDamageReduction();

            if(damageReduction > 0) event.setAmount(event.getAmount() * (1.0F - damageReduction));
        }

        //Critical Resistance
        if(random.nextFloat() < 0.1F)
        {
            int totalCritResistLevel = 0;
            for(ItemStack stack : event.getEntityLiving().getArmorSlots()) totalCritResistLevel += EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get(), stack);
            totalCritResistLevel = Math.min(totalCritResistLevel, 12);

            if(totalCritResistLevel > 0)
            {
                float reduction = 1 - ((totalCritResistLevel / 12.0F) * (event.getSource().getEntity() instanceof Raider ? 0.8F : 0.7F));

                event.setAmount(event.getAmount() * reduction);
                event.getEntityLiving().playSound(SoundEvents.GLASS_PLACE, 1.5F, 1.5F);
            }
        }

        //Projectile Evasion
        if(event.getSource().isProjectile() && !event.getSource().isBypassArmor())
        {
            int projectileEvasionLevel = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.PROJECTILE_EVASION.get(), event.getEntityLiving().getItemBySlot(EquipmentSlot.FEET));

            if(projectileEvasionLevel > 0)
            {
                float chance = switch(projectileEvasionLevel) {
                    case 1 -> 0.05F;
                    case 2 -> 0.1F;
                    case 3 -> 0.2F;
                    default -> 0.0F;
                };

                if(random.nextFloat() < chance) event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        Random random = new Random();

        //Critical Strike & Burst
        if(event.getSource().getEntity() instanceof LivingEntity living && !living.getMainHandItem().isEmpty())
        {
            int strikeLevel = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), living.getMainHandItem());
            int burstLevel = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_BURST.get(), living.getMainHandItem());

            if(strikeLevel > 0 || burstLevel > 0)
            {
                //Chance
                float minimumChance = 0.035F;
                float chance = 0.05F;
                if(strikeLevel > 0)
                {
                    //I, II | Add 10% per level
                    for(int i = 0; i < 2 && strikeLevel-- > 0; i++) chance += 0.1F;

                    //III+ | Add 12.5% per level
                    while(strikeLevel-- > 0) chance += 0.125F;
                }

                //Damage
                float multiplier = 1.25F;
                if(burstLevel > 0)
                {
                    //I -> III | Add 20% per level
                    for(int i = 0; i < 3 && burstLevel-- > 0; i++) multiplier += 0.2F;

                    //IV -> VII | Add 30% per level | Reduces chance by 1.5% per level
                    for(int i = 0; i < 4 && burstLevel-- > 0; i++)
                    {
                        multiplier += 0.3F;
                        if(chance - 0.015F >= minimumChance) chance -= 0.015F;
                    }

                    //VIII+ | Add 45% per level | Reduces chance by 2.5% per level
                    while(burstLevel-- > 0)
                    {
                        multiplier += 0.45F;
                        if(chance - 0.025F >= minimumChance) chance -= 0.025F;
                    }
                }

                if(random.nextFloat() < chance)
                {
                    event.setAmount(event.getAmount() * multiplier);
                    living.getLevel().playSound(null, living, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 4.25F, 0.65F);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntitySetTarget(LivingSetAttackTargetEvent event)
    {
        LivingEntity target = event.getTarget();
        if(event.getEntityLiving() instanceof Mob mob && target != null && target.hasEffect(MobEffects.INVISIBILITY)) for(ItemStack slot : target.getArmorSlots()) if(EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.INVISIBILITY.get(), slot) > 0) mob.setTarget(null);
    }
}
