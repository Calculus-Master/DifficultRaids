package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.raids.RaidLoot;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

import static com.calculusmaster.difficultraids.util.DifficultRaidsUtil.OverflowHandlingMode.ZERO;

@Mixin(Raid.class)
public abstract class RaidMixin
{
    private int players;
    private AABB validRaidArea;

    @Mutable @Shadow @Final private int numGroups;
    @Shadow private int groupsSpawned;
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private RandomSource random;
    @Shadow private BlockPos center;
    @Shadow @Final private Set<UUID> heroesOfTheVillage;
    @Shadow private long ticksActive;

    @Shadow public abstract boolean isVictory();
    @Shadow public abstract boolean isLoss();

    @Shadow public abstract void joinRaid(int p_37714_, Raider p_37715_, @Nullable BlockPos p_37716_, boolean p_37717_);
    @Shadow public abstract int getGroupsSpawned();
    @Shadow public abstract int getBadOmenLevel();
    @Shadow public abstract int getTotalRaidersAlive();
    @Shadow public abstract Set<Raider> getAllRaiders();

    @Shadow @Final private ServerBossEvent raidEvent;

    @Shadow public abstract boolean isOver();
    @Shadow private int celebrationTicks;

    @Shadow public abstract int getNumGroups(Difficulty pDifficulty);

    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private void difficultRaids$initializeValidRaidArea()
    {
        this.validRaidArea = new AABB(this.center).inflate(Math.sqrt(Raid.VALID_RAID_RADIUS_SQR));
        this.players = this.level.getEntitiesOfClass(Player.class, this.validRaidArea).size();
    }

    @Inject(at = @At("TAIL"), method = "absorbBadOmen")
    private void difficultraids_raidStart(Player p_37729_, CallbackInfo callbackInfo)
    {
        this.difficultRaids$initializeValidRaidArea();

        this.numGroups = this.getNumGroups(this.level.getDifficulty());
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void difficultraids_highlightRemainingRaiders(CallbackInfo callback)
    {
        if(this.ticksActive % 20 * 2 == 0 && this.getTotalRaidersAlive() <= DifficultRaidsConfig.HIGHLIGHT_THRESHOLD.get())
            this.getAllRaiders().stream()
                    .filter(LivingEntity::isAlive) //Alive Raiders
                    .filter(r -> !r.hasEffect(MobEffects.GLOWING)) //Not already glowing
                    .forEach(r -> r.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 3, 1, false, false))); //Apply glow
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void difficultraids_addDifficultyToEventBar(CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());
        String title = this.raidEvent.getName().getString();

        if(this.ticksActive % 20L == 0L)
        {
            if(title.toLowerCase().contains("raid") && !title.toLowerCase().contains(raidDifficulty.getFormattedName().toLowerCase()))
            {
                MutableComponent current = Component.literal(this.raidEvent.getName().getString());

                MutableComponent additions = Component.empty();
                if(DifficultRaidsConfig.INSANITY_MODE.get()) additions.append(Component.literal("Insane ").withStyle(ChatFormatting.RED));
                additions.append(raidDifficulty.getFormattedName() + " ");

                MutableComponent wave = DifficultRaidsConfig.SHOW_WAVE_INFORMATION.get() ? Component.literal(" (Wave " + this.getGroupsSpawned() + " of " + this.numGroups + ")").withStyle(ChatFormatting.GRAY) : Component.empty();

                this.raidEvent.setName(additions.append(current).append(wave));
            }
        }

