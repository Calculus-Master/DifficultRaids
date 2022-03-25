package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.util.RaidDifficulty;
import com.calculusmaster.difficultraids.util.RaidReinforcements;
import com.calculusmaster.difficultraids.util.RaiderDefaultSpawns;
import com.calculusmaster.difficultraids.util.WeightedRewardPool;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(Raid.class)
public abstract class MixinRaid
{
    private RaidReinforcements raidReinforcements;

    @Shadow @Final private int numGroups;
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Random random;
    @Shadow private BlockPos center;
    @Shadow @Final private Set<UUID> heroesOfTheVillage;

    @Shadow public abstract boolean isVictory();
    @Shadow public abstract boolean isLoss();

    private static final Logger LOGGER = LogUtils.getLogger();

    private static void outputLog(String text)
    {
        LOGGER.info("Difficult Raids - Log Info - [[ " + text + " ]]");
    }

    @Inject(at = @At("HEAD"), method = "spawnGroup", cancellable = true)
    private void difficultraids_spawnGroup(BlockPos pos, CallbackInfo callbackInfo)
    {
        Difficulty worldDifficulty = this.level.getDifficulty();
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        int bonusChance = switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
            case DEFAULT -> 10;
            case HERO -> 15;
            case LEGEND -> 25;
            case MASTER -> 50;
            case APOCALYPSE -> 100;
            case DEBUG -> 0;
        };

        if(this.random.nextInt(100) < bonusChance)
        {
            this.raidReinforcements = RaidReinforcements.getRandom();

            //TODO: Only send to players within the raid boundaries
            Minecraft.getInstance().player.sendMessage(
                    new TextComponent("The " + this.raidReinforcements.getChatName() + " has spawned!"),
                    Minecraft.getInstance().player.getUUID()
            );
        }
        else this.raidReinforcements = null;

        //Non-Raider Entity Reinforcements
        if(this.raidReinforcements != null)
        {
            for(Map.Entry<EntityType<?>, Integer> entityEntry : this.raidReinforcements.getGenericReinforcements(worldDifficulty, raidDifficulty).entrySet())
            {
                for(int i = 0; i < entityEntry.getValue(); i++)
                {
                    Entity spawn = entityEntry.getKey().create(this.level);
                    spawn.setPos(pos.getX(), pos.getY(), pos.getZ());

                    if(entityEntry.getKey().equals(EntityType.ZOMBIE))
                    {
                        Item helmet = switch(raidDifficulty) {
                            case HERO -> Items.CHAINMAIL_HELMET;
                            case LEGEND -> Items.IRON_HELMET;
                            case MASTER -> Items.DIAMOND_HELMET;
                            case APOCALYPSE -> Items.NETHERITE_HELMET;
                            default -> Items.LEATHER_HELMET;
                        };

                        spawn.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet));
                    }

                    this.level.addFreshEntity(spawn);
                }
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();
        Difficulty worldDifficulty = this.level.getDifficulty();

        if(raidDifficulty.equals(RaidDifficulty.DEBUG))
        {
            callbackInfoReturnable.setReturnValue(1);
            return;
        }

        outputLog("Searching for Default Spawns: Raider Type {%s}, Raid Difficulty {%s}".formatted(raiderType.toString(), raidDifficulty.toString()));
        //Spawns per wave array
        int[] spawnsPerWave = RaiderDefaultSpawns.getDefaultSpawns(raiderType.toString(), raidDifficulty);
        //Selected spawns for the current wave
        int baseSpawnCount = spawnBonusGroup ? spawnsPerWave[this.numGroups] : spawnsPerWave[groupsSpawned];

        //Modifiers based on Game Difficulty (Default and Apocalypse ignore this)
        if(!List.of(RaidDifficulty.DEFAULT, RaidDifficulty.APOCALYPSE).contains(raidDifficulty))
        {
            switch(worldDifficulty)
            {
                case PEACEFUL -> baseSpawnCount = 0; //Don't think this ever executes?
                //BSC ranges from BSC - 3 to BSC - 1 -- Minimum: 0
                case EASY -> baseSpawnCount = Math.max(0, baseSpawnCount - this.random.nextInt(1, 4));
                //BSC ranges from BSC - 2 to BSC + 2 -- Minimum: 0 if no mobs are supposed to spawn this wave, 1 if any are
                case NORMAL -> baseSpawnCount = Math.max(baseSpawnCount == 0 ? 0 : 1, baseSpawnCount + (this.random.nextInt(3) - 1) * (this.random.nextInt(1, 3)));
                //BSC ranges from BSC to BSC + 5 -- Minimum: 1
                case HARD -> baseSpawnCount = Math.max(1, baseSpawnCount + this.random.nextInt(6));
            }
        }

