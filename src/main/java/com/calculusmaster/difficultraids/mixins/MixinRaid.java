package com.calculusmaster.difficultraids.mixins;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.calculusmaster.difficultraids.util.BonusRaidSpawnPreset;
import com.calculusmaster.difficultraids.util.RaidDifficulty;
import com.calculusmaster.difficultraids.util.RaiderDefaultSpawns;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.raid.Raid;
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
    private BonusRaidSpawnPreset preset;

    @Shadow @Final private int numGroups;
    @Shadow @Final private ServerLevel level;
    @Shadow @Final private Random random;

    private static final Logger LOGGER = LogUtils.getLogger();

    private static void outputLog(String text)
    {
        LOGGER.info("Difficult Raids - Log Info - [[ " + text + " ]]");
    }

    @Inject(at = @At("HEAD"), method = "spawnGroup", cancellable = true)
    private void difficultraids_spawnGroup(BlockPos pos, CallbackInfo callbackInfo)
    {
        int bonusChance = switch(DifficultRaidsConfig.RAID_DIFFICULTY.get()) {
            case DEFAULT -> 10;
            case HERO -> 15;
            case LEGEND -> 25;
            case MASTER -> 50;
            case APOCALYPSE -> 100;
        };

        if(this.random.nextInt(100) < bonusChance)
        {
            this.preset = BonusRaidSpawnPreset.getRandom();

            //TODO: Only send to players within the raid boundaries
            Minecraft.getInstance().player.sendMessage(
                    new TextComponent("The " + this.preset.getChatName() + " has spawned!"),
                    Minecraft.getInstance().player.getUUID()
            );
        }
        else this.preset = null;
    }

    @Inject(at = @At("HEAD"), method = "getDefaultNumSpawns", cancellable = true)
    private void difficultraids_getDefaultNumSpawns(Raid.RaiderType raiderType, int groupsSpawned, boolean spawnBonusGroup, CallbackInfoReturnable<Integer> callbackInfoReturnable)
    {
        RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();
        Difficulty worldDifficulty = this.level.getDifficulty();

        //Spawns per wave array
        int[] spawnsPerWave = RaiderDefaultSpawns.getDefaultSpawns(raiderType, raidDifficulty);
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
        if(this.preset != null)
        {
            Difficulty worldDifficulty = difficultyInstance.getDifficulty();
            RaidDifficulty raidDifficulty = DifficultRaidsConfig.RAID_DIFFICULTY.get();

            int count = this.preset.getBonusSpawnCount(raiderType, worldDifficulty, raidDifficulty);

            MixinRaid.outputLog(
                    "Bonus Spawns: Raider Type {%s}, Spawn Count {%s}, Difficulty {World: %s, Raid: %s}"
                    .formatted(raiderType.toString(), count, worldDifficulty.toString(), raidDifficulty.toString())
            );

            callbackInfoReturnable.setReturnValue(count);
        }
        else MixinRaid.outputLog("BonusRaidSpawnPreset is null! Defaulting to vanilla Minecraft bonus spawn groups...");
    }
}
