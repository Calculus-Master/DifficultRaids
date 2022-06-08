package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.raids.RaidLoot;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(Raid.class)
public abstract class RaidMixin
{
    private int players;
    private AABB validRaidArea;

    @Shadow @Final private int numGroups;
    @Shadow private int groupsSpawned;
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Random random;
    @Shadow private BlockPos center;
    @Shadow @Final private Set<UUID> heroesOfTheVillage;

    @Shadow public abstract boolean isVictory();
    @Shadow public abstract boolean isLoss();

    @Shadow public abstract void joinRaid(int p_37714_, Raider p_37715_, @Nullable BlockPos p_37716_, boolean p_37717_);
    @Shadow public abstract int getGroupsSpawned();
    @Shadow public abstract int getBadOmenLevel();

    @Shadow @Final private ServerBossEvent raidEvent;
    private static final Logger LOGGER = LogUtils.getLogger();

    private void initializeValidRaidArea()
    {
        this.validRaidArea = new AABB(this.center).inflate(Math.sqrt(Raid.VALID_RAID_RADIUS_SQR));
        this.players = this.level.getEntitiesOfClass(Player.class, this.validRaidArea).size();
    }

    @Inject(at = @At("TAIL"), method = "absorbBadOmen")
    private void difficultraids_raidStart(Player p_37729_, CallbackInfo callbackInfo)
    {
        this.initializeValidRaidArea();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void difficultraids_addDifficultyToEventBar(CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());
        String title = this.raidEvent.getName().getString();

        if(title.toLowerCase().contains("raid") && !title.toLowerCase().contains(raidDifficulty.getFormattedName().toLowerCase()))
            this.raidEvent.setName(new TextComponent(raidDifficulty.getFormattedName() + " " + title));
    }

