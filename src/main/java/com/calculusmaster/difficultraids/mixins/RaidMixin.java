package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.entity.DifficultRaidsEntityTypes;
import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.raids.RaidLoot;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.Item;
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
    private void difficultraids_highlightRemainingRaiders(CallbackInfo callback)
    {
        if(this.ticksActive % 5 == 0 && this.getTotalRaidersAlive() <= 3)
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

        if(title.toLowerCase().contains("raid") && !title.toLowerCase().contains(raidDifficulty.getFormattedName().toLowerCase()))
            this.raidEvent.setName(Component.literal(raidDifficulty.getFormattedName() + " " + title));
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
        if(this.random.nextInt(100) < raidDifficulty.config().reinforcementChance())
        {
            Map<EntityType<?>, Integer> reinforcements = RaidEnemyRegistry.getReinforcements(this.groupsSpawned, raidDifficulty, levelDifficulty);
            final String sum = "(" + reinforcements.values().stream().mapToInt(i -> i).sum() + ")";
            final List<String> messages = List.of("Reinforcements have arrived!", "Additional mobs have joined!", "An extra group of mobs has appeared!", "The Illagers have called in reinforcements!", "The Illagers have called for backup!");
            participants.forEach(p -> p.sendSystemMessage(Component.literal(messages.get(this.random.nextInt(messages.size())) + " " + sum)));

            for(Map.Entry<EntityType<?>, Integer> entityEntry : reinforcements.entrySet())
            {
                for(int i = 0; i < entityEntry.getValue(); i++)
                {
                    EntityType<?> type = entityEntry.getKey();
                    LivingEntity spawn = (LivingEntity)type.create(this.level); if(spawn == null) continue;
                    spawn.moveTo(pos.getX(), pos.getY(), pos.getZ()); //TODO: Randomize spawn a bit more
                    if(spawn instanceof Monster mob) mob.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);

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

                        if(raidDifficulty.is(RaidDifficulty.GRANDMASTER))
                        {
                            monster.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                            monster.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
                            monster.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
                            monster.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));

                            monster.setDropChance(EquipmentSlot.HEAD, 0);
                            monster.setDropChance(EquipmentSlot.CHEST, 0);
                            monster.setDropChance(EquipmentSlot.LEGS, 0);
                            monster.setDropChance(EquipmentSlot.FEET, 0);
                        }
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

            if(true || eliteTier != -1) //TODO: Remove after Elite Testing is done
            {
                EntityType<?> eliteType = DifficultRaidsEntityTypes.NUAOS_ELITE.get(); //RaidEnemyRegistry.getRandomElite(eliteTier);
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
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, RandomSource random, int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(!RaidDifficulty.get(this.getBadOmenLevel()).isDefault()) callbackInfoReturnable.setReturnValue(0);
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
        else if(false && this.isLoss() && raidDifficulty.is(RaidDifficulty.GRANDMASTER)) //TODO: Add config option or rework
        {
            WitherBoss wither = EntityType.WITHER.create(this.level);

            wither.setCustomName(Component.literal("The Apocalypse"));
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
