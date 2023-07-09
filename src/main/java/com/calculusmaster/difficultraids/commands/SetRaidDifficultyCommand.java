package com.calculusmaster.difficultraids.commands;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.raid.Raid;

public class SetRaidDifficultyCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("difficultraids_raiddifficulty");

        // dr_raiddifficulty get
        literalArgumentBuilder.then(Commands.literal("get").executes(css -> {
            ServerPlayer player = css.getSource().getPlayerOrException();
            Raid raid = player.getLevel().getRaidAt(player.blockPosition());

            if(raid == null)
            {
                if(!player.hasEffect(MobEffects.BAD_OMEN))
                    css.getSource().sendFailure(Component.literal("You must be in a Raid or have some level of Bad Omen to check the Raid Difficulty."));
                else
                {
                    int level = player.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;

                    css.getSource().sendSuccess(Component.literal("Entering a Village will initiate a " + RaidDifficulty.get(level).getFormattedName() + " Raid with your current Bad Omen level."), false);
                }
            }
            else css.getSource().sendSuccess(Component.literal("You're currently in a " + RaidDifficulty.get(raid.getBadOmenLevel()).getFormattedName() + " Raid."), false);

            return 1;
        }));

        // dr_raiddifficulty set <difficulty>
        for(RaidDifficulty difficulty : RaidDifficulty.values())
        {
            literalArgumentBuilder.then(Commands.literal("set").then(Commands.literal(difficulty.toString().toLowerCase()).requires(css -> {
                try { return css.getPlayerOrException().hasPermissions(2); }
                catch(CommandSyntaxException e) { e.printStackTrace(); return false; }
            }).executes(css -> {
                int level = switch(difficulty) {
                    case DEFAULT -> 1;
                    case HERO -> 2;
                    case LEGEND -> 3;
                    case MASTER -> 4;
                    case GRANDMASTER -> 5;
                };

                css.getSource().getPlayerOrException().addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 100000, level - 1));

                css.getSource().sendSuccess(Component.literal("Entering a Village will initiate a " + difficulty.getFormattedName() + " Raid."), false);
                return 1;
            })));
        }

        // dr_raiddifficulty info
        literalArgumentBuilder.then(Commands.literal("info").executes(css -> {
            css.getSource().sendSuccess(Component.literal("To select a Raid Difficulty, obtain higher levels of Bad Omen. Level 1 spawns a Default Vanilla Raid, and higher levels spawn tougher Raids added by DifficultRaids (2: Hero, 3: Legend, 4: Master, 5: Grandmaster)."), false);
            return 1;
        }));

        dispatcher.register(literalArgumentBuilder);
    }
}