        MixinRaid.outputLog(
                "Default Spawns: Raider Type {%s}, Spawns per Wave {%s}, Selected Spawn Count {%s}, Difficulty {World: %s, Raid: %s}"
                .formatted(raiderType.toString(), Arrays.toString(spawnsPerWave), baseSpawnCount, worldDifficulty, raidDifficulty)
        );

        callbackInfoReturnable.setReturnValue(baseSpawnCount);
    }

    @Inject(at = @At("HEAD"), method = "getPotentialBonusSpawns", cancellable = true)
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, Random random,
            int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(this.raidReinforcements != null)
        {
            Difficulty worldDifficulty = difficultyInstance.getDifficulty();
            RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

            int count = this.raidReinforcements.getRaiderReinforcementCount(raiderType, worldDifficulty, raidDifficulty);

            MixinRaid.outputLog(
                    "Bonus Spawns: Raider Type {%s}, Spawn Count {%s}, Difficulty {World: %s, Raid: %s}"
                    .formatted(raiderType.toString(), count, worldDifficulty.toString(), raidDifficulty.toString())
            );

            callbackInfoReturnable.setReturnValue(count);
        }
        else MixinRaid.outputLog("BonusRaidSpawnPreset is null! Defaulting to vanilla Minecraft bonus spawn groups...");
    }

    @Inject(at = @At("HEAD"), method = "stop")
    public void difficultraids_grantRewards(CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        if(this.isVictory() && !List.of(RaidDifficulty.DEFAULT, RaidDifficulty.DEBUG).contains(raidDifficulty))
        {
            WeightedRewardPool pool = new WeightedRewardPool()
                    .add(Items.TOTEM_OF_UNDYING, 10, switch(raidDifficulty) {
                        case HERO -> 2;
                        case LEGEND -> 5;
                        case MASTER -> 8;
                        case APOCALYPSE -> 12;
                        default -> 0;
                    }, 1)
                    .add(Items.IRON_INGOT, 15, switch(raidDifficulty) {
                        case HERO -> 10;
                        case LEGEND -> 15;
                        case MASTER -> 20;
                        case APOCALYPSE -> 40;
                        default -> 0;
                    }, 100)
                    .add(Items.DIAMOND, 5, switch(raidDifficulty) {
                        case HERO -> 1;
                        case LEGEND -> 2;
                        case MASTER -> 5;
                        case APOCALYPSE -> 8;
                        default -> 0;
                    }, 1)
                    .add(Items.EMERALD, 12, switch(raidDifficulty) {
                        case HERO -> 6;
                        case LEGEND -> 9;
                        case MASTER -> 12;
                        case APOCALYPSE -> 20;
                        default -> 0;
                    }, 3)
                    .add(Items.LEATHER, 12, switch(raidDifficulty) {
                        case HERO -> 7;
                        case LEGEND -> 10;
                        case MASTER -> 15;
                        case APOCALYPSE -> 26;
                        default -> 0;
                    }, 5);

            List<ItemStack> rewards = new ArrayList<>();

            for(int i = 0; i < switch(raidDifficulty) {
                case HERO -> 3;
                case LEGEND -> 5;
                case MASTER -> 7;
                case APOCALYPSE -> 10;
                default -> 0;
            }; i++) rewards.add(pool.pull());

            BlockPos rewardPos = new BlockPos(this.center.getX(), this.center.getY(), this.center.getZ() + 10);
            rewards.forEach(stack -> {
                ItemEntity entityItem = new ItemEntity(this.level, rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), stack);
                entityItem.setExtendedLifetime();

                this.level.addFreshEntity(entityItem);
            });

            MixinRaid.outputLog("Raid Rewards Generated at X: %s Y: %s Z: %s, Rewards List: %s".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), rewards.stream().map(stack -> stack.getItem().getRegistryName() + " (x" + stack.getCount() + ")")));

            this.heroesOfTheVillage.stream().map(uuid -> this.level.getPlayerByUUID(uuid)).filter(Objects::nonNull).forEach(p -> {
                p.sendMessage(
                        new TextComponent("Raid Rewards have spawned at X: %s Y: %s Z: %s!".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ())),
                        p.getUUID()
                );
            });
        }
        else if(this.isLoss() && raidDifficulty.equals(RaidDifficulty.APOCALYPSE) && DifficultRaidsConfig.RAID_LOSS_APOCALYPSE_SHOULD_WITHER_SPAWN.get())
        {
            WitherBoss wither = EntityType.WITHER.create(this.level);

            wither.setCustomName(new TextComponent("The Apocalypse"));
            wither.addEffect(new MobEffectInstance(MobEffects.GLOWING));
            wither.setPos(this.center.getX(), this.center.getY(), this.center.getZ() + 10);

            MixinRaid.outputLog("Wither Boss spawned at X: %s Y: %s Z: %s!".formatted(this.center.getX(), this.center.getY(), this.center.getZ() + 10));

            this.level.addFreshEntity(wither);
        }
    }
}
