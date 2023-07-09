package com.calculusmaster.difficultraids.config;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.ForgeConfigSpec;

public class RaidDifficultyConfig
{
    //General
    public ForgeConfigSpec.BooleanValue elitesEnabled;
    public ForgeConfigSpec.DoubleValue playerHealthBoostAmount;

    //Raider-Specific
    public RaiderConfigs.Vindicator vindicator;
    public RaiderConfigs.Evoker evoker;
    public RaiderConfigs.Pillager pillager;
    public RaiderConfigs.Ravager ravager;

    public RaiderConfigs.Warrior warrior;
    public RaiderConfigs.Dart dart;
    public RaiderConfigs.Conductor conductor;
    public RaiderConfigs.Necromancer necromancer;
    public RaiderConfigs.Shaman shaman;
    public RaiderConfigs.Tank tank;
    public RaiderConfigs.Assassin assassin;
    public RaiderConfigs.Frostmage frostmage;

    public RaiderConfigs.Nuaos nuaos;
    public RaiderConfigs.Xydrax xydrax;
    public RaiderConfigs.Modur modur;
    public RaiderConfigs.Voldon voldon;

    public RaiderConfigs.Hunter hunter;
    public RaiderConfigs.Archer archer;
    public RaiderConfigs.Skirmisher skirmisher;
    public RaiderConfigs.Legioner legioner;
    public RaiderConfigs.Executioner executioner;
    public RaiderConfigs.Mountaineer mountaineer;
    public RaiderConfigs.RoyalGuard royalguard;

    //Translate ForgeConfigSpec values into usable variables
    public void init()
    {
        LogUtils.getLogger().info("Difficult Raids: Baking Config Values!");

        this.vindicator.initialize();
        this.evoker.initialize();
        this.pillager.initialize();
        this.ravager.initialize();

        this.warrior.initialize();
        this.dart.initialize();
        this.conductor.initialize();
        this.necromancer.initialize();
        this.shaman.initialize();
        this.tank.initialize();
        this.assassin.initialize();
        this.frostmage.initialize();

        this.nuaos.initialize();
        this.xydrax.initialize();
        this.modur.initialize();
        this.voldon.initialize();

        this.hunter.initialize();
        this.archer.initialize();
        this.skirmisher.initialize();
        this.legioner.initialize();
        this.executioner.initialize();
        this.mountaineer.initialize();
        this.royalguard.initialize();
    }
}
