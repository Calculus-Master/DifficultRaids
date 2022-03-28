package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidReinforcements;
import com.calculusmaster.difficultraids.raids.RaiderSpawnRegistry;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.util.WeightedRewardPool;
import com.mojang.logging.LogUtils;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
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
    private int players;
    private AABB validRaidArea;

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

    @Inject(at = @At("TAIL"), method = "absorbBadOmen")
    private void difficultraids_raidStart(Player p_37729_, CallbackInfo callbackInfo)
    {
        this.validRaidArea = new AABB(this.center).inflate(Math.sqrt(Raid.VALID_RAID_RADIUS_SQR));
        this.players = this.level.getEntitiesOfClass(Player.class, this.validRaidArea).size();
    }

    @Inject(at = @At("HEAD"), method = "spawnGroup")
    private void difficultraids_spawnGroup(BlockPos pos, CallbackInfo callbackInfo)
    {
        List<Player> participants = this.level.getEntitiesOfClass(Player.class, this.validRaidArea);
        this.players = participants.size();

        Difficulty worldDifficulty = this.level.getDifficulty();
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        if(this.random.nextInt(100) < raidDifficulty.reinforcementChance)
        {
            this.raidReinforcements = RaidReinforcements.getRandom();

            participants.forEach(p -> p.sendMessage(
                        new TextComponent("Raid Reinforcements have arrived!"), //TODO: Remove RaidReinforcements#chatName field – Ideas: have different generic messages, give information of what spawns, ??
                        p.getUUID()));
        }
        else this.raidReinforcements = null;

        //Non-Raider Entity Reinforcements
        if(this.raidReinforcements != null)
        {
            for(Map.Entry<EntityType<?>, Integer> entityEntry : this.raidReinforcements.getGenericReinforcements(worldDifficulty, raidDifficulty).entrySet())
            {
                for(int i = 0; i < entityEntry.getValue(); i++)
                {
                    EntityType<?> type = entityEntry.getKey();
                    Entity spawn = type.create(this.level);
                    spawn.setPos(pos.getX(), pos.getY(), pos.getZ());

                    int creeperInvisChance = DifficultRaidsConfig.RAID_CREEPER_INVIS_CHANCE_MASTER.get() + (raidDifficulty.equals(RaidDifficulty.APOCALYPSE) ? 5 : 0);
                    if(creeperInvisChance != 0 &&
                            type.equals(EntityType.CREEPER) &&
                            List.of(RaidDifficulty.MASTER, RaidDifficulty.APOCALYPSE).contains(raidDifficulty) &&
                            this.random.nextInt(100) < DifficultRaidsConfig.RAID_CREEPER_INVIS_CHANCE_MASTER.get())
                        ((Monster)spawn).addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 15));

                    if(DifficultRaidsConfig.RAID_PREVENT_SUNLIGHT_BURNING_HELMETS.get() &&
                            (entityEntry.getKey().equals(EntityType.ZOMBIE) || entityEntry.getKey().equals(EntityType.SKELETON) || entityEntry.getKey().equals(EntityType.STRAY)))
                        spawn.setItemSlot(EquipmentSlot.HEAD, new ItemStack(raidDifficulty.daylightHelmet));

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

        //outputLog("Searching for Default Spawns: Raider Type {%s}, Raid Difficulty {%s}".formatted(raiderType.toString(), raidDifficulty.toString()));

        //Spawns per wave array
        int[] spawnsPerWave = RaiderSpawnRegistry.getDefaultSpawns(raiderType.toString(), raidDifficulty);
        //Selected spawns for the current wave
        int baseSpawnCount = spawnBonusGroup ? spawnsPerWave[this.numGroups] : spawnsPerWave[groupsSpawned];

        //Modifiers based on Game Difficulty (Default and Apocalypse ignore this)
        if(!List.of(RaidDifficulty.DEFAULT, RaidDifficulty.APOCALYPSE).contains(raidDifficulty) && baseSpawnCount != 0 && !raiderType.equals(Raid.RaiderType.RAVAGER))
        {
            switch(worldDifficulty)
            {
                case PEACEFUL -> baseSpawnCount = 0; //Don't think this ever executes?
                //BSC ranges from BSC - 3 to BSC - 1 -- Minimum: 0
                case EASY -> baseSpawnCount = this.random.nextInt(baseSpawnCount - 3, baseSpawnCount);
                //BSC ranges from BSC - 2 to BSC + 2 -- Minimum: 0 if no mobs are supposed to spawn this wave, 1 if any are
                case NORMAL -> baseSpawnCount = this.random.nextInt(baseSpawnCount - 2, baseSpawnCount + 2 + 1);
                //BSC ranges from BSC to BSC + 5 -- Minimum: 1
                case HARD -> baseSpawnCount = this.random.nextInt(baseSpawnCount, baseSpawnCount + 3 + 1);
            }

            if(baseSpawnCount < 0) baseSpawnCount = 0;
        }

        //Modifiers based on Player Count
        baseSpawnCount *= 1 + this.players * switch(raidDifficulty) {
            case HERO -> 0.05;
            case LEGEND -> 0.1;
            case MASTER -> 0.15;
            case APOCALYPSE -> 0.2;
            default -> 0.0;
        };

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
                    }, 5)
                    .add(Items.GOLD_INGOT, 5, switch(raidDifficulty) {
                        case HERO -> 8;
                        case LEGEND -> 12;
                        case MASTER -> 18;
                        case APOCALYPSE -> 25;
                        default -> 0;
                    }, 3);

            List<ItemStack> rewards = new ArrayList<>();

            for(int i = 0; i < raidDifficulty.totalLootDrops; i++) rewards.add(pool.pull());

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

    /**
     * @author CalculusMaster
     * @reason Changing the wave counts based on RaidDifficulty and World Difficulty
     */
    //TODO: Reenable after changing default spawn arrays
    //TODO: Check the applyRaidBuffs methods in Vindicator, Pillager, Evoker, Ravager, (?)Witch
    //@Overwrite
    public int getNumGroups(Difficulty p_37725_)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

        if(raidDifficulty.equals(RaidDifficulty.DEFAULT))
        {
            //Vanilla Defaults
            return switch(p_37725_) {
                case EASY -> 3;
                case NORMAL -> 5;
                case HARD -> 7;
                case PEACEFUL -> 0;
            };
        }
        else if(raidDifficulty.equals(RaidDifficulty.DEBUG)) return 1;

        //Base Waves (from RaidDifficulty)
        int waves = switch(raidDifficulty) {
            case HERO -> 5;
            case LEGEND -> 7;
            case MASTER -> 9;
            case APOCALYPSE -> 11;
            default -> 0;
        };

        //Waves Modifier (from World Difficulty)
        waves += switch(p_37725_) {
            case PEACEFUL -> -waves;
            case EASY -> -2;
            case NORMAL -> 0;
            case HARD -> +2;
        };

        return waves;
    }
}
