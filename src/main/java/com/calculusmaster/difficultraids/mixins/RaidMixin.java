package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.calculusmaster.difficultraids.raids.RaidLoot;
import com.calculusmaster.difficultraids.util.DifficultRaidsUtil;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.pathfinder.Path;
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

    private static final Logger LOGGER = LogUtils.getLogger();

    private static void outputLog(String text)
    {
        LOGGER.info("DR Log - [[ " + text + " ]]");
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

        Difficulty levelDifficulty = this.level.getDifficulty();
        RaidDifficulty raidDifficulty = RaidDifficulty.current();

        //Entity Reinforcements (no Raiders)
        if(this.random.nextInt(100) < raidDifficulty.config().reinforcementChance())
        {
            Map<EntityType<?>, Integer> reinforcements = RaidEnemyRegistry.generateReinforcements(this.groupsSpawned, raidDifficulty, levelDifficulty);
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
                    spawn.moveTo(pos.getX(), pos.getY(), pos.getZ());
                    if(spawn instanceof Monster mob) mob.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.REINFORCEMENT, null, null);
                    spawn.setOnGround(true);

                    if(List.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.STRAY).contains(entityEntry.getKey()))
                        spawn.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));

                    if(spawn instanceof Monster monster)
                    {
                        Path path = monster.getNavigation().createPath(this.center, 10);
                        monster.getNavigation().moveTo(path, 1.3);
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

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.current();
        Difficulty worldDifficulty = this.level.getDifficulty();

        if(!raidDifficulty.isDefault())
        {
            //Guard Villagers also adds Illusioners to Raids - Disable that, so we use our Illusioner wave values
            if(DifficultRaidsUtil.isGuardVillagersLoaded())
            {
                if(raiderType.toString().equalsIgnoreCase("thebluemengroup"))
                {
                    callbackInfoReturnable.setReturnValue(0);
                    return;
                }
            }

            //Spawns per wave array
            int[] spawnsPerWave = RaidEnemyRegistry.getDefaultSpawns(raiderType.toString(), raidDifficulty);
            //Selected spawns for the current wave
            int baseSpawnCount = spawnBonusGroup ? spawnsPerWave[this.numGroups] : spawnsPerWave[groupsSpawned];

            //Modifiers based on Game Difficulty (Default and Apocalypse ignore this)
            if(!raidDifficulty.isDefault() && !raidDifficulty.is(RaidDifficulty.APOCALYPSE) && baseSpawnCount != 0 && !raiderType.equals(Raid.RaiderType.RAVAGER))
            {
                switch(worldDifficulty)
                {
                    case EASY -> baseSpawnCount = this.random.nextInt(baseSpawnCount - 2, baseSpawnCount);
                    case HARD -> baseSpawnCount = this.random.nextInt(baseSpawnCount, baseSpawnCount + 2 + 1);
                }

                if(baseSpawnCount < 0) baseSpawnCount = 0;
            }

            //Modifiers based on Player Count
            baseSpawnCount *= 1 + raidDifficulty.config().playerCountSpawnModifier();

            RaidMixin.outputLog(
                    "Default Spawns: Raider Type {%s}, Spawns per Wave {%s}, Selected Spawn Count {%s}, Difficulty {World: %s, Raid: %s}"
                            .formatted(raiderType.toString(), Arrays.toString(spawnsPerWave), baseSpawnCount, worldDifficulty, raidDifficulty)
            );

            callbackInfoReturnable.setReturnValue(baseSpawnCount);
        }
    }

    @Inject(at = @At("HEAD"), method = "getPotentialBonusSpawns", cancellable = true)
    private void difficultraids_getPotentialBonusSpawns(Raid.RaiderType raiderType, Random random, int groupsSpawned, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        if(!RaidDifficulty.current().isDefault()) callbackInfoReturnable.setReturnValue(0);
    }

    @Inject(at = @At("HEAD"), method = "stop")
    public void difficultraids_grantRewards(CallbackInfo callbackInfo)
    {
        RaidDifficulty raidDifficulty = RaidDifficulty.current();

        if(this.isVictory() && !raidDifficulty.isDefault())
        {
            List<ItemStack> rewards = RaidLoot.generate(raidDifficulty, this.level.getDifficulty());

            //TODO: Temporary
            if(raidDifficulty.is(RaidDifficulty.APOCALYPSE))
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

            RaidMixin.outputLog("Raid Rewards Generated at X: %s Y: %s Z: %s, Rewards List: %s".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ(), rewards.stream().map(stack -> stack.getItem().getRegistryName() + " (x" + stack.getCount() + ")")));

            this.heroesOfTheVillage.stream().map(uuid -> this.level.getPlayerByUUID(uuid)).filter(Objects::nonNull).forEach(p -> {
                p.sendMessage(
                        new TextComponent("Raid Rewards have spawned at X: %s Y: %s Z: %s!".formatted(rewardPos.getX(), rewardPos.getY(), rewardPos.getZ())),
                        p.getUUID()
                );
            });
        }
        else if(this.isLoss() && raidDifficulty.is(RaidDifficulty.APOCALYPSE))
        {
            WitherBoss wither = EntityType.WITHER.create(this.level);

            wither.setCustomName(new TextComponent("The Apocalypse"));
            wither.setPos(this.center.getX(), this.center.getY() + 10, this.center.getZ());

            RaidMixin.outputLog("Wither Boss spawned at X: %s Y: %s Z: %s!".formatted(this.center.getX(), this.center.getY() + 10, this.center.getZ()));

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
        RaidDifficulty raidDifficulty = RaidDifficulty.current();

        if(raidDifficulty.isDefault())
        {
            //Vanilla Defaults
            return switch(p_37725_) {
                case EASY -> 3;
                case NORMAL -> 5;
                case HARD -> 7;
                case PEACEFUL -> 0;
            };
        }

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
            case EASY -> -1;
            case NORMAL -> 0;
            case HARD -> +1;
        };

        return waves;
    }
}
