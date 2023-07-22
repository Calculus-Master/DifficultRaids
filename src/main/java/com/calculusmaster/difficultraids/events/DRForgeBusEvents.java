package com.calculusmaster.difficultraids.events;

import com.calculusmaster.difficultraids.DifficultRaids;
import com.calculusmaster.difficultraids.commands.*;
import com.calculusmaster.difficultraids.entity.entities.component.VoldonFamiliarEntity;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractEvokerVariant;
import com.calculusmaster.difficultraids.entity.entities.core.AbstractIllagerVariant;
import com.calculusmaster.difficultraids.entity.entities.elite.ModurEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.NuaosEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.VoldonEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.elite.XydraxEliteEntity;
import com.calculusmaster.difficultraids.entity.entities.raider.*;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEffects;
import com.calculusmaster.difficultraids.setup.DifficultRaidsEnchantments;
import com.calculusmaster.difficultraids.util.Compat;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.PlayLevelSoundEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import tallestegg.guardvillagers.entities.Guard;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = DifficultRaids.MODID)
public class DRForgeBusEvents
{
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event)
    {
        SetRaidDifficultyCommand.register(event.getDispatcher());
        PrintRaidersCommand.register(event.getDispatcher());
        AdvanceRaidWaveCommand.register(event.getDispatcher());
        ToggleInsanityModeCommand.register(event.getDispatcher());
        DumpRaidWavesCommand.register(event.getDispatcher());
        FreezeRaidersCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onSoundPlayedAtPosition(PlayLevelSoundEvent.AtPosition event)
    {
        BlockPos pos = new BlockPos(event.getPosition().x(), event.getPosition().y(), event.getPosition().z());
        if(event.getSource().equals(SoundSource.WEATHER) && (event.getLevel() instanceof ServerLevel sl && sl.getRaidAt(pos) != null))
        {
            if(event.getSound().equals(SoundEvents.LIGHTNING_BOLT_THUNDER)) event.setNewVolume(event.getOriginalVolume() / 100);
            else if(event.getSound().equals(SoundEvents.LIGHTNING_BOLT_IMPACT)) event.setNewVolume(event.getOriginalVolume() / 2);
        }
    }

    @SubscribeEvent
    public static void addSpawn(EntityJoinLevelEvent event)
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
                    new AvoidEntityGoal<>(villager, AssassinIllagerEntity.class, 2.5F, defaultWalkSpeedModifier + 0.1F, defaultSprintSpeedModifier + 0.1F));

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
        if(Compat.GUARD_VILLAGERS.isLoaded() && event.getEntity() instanceof AbstractIllagerVariant illager)
        {
            int priority = 3;

            if(illager instanceof AssassinIllagerEntity || illager instanceof DartIllagerEntity) priority = 2;

            if(illager instanceof AbstractEvokerVariant spellcaster) spellcaster.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(illager, Guard.class, true).setUnseenMemoryTicks(300));
            else illager.targetSelector.addGoal(priority, new NearestAttackableTargetGoal<>(illager, Guard.class, true));
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event)
    {
        MobEffect effect = event.getEffectInstance().getEffect();
        EntityType<?> target = event.getEntity().getType();

        if(effect.equals(DifficultRaidsEffects.WIND_CURSE_EFFECT.get()) && ForgeRegistries.ENTITY_TYPES.tags().getTag(DifficultRaidsConfig.WINDS_CURSE_IMMUNE).contains(target))
            event.setResult(Event.Result.DENY);
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
            int level = stack.getEnchantmentLevel(DifficultRaidsEnchantments.LIGHTNING_RESISTANCE.get());

            float damageMultiplier = switch(level) {
                case 1 -> 0.95F;
                case 2 -> 0.8F;
                case 3 -> 0.7F;
                case 4 -> 0.5F;
                case 5 -> 0.25F;
                default -> 1.0F;
            };

            if(isElectroIllagerBolt) damageMultiplier -= 0.05F;

            lightning.setDamage(lightning.getDamage() * damageMultiplier);
        }
    }

    private static final TargetingConditions NECROMANCER_MINION_CHARGE_TARGETING = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().range(50.0);

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event)
    {
        if(event.getEntity() instanceof Raider raider && raider.getLevel() instanceof ServerLevel serverLevel)
        {
            Optional.ofNullable(serverLevel.getNearestEntity(
                    NecromancerIllagerEntity.class,
                    NECROMANCER_MINION_CHARGE_TARGETING,
                    raider, raider.getX(), raider.getY(), raider.getZ(), raider.getBoundingBox().inflate(50.0)
            )).ifPresent(necro ->
            {
                necro.addMinionCharge();

                necro.playSound(SoundEvents.WITCH_DRINK, 12.0F, 0.7F);
            });
        }
    }

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event)
    {
        if(event.getEntity() instanceof AshenmancerIllagerEntity ashenmancer && !ashenmancer.config().ashenmancer.allowMobGriefing)
            event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event)
    {
        if(event.getSource().equals(DamageSource.WITHER)
                && event.getSource().getEntity() instanceof AshenmancerIllagerEntity ashenmancer
                && ashenmancer.isInDifficultRaid()
        )
            event.setAmount(ashenmancer.config().ashenmancer.witherSkullWitherTickDamage);
    }
}
