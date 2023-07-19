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
import net.minecraft.world.Container;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

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
        RaidDifficulty raidDifficulty = this.difficultraids$getRaidDifficulty();
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
        RaidDifficulty raidDifficulty = this.difficultraids$getRaidDifficulty();
        int wave = this.getGroupsSpawned();

        if(!raidDifficulty.isDefault() && raidDifficulty.config().elitesEnabled.get() && RaidEnemyRegistry.isEliteWave(raidDifficulty, wave))
        {
            EntityType<?> eliteType = RaidEnemyRegistry.getRandomElite(raidDifficulty, wave);

            Entity elite = eliteType == null ? null : eliteType.create(this.level);

            if(elite instanceof Raider raider) this.joinRaid(wave, raider, spawnPos, false);
            else LOGGER.error("Failed to spawn Raid Elite! {EntityType: " + (eliteType == null ? "null" : eliteType.toShortString()) + "}, Wave {" + wave + "}, Difficulty {" + this.level.getDifficulty() + "}");
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = this.difficultraids$getRaidDifficulty();

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
            else if(DifficultRaidsConfig.RESTRICTIVE_MODE.get()) callbackInfoReturnable.setReturnValue(0);
        }
    }

    @Inject(at = @At("HEAD"), method = "getPotentialBonusSpawns", cancellable = true)
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, RandomSource random, int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(!this.difficultraids$getRaidDifficulty().isDefault()) callbackInfoReturnable.setReturnValue(0);
    }

    @Inject(at = @At("HEAD"), method = "getNumGroups", cancellable = true)
    private void difficultraids_getWaveCounts(Difficulty pDifficulty, CallbackInfoReturnable<Integer> cir)
    {
        RaidDifficulty rd = this.difficultraids$getRaidDifficulty();

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
        RaidDifficulty raidDifficulty = this.difficultraids$getRaidDifficulty();

        if(this.isVictory() && !raidDifficulty.isDefault())
        {
            LOGGER.info("DifficultRaids: Generating " + raidDifficulty.getFormattedName() + " Raid Loot!");

            LootTable valuablesLT = this.level.getServer().getLootTables().get(switch(raidDifficulty)
            {
                case DEFAULT, HERO -> RaidLoot.HERO_VALUABLES;
                case LEGEND -> RaidLoot.LEGEND_VALUABLES;
                case MASTER -> RaidLoot.MASTER_VALUABLES;
                case GRANDMASTER -> RaidLoot.GRANDMASTER_VALUABLES;
            });

            LootTable magicLT = this.level.getServer().getLootTables().get(switch(raidDifficulty)
            {
                case DEFAULT, HERO -> RaidLoot.HERO_MAGIC;
                case LEGEND -> RaidLoot.LEGEND_MAGIC;
                case MASTER -> RaidLoot.MASTER_MAGIC;
                case GRANDMASTER -> RaidLoot.GRANDMASTER_MAGIC;
            });

            BlockPos valuablesPos = this.difficultRaids$spawnLootChest("Valuables", valuablesLT);
            BlockPos magicPos = this.difficultRaids$spawnLootChest("Magic", magicLT);

            if(DifficultRaidsConfig.INSANITY_MODE.get()) for(int i = 0; i < DifficultRaidsConfig.INSANITY_COUNT_MULTIPLIER.get() - 1; i++)
            {
                this.difficultRaids$spawnLootChest("Valuables", valuablesLT);
                this.difficultRaids$spawnLootChest("Magic", magicLT);
            }

            //Notify players
            int vX = valuablesPos.getX(); int vY = valuablesPos.getY(); int vZ = valuablesPos.getZ();
            int mX = magicPos.getX(); int mY = magicPos.getY(); int mZ = magicPos.getZ();

            this.heroesOfTheVillage
                    .stream()
                    .map(uuid -> this.level.getPlayerByUUID(uuid))
                    .filter(Objects::nonNull)
                    .forEach(p -> p.sendSystemMessage(
                            Component.literal("The Villagers have granted you gifts at (%s, %s, %s) and (%s, %s, %s)!%s"
                            .formatted(vX, vY, vZ, mX, mY, mZ, DifficultRaidsConfig.INSANITY_MODE.get() ? " Your insanity has granted you additional gifts!" : "")))
                    );

            LOGGER.info("DifficultRaids: Spawned " + raidDifficulty.getFormattedName() + " Raid Loot (V: %s, %s, %s | M: %s, %s, %s)!".formatted(vX, vY, vZ, mX, mY, mZ));
        }
    }

    @Unique
    private BlockPos difficultRaids$spawnLootChest(String type, LootTable table)
    {
        Function<Integer, Integer> randomizer = s -> s + (3 - this.random.nextInt(7));

        //Find suitable spawn location
        BlockPos pos = new BlockPos(
                randomizer.apply(this.center.getX()),
                this.center.getY() + 5,
                randomizer.apply(this.center.getZ())
        );
        while(!this.level.getBlockState(pos).isAir()) pos = pos.offset(randomizer.apply(pos.getX()), 1, randomizer.apply(pos.getZ()));

        //Spawn chest
        this.level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 2);

        //Find chest & fill
        BlockEntity blockEntity = this.level.getExistingBlockEntity(pos);
        if(blockEntity instanceof Container container)
        {
            LootContext context = new LootContext.Builder(this.level)
                    .withLuck(this.level.getDifficulty() == Difficulty.HARD ? 1.0F : 0.0F)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                    .create(LootContextParamSets.CHEST);

            table.fill(container, context);
            container.setChanged();
        }
        else LOGGER.warn("Could not find container for " + type + " Raid Loot at {" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "}!");

        //Return position for messaging
        return pos;
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
        float perPlayerHealthBoost = this.difficultraids$getRaidDifficulty().config().playerHealthBoostAmount.get().floatValue();
        float healthBoost = perPlayerHealthBoost * (this.players - 1);

        AttributeModifier healthBoostModifier = new AttributeModifier("RAID_PLAYER_COUNT_HEALTH_BOOST", healthBoost, AttributeModifier.Operation.ADDITION);

        AttributeInstance health = defaultRaider.getAttribute(Attributes.MAX_HEALTH);
        if(health != null) health.addPermanentModifier(healthBoostModifier);

        return defaultRaider;
    }

    @Unique
    public RaidDifficulty difficultraids$getRaidDifficulty()
    {
        return RaidDifficulty.get(this.getBadOmenLevel());
    }
}