        if(this.isOver() && this.celebrationTicks % 20 == 0)
        {
            if(title.startsWith(Component.translatable("event.minecraft.raid").append(" - ").getString()))
                this.raidEvent.setName(Component.literal(raidDifficulty.getFormattedName() + " ").append(title));
        }
    }

    @Inject(at = @At("HEAD"), method = "spawnGroup")
    private void difficultraids_spawnGroup(BlockPos pos, CallbackInfo callbackInfo)
    {
        if(this.validRaidArea == null) this.difficultRaids$initializeValidRaidArea();

        List<Player> participants = this.level.getEntitiesOfClass(Player.class, this.validRaidArea);
        this.players = participants.size();
    }

    @Inject(at = @At("TAIL"), method = "spawnGroup")
    private void difficultraids_spawnElite(BlockPos spawnPos, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());
        int wave = this.getGroupsSpawned();

        if(raidDifficulty.config().elitesEnabled.get() && RaidEnemyRegistry.isEliteWave(raidDifficulty, wave))
        {
            EntityType<?> eliteType = RaidEnemyRegistry.getRandomElite(raidDifficulty, wave); //DifficultRaidsEntityTypes.NUAOS_ELITE.get();

            Entity elite = eliteType == null ? null : eliteType.create(this.level);

            if(elite instanceof Raider raider) this.joinRaid(wave, raider, spawnPos, false);
            else LOGGER.error("Failed to spawn Raid Elite! {EntityType: " + (eliteType == null ? "null" : eliteType.toShortString()) + "}, Wave {" + wave + "}, Difficulty {" + this.level.getDifficulty() + "}");
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        if(!raidDifficulty.isDefault())
        {
            boolean isRegistered = RaidEnemyRegistry.isRaiderTypeRegistered(raiderType.toString());
            boolean isEnabled = RaidEnemyRegistry.isRaiderTypeEnabled(raiderType.toString());

            //Disable GuardVillagers Illusioner spawns
            if(raiderType.toString().equalsIgnoreCase("thebluemengroup"))
                callbackInfoReturnable.setReturnValue(0);
            //Disable the regular Dungeons Mobs Illusioner spawns (replaced by a custom re-registration of the RaiderType)
            else if(raiderType.toString().equals("illusioner"))
                callbackInfoReturnable.setReturnValue(0);

            //Check if the Raider Type is enabled
            else if(isRegistered && !isEnabled) callbackInfoReturnable.setReturnValue(0);
            //Add default compatibility with other mods, so if a new raider type isn't in the registry the game won't crash
            else if(isRegistered)
            {
                //Spawns per wave array
                List<Integer> spawnsPerWave = RaidEnemyRegistry.getWaves(raidDifficulty, raiderType.toString());

                int waveIndex = spawnBonusGroup ? this.numGroups : groupsSpawned;

                int count;
                if(waveIndex >= spawnsPerWave.size()) count = DifficultRaidsConfig.OVERFLOW_MODE.get() == ZERO ? 0 : spawnsPerWave.get(spawnsPerWave.size() - 1);
                else count = spawnsPerWave.get(waveIndex);

                if(DifficultRaidsConfig.INSANITY_MODE.get()) count *= DifficultRaidsConfig.INSANITY_COUNT_MULTIPLIER.get();

                callbackInfoReturnable.setReturnValue(count);
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getPotentialBonusSpawns", cancellable = true)
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, RandomSource random, int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(!RaidDifficulty.get(this.getBadOmenLevel()).isDefault()) callbackInfoReturnable.setReturnValue(0);
    }

    @Inject(at = @At("HEAD"), method = "getNumGroups", cancellable = true)
    private void difficultraids_getWaveCounts(Difficulty pDifficulty, CallbackInfoReturnable<Integer> cir)
    {
        RaidDifficulty rd = RaidDifficulty.get(this.getBadOmenLevel());

        if(!rd.isDefault())
        {
            cir.setReturnValue(switch(pDifficulty)
            {
                case PEACEFUL -> 0;
                case EASY -> DifficultRaidsConfig.WAVE_COUNT_EASY.get();
                case NORMAL -> DifficultRaidsConfig.WAVE_COUNT_NORMAL.get();
                case HARD -> DifficultRaidsConfig.WAVE_COUNT_HARD.get();
            });
        }
    }

    @Inject(at = @At("HEAD"), method = "stop")
    public void difficultraids_grantRewards(CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        if(this.isVictory() && !raidDifficulty.isDefault())
        {
            BlockPos rewardPos = new BlockPos(this.center.getX(), this.center.getY() + 7, this.center.getZ());

            RaidLoot.RaidLootData data = RaidLoot.RAID_LOOT.get(raidDifficulty);

            //Emeralds
            int emeralds = this.random.nextInt(data.emeralds[0], data.emeralds[1] + 1);

            for(int i = 0; i < emeralds; i++)
            {
                BlockPos pos = rewardPos.offset(this.random.nextInt(11) - 5, 0, this.random.nextInt(11) - 5);

                ItemEntity entityItem = new ItemEntity(this.level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.EMERALD));
                entityItem.setExtendedLifetime();

                this.level.addFreshEntity(entityItem);
            }

            //Totems
            int totems = data.totemsPulls.get(this.level.getDifficulty());

            for(int i = 0; i < totems; i++)
            {
                BlockPos pos = rewardPos.offset(this.random.nextInt(5) - 2, 0, this.random.nextInt(5) - 2);

                Item totem = data.totemsPool.get(this.random.nextInt(data.totemsPool.size()));
                ItemEntity entityItem = new ItemEntity(this.level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(totem));
                entityItem.setExtendedLifetime();

                this.level.addFreshEntity(entityItem);
            }

            //Valuables
            int valuables = data.valuablesPulls.get(this.level.getDifficulty());

            Map<Item, Integer> valuablesLoot = new HashMap<>();
            for(int i = 0; i < valuables; i++)
            {
                Item item = data.pullValuable(this.random);

                if(item != null) valuablesLoot.put(item, valuablesLoot.getOrDefault(item, 0) + 1);
                else LOGGER.error("Error pulling valuables Loot Item from a " + raidDifficulty.getFormattedName() + " Raid!");
            }

            valuablesLoot.forEach((item, count) -> {
                ItemStack stack = new ItemStack(item, count);

                ItemEntity entityItem = new ItemEntity(this.level, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), stack);
                entityItem.setExtendedLifetime();
                this.level.addFreshEntity(entityItem);
            });

            //Armor
            List<ItemStack> armor = RaidLoot.generateArmorLoot(raidDifficulty);

            armor.forEach(stack -> {
                ItemEntity entityItem = new ItemEntity(this.level, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), stack);
                entityItem.setExtendedLifetime();
                this.level.addFreshEntity(entityItem);
            });

            //Enchantments
            for(int i = 0; i < data.enchantmentCount; i++)
            {
                ItemStack book = data.pullEnchantment(this.random);

                ItemEntity entityItem = new ItemEntity(this.level, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), book);
                entityItem.setExtendedLifetime();
                this.level.addFreshEntity(entityItem);
            }

            //Notify players
            this.heroesOfTheVillage.stream().map(uuid -> this.level.getPlayerByUUID(uuid)).filter(Objects::nonNull).forEach(p -> {
                p.sendSystemMessage(Component.literal("Raid Rewards have spawned at X: %s Y: %s Z: %s!".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ())));
            });
        }
    }

    @ModifyVariable(at = @At("HEAD"), method = "joinRaid", ordinal = 0, argsOnly = true)
    private BlockPos difficultraids_randomizeSpawnPos(BlockPos spawnPos)
    {
        if(spawnPos != null)
        {
            BlockPos spawnOffset;
            int tries = 0;
            do { spawnOffset = spawnPos.offset(this.random.nextInt(7) - 3, 0, this.random.nextInt(7) - 3); }
            while(!this.level.getBlockState(spawnOffset).isAir() && ++tries < 3);

            return spawnOffset;
        }
        else return spawnPos;
    }

    @ModifyVariable(at = @At("HEAD"), method = "joinRaid", ordinal = 0, argsOnly = true)
    private Raider difficultraids_boostRaiderFromPlayerCount(Raider defaultRaider)
    {
        float perPlayerHealthBoost = RaidDifficulty.get(this.getBadOmenLevel()).config().playerHealthBoostAmount.get().floatValue();
        float healthBoost = perPlayerHealthBoost * (this.players - 1);

        AttributeModifier healthBoostModifier = new AttributeModifier("RAID_PLAYER_COUNT_HEALTH_BOOST", healthBoost, AttributeModifier.Operation.ADDITION);

        AttributeInstance health = defaultRaider.getAttribute(Attributes.MAX_HEALTH);
        if(health != null) health.addPermanentModifier(healthBoostModifier);

        return defaultRaider;
    }
}
