package com.calculusmaster.difficultraids.entity;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.raider.TankIllagerEntity;
import com.calculusmaster.difficultraids.items.GMArmorItem;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DREntityEvents
{
    @SubscribeEvent
    public static void onEntityDamaged(LivingDamageEvent event)
    {
        Random random = new Random();

        Entity source = event.getSource().getEntity();
        LivingEntity target = event.getEntity();

        //Preventing Friendly Fire
        if(!DifficultRaidsConfig.FRIENDLY_FIRE_ARROWS.get())
        {
            boolean friendlyFire = event.getSource().getDirectEntity() instanceof AbstractArrow arrow
                    && (arrow.getOwner() instanceof Raider || source instanceof Raider)
                    && target instanceof Raider;

            if(friendlyFire)
            {
                event.setAmount(0.0F);
                event.setCanceled(true);
            }
        }

        //Nuaos Chargewave
        if(target instanceof NuaosEliteEntity nuaos && event.getAmount() > 0)
        {
            nuaos.increaseChargedDamage(event.getAmount());
            nuaos.resetLastDamageTakenTicks();
        }

        //Grandmaster Armor Damage Reduction
        if(event.getAmount() > 0 && source instanceof Raider)
        {
            float dr = 0.0F;
            for(ItemStack armor : target.getArmorSlots()) if(armor.getItem() instanceof GMArmorItem gmArmor) dr += gmArmor.getRaiderDamageReduction();

            if(dr > 0) event.setAmount(event.getAmount() * (1.0F - dr));
        }

        //Critical Resistance
        if(random.nextFloat() < 0.1F)
        {
            int max = 4 * DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get().getMaxLevel();

            int equipped = 0;
            for(ItemStack stack : target.getArmorSlots()) equipped += stack.getEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_RESISTANCE.get());
            if(equipped > max) equipped = max;

            if(equipped > 0)
            {
                float maxReduction = source instanceof Raider ? 0.8F : 0.7F;
                float reduction = (equipped / (float)max) * maxReduction;

                event.setAmount(event.getAmount() * (1.0F - reduction));
                target.getLevel().playSound(null, target, SoundEvents.GLASS_PLACE, SoundSource.PLAYERS, 2.5F, 0.7F);
            }
        }

        //Projectile Evasion
        if(event.getSource().isProjectile() && !event.getSource().isBypassArmor())
        {
            int projectileEvasionLevel = event.getEntity().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(DifficultRaidsEnchantments.PROJECTILE_EVASION.get());

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
            int strikeLevel = living.getMainHandItem().getEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_STRIKE.get());
            int burstLevel = living.getMainHandItem().getEnchantmentLevel(DifficultRaidsEnchantments.CRITICAL_BURST.get());

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

        //Tank Damage Shielding
        if(event.getEntity() instanceof Raider raider && !raider.getType().equals(DifficultRaidsEntityTypes.TANK_ILLAGER.get()))
        {
            float shieldedPercent = 0.5F;
            if(raider.hasActiveRaid()) shieldedPercent = switch(RaidDifficulty.get(raider.getCurrentRaid().getBadOmenLevel())) {
                case DEFAULT, HERO -> 0.5F;
                case LEGEND -> 0.65F;
                case MASTER -> 0.75F;
                case GRANDMASTER -> 0.9F;
            };

            float damage = event.getAmount() * shieldedPercent;
            List<TankIllagerEntity> nearbyTanks = raider.getLevel()
                    .getNearbyEntities(TankIllagerEntity.class, TargetingConditions.DEFAULT, raider, raider.getBoundingBox().inflate(4.0D))
                    .stream()
                    .filter(tank -> tank.getHealth() > damage)
                    .toList();

            if(!nearbyTanks.isEmpty())
            {
                float splitDamage = damage / nearbyTanks.size();
                for(TankIllagerEntity e : nearbyTanks) e.hurt(event.getSource(), splitDamage);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityChangeTarget(LivingChangeTargetEvent event)
    {
        LivingEntity target = event.getNewTarget();
        if(event.getEntity() instanceof Mob mob && target != null && target.hasEffect(MobEffects.INVISIBILITY)) for(ItemStack slot : target.getArmorSlots()) if(slot.getEnchantmentLevel(DifficultRaidsEnchantments.INVISIBILITY.get()) > 0) mob.setTarget(null);
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event)
    {
        if(event.getEntity() instanceof XydraxEliteEntity) event.setDamageMultiplier(0.0F);
    }
}