    @Inject(at = @At("HEAD"), method = "spawnGroup")
    private void difficultraids_spawnGroup(BlockPos pos, CallbackInfo callbackInfo)
    {
        if(this.validRaidArea == null) this.initializeValidRaidArea();

        List<Player> participants = this.level.getEntitiesOfClass(Player.class, this.validRaidArea);
        this.players = participants.size();

        Difficulty levelDifficulty = this.level.getDifficulty();
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        //Entity Reinforcements (no Raiders)
        if(false && this.random.nextInt(100) < raidDifficulty.config().reinforcementChance())
        {
            Map<EntityType<?>, Integer> reinforcements = RaidEnemyRegistry.getReinforcements(this.groupsSpawned, raidDifficulty, levelDifficulty);
            final String sum = "(" + reinforcements.values().stream().mapToInt(i -> i).sum() + ")";
            final List<String> messages = List.of("Reinforcements have arrived!", "Additional mobs have joined!", "An extra group of mobs has appeared!", "The Illagers have called in reinforcements!", "The Illagers have called for backup!");
            participants.forEach(p -> p.sendMessage(new TextComponent(messages.get(this.random.nextInt(messages.size())) + " " + sum), p.getUUID()));

            for(Map.Entry<EntityType<?>, Integer> entityEntry : reinforcements.entrySet())
            {
                for(int i = 0; i < entityEntry.getValue(); i++)
                {
                    EntityType<?> type = entityEntry.getKey();
                    LivingEntity spawn = (LivingEntity)type.create(this.level);
                    if(spawn == null) continue; //This shouldn't execute, but just in case
                    spawn.moveTo(pos.getX(), pos.getY(), pos.getZ()); //TODO: Randomize spawn a bit more
                    if(spawn instanceof Monster mob) mob.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                    spawn.setOnGround(true);

                    if(List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.STRAY).contains(entityEntry.getKey()))
                        spawn.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));

                    if(spawn instanceof Monster monster)
                    {
                        Path path = monster.getNavigation().createPath(this.center, 10);
                        monster.getNavigation().moveTo(path, 1.3);

                        monster.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(monster, Villager.class, true));
                        monster.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(monster, IronGolem.class, true));
                        monster.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(monster, Player.class, true));
                        //TODO: Reenable this: if(DifficultRaidsUtil.isGuardVillagersLoaded()) monster.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(monster, Guard.class, true));
                    }
                    else if(spawn instanceof Animal animal)
                    {
                        Path path = animal.getNavigation().createPath(this.center, 15);
                        animal.getNavigation().moveTo(path, 0.5);
                    }

                    this.level.addFreshEntity(spawn);
                }
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "spawnGroup")
    private void difficultraids_spawnElite(BlockPos spawnPos, CallbackInfo callback)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());
        if(raidDifficulty.config().areElitesEnabled() && raidDifficulty.is(RaidDifficulty.LEGEND, RaidDifficulty.MASTER, RaidDifficulty.GRANDMASTER))
        {
            int wave = this.getGroupsSpawned();
            int eliteTier = RaidEnemyRegistry.getEliteWaveTier(this.level.getDifficulty(), wave);

            //TODO: Remove when reworking waves
            if(raidDifficulty.is(RaidDifficulty.LEGEND) && eliteTier == 2) eliteTier = 1;

            if(eliteTier != -1)
            {
                EntityType<?> eliteType = RaidEnemyRegistry.getRandomElite(eliteTier);
                Entity elite = eliteType.create(this.level);
                if(elite instanceof Raider raider) this.joinRaid(wave, raider, spawnPos, false);
                else LOGGER.error("Failed to spawn Raid Elite! {EntityType: " + eliteType.toShortString() + "}, Wave {" + wave + "}, Elite Tier: {" + eliteTier + "}, Difficulty {" + this.level.getDifficulty() + "}");
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        boolean isDefault = raidDifficulty.isDefault();
        boolean isRegistered = RaidEnemyRegistry.isRaiderTypeRegistered(raiderType.toString());
        boolean isEnabled = RaidEnemyRegistry.isRaiderTypeEnabled(raiderType.toString());

        //Disable GuardVillagers Illusioner spawns
        if(!isDefault && DifficultRaidsUtil.isGuardVillagersLoaded() && raiderType.toString().equalsIgnoreCase("thebluemengroup"))
            callbackInfoReturnable.setReturnValue(0);
        //Check if the Raider Type is enabled
        else if(isRegistered && !isEnabled) callbackInfoReturnable.setReturnValue(0);
        //Add default compatibility with other mods, so if a new raider type isn't in the registry the game won't crash
        else if(!isDefault && isRegistered)
        {
            //Spawns per wave array
            int[] spawnsPerWave = RaidEnemyRegistry.getWaves(raidDifficulty, raiderType.toString());

            //Selected spawns for the current wave
            int baseSpawnCount = spawnBonusGroup ? spawnsPerWave[this.numGroups] : spawnsPerWave[groupsSpawned];

            callbackInfoReturnable.setReturnValue(baseSpawnCount);
        }
    }

    @Inject(at = @At("HEAD"), method = "getPotentialBonusSpawns", cancellable = true)
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, Random random, int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(!RaidDifficulty.get(this.getBadOmenLevel()).isDefault()) callbackInfoReturnable.setReturnValue(0);
    }

    @Inject(at = @At("HEAD"), method = "stop")
    public void difficultraids_grantRewards(CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.get(this.getBadOmenLevel());

        if(this.isVictory() && !raidDifficulty.isDefault())
        {
            List<ItemStack> rewards = RaidLoot.generate(raidDifficulty, this.level.getDifficulty());

            //TODO: Temporary
            if(raidDifficulty.is(RaidDifficulty.GRANDMASTER))
            {
                rewards.addAll(RaidLoot.generate(RaidDifficulty.MASTER, this.level.getDifficulty()));
                rewards.addAll(RaidLoot.generate(RaidDifficulty.MASTER, this.level.getDifficulty()));
                rewards.addAll(RaidLoot.generate(List.of(RaidDifficulty.HERO, RaidDifficulty.LEGEND, RaidDifficulty.MASTER).get(this.random.nextInt(3)), this.level.getDifficulty()));
            }

            BlockPos rewardPos = new BlockPos(this.center.getX(), this.center.getY(), this.center.getZ() + 10);
            rewards.forEach(stack -> {
                int x = rewardPos.getX();
                int y = rewardPos.getY();
                int z = rewardPos.getZ();

                ItemEntity entityItem = new ItemEntity(this.level, this.random.nextInt(x - 5, x + 6), this.random.nextInt(y - 5, y + 6), this.random.nextInt(z - 5, z + 6), stack);
                entityItem.setExtendedLifetime();

                this.level.addFreshEntity(entityItem);
            });
            //TODO: Chest with loot instead of dropping on ground

            this.heroesOfTheVillage.stream().map(uuid -> this.level.getPlayerByUUID(uuid)).filter(Objects::nonNull).forEach(p -> {
                p.sendMessage(
                        new TextComponent("Raid Rewards have spawned at X: %s Y: %s Z: %s!".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ())),
                        p.getUUID()
                );
            });
        }
        else if(this.isLoss() && raidDifficulty.is(RaidDifficulty.GRANDMASTER))
        {
            WitherBoss wither = EntityType.WITHER.create(this.level);

            wither.setCustomName(new TextComponent("The Apocalypse"));
            wither.setPos(this.center.getX(), this.center.getY() + 10, this.center.getZ());

            this.level.addFreshEntity(wither);
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
        float healthBoost = switch(this.players) {
            case 1 -> 0.0F;
            case 2 -> 0.5F;
            case 3 -> 1.0F;
            case 4 -> 1.75F;
            case 5 -> 2.5F;
            case 6 -> 3.5F;
            case 7 -> 5.0F;
            default -> 5.0F + this.players * 1.25F;
        };

        AttributeModifier healthBoostModifier = new AttributeModifier("RAID_PLAYER_COUNT_HEALTH_BOOST", healthBoost, AttributeModifier.Operation.ADDITION);

        AttributeInstance health = defaultRaider.getAttribute(Attributes.MAX_HEALTH);
        if(health != null) health.addPermanentModifier(healthBoostModifier);

        return defaultRaider;
    }
}
