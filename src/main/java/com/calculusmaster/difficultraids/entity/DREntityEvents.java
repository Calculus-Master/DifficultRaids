package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.items.GMArmorItem;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
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

        //Critical Strike & Burst
        if(event.getSource().getEntity() instanceof LivingEntity living && !living.getMainHandItem().isEmpty())
        {
            int strikeLevel = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_STRIKE.get(), living.getMainHandItem());
            int burstLevel = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_BURST.get(), living.getMainHandItem());

            if(strikeLevel > 0 || burstLevel > 0)
            {
                float chance = switch(strikeLevel) {
                    case 0 -> 0.05F;
                    case 1 -> 0.15F;
                    case 2 -> 0.35F;
                    default -> 0.0F;
                };

                float multiplier = switch(burstLevel) {
                    case 0 -> 1.25F;
                    case 1 -> 1.5F;
                    case 2 -> 2.0F;
                    case 3 -> 2.5F;
                    default -> 0.0F;
                };

                if(random.nextFloat() < chance)
                {
                    event.setAmount(event.getAmount() * multiplier);
                    living.playSound(SoundEvents.GLASS_BREAK, 3.5F, 0.75F);
                }
            }
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
    public static void onEntitySetTarget(LivingSetAttackTargetEvent event)
    {
        LivingEntity target = event.getTarget();
        if(event.getEntityLiving() instanceof Mob mob && target != null && target.hasEffect(MobEffects.INVISIBILITY)) for(ItemStack slot : target.getArmorSlots()) if(EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.INVISIBILITY.get(), slot) > 0) mob.setTarget(null);
    }
}
