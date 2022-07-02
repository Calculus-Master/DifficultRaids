package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.commands.AdvanceRaidWaveCommand;
import com.calculusmaster.difficultraids.commands.PrintRaidersCommand;
import com.calculusmaster.difficultraids.commands.SetRaidDifficultyCommand;
import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.entity.entities.elite.ModurEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.raider.*;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tallestegg.guardvillagers.entities.Guard;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID)
public class DRForgeModEvents
{
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event)
    {
        SetRaidDifficultyCommand.register(event.getDispatcher());
        PrintRaidersCommand.register(event.getDispatcher());
        AdvanceRaidWaveCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void addSpawn(EntityJoinWorldEvent event)
    {
        final float defaultMaxDistance = 16.0F;
        final float defaultWalkSpeedModifier = 0.8F;
        final float defaultSprintSpeedModifier = 0.85F;

        //Both Villager and WanderingTrader
        if(event.getEntity() instanceof AbstractVillager villager)
        {
            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, WarriorIllagerEntity.class, defaultMaxDistance, defaultWalkSpeedModifier, defaultSprintSpeedModifier));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, DartIllagerEntity.class, defaultMaxDistance + 2.0F, defaultWalkSpeedModifier, defaultSprintSpeedModifier));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, ElectroIllagerEntity.class, defaultMaxDistance, defaultWalkSpeedModifier - 0.2F, defaultSprintSpeedModifier - 0.1F));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, NecromancerIllagerEntity.class, defaultMaxDistance - 4.0F, defaultWalkSpeedModifier - 0.3F, defaultSprintSpeedModifier - 0.2F));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, ShamanIllagerEntity.class, defaultMaxDistance - 10.0F, defaultWalkSpeedModifier - 0.3F, defaultSprintSpeedModifier - 0.2F));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, TankIllagerEntity.class, defaultMaxDistance, defaultWalkSpeedModifier + 0.1F, defaultSprintSpeedModifier));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, AssassinIllagerEntity.class, 2.0F, defaultWalkSpeedModifier + 0.5F, defaultSprintSpeedModifier + 0.9F));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, FrostIllagerEntity.class, defaultMaxDistance, defaultWalkSpeedModifier - 0.2F, defaultSprintSpeedModifier - 0.1F));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, NuaosEliteEntity.class, defaultMaxDistance, defaultWalkSpeedModifier / 2, defaultSprintSpeedModifier / 2));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, XydraxEliteEntity.class, defaultMaxDistance, defaultWalkSpeedModifier / 2, defaultSprintSpeedModifier / 2));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, ModurEliteEntity.class, defaultMaxDistance, defaultWalkSpeedModifier / 2, defaultSprintSpeedModifier / 2));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, VoldonEliteEntity.class, defaultMaxDistance, defaultWalkSpeedModifier / 2, defaultSprintSpeedModifier / 2));

            villager.goalSelector.addGoal(1,
                    new AvoidEntityGoal<>(villager, VoldonFamiliarEntity.class, defaultMaxDistance / 2, defaultWalkSpeedModifier - 0.2F, defaultSprintSpeedModifier - 0.2F));
        }

        //Compatibility with GuardVillagers - Custom Illagers will also target Guards
        if(DifficultRaidsUtil.isGuardVillagersLoaded() && event.getEntity() instanceof AbstractIllagerVariant illager)
        {
            int priority = 3;

            if(illager instanceof AssassinIllagerEntity || illager instanceof DartIllagerEntity) priority = 2;

            if(illager instanceof AbstractEvokerVariant spellcaster) spellcaster.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(illager, Guard.class, true).setUnseenMemoryTicks(300));
            else illager.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(illager, Guard.class, true));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityHitByLightning(EntityStruckByLightningEvent event)
    {
        LightningBolt lightning = event.getLightning();
        boolean isElectroIllagerBolt = lightning.getCustomName() != null && lightning.getCustomName().getString().equals(DifficultRaidsUtil.ELECTRO_ILLAGER_CUSTOM_BOLT_TAG);

        //Prevent Raiders from taking damage to Electro Illager lightning attacks
        if(isElectroIllagerBolt && event.getEntity() instanceof Raider) event.setCanceled(true);

        //Lightning Resistance Enchantment
        if(event.getEntity() instanceof LivingEntity living)
        {
            ItemStack stack = living.getItemBySlot(EquipmentSlot.HEAD);
            int level = EnchantmentHelper.getItemEnchantmentLevel(DifficultRaidsEnchantments.LIGHTNING_RESISTANCE.get(), stack);

            float damageMultiplier = switch(level) {
                case 1 -> 0.95F;
                case 2 -> 0.8F;
                case 3 -> 0.7F;
                case 4 -> 0.5F;
                case 5 -> 0.25F;
                default -> 0.0F;
            };

            if(isElectroIllagerBolt) damageMultiplier -= 0.05F;

            lightning.setDamage(lightning.getDamage() * damageMultiplier);
        }
    }
}
