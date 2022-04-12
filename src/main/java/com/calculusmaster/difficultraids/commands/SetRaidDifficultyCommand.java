package com.calculusmaster.difficultraids.commands;

import com.calculusmaster.difficultraids.raids.RaidDifficulty;
import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;

public class SetRaidDifficultyCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("dr_raiddifficulty");

        //Get Raid Difficulty Command
        literalArgumentBuilder.executes(css -> {
            css.getSource().sendSuccess(new TextComponent("Raid Difficulty is currently set to " + RaidDifficulty.current().getFormattedName() + "!"), false);
            return 1;
        });

        //Change Raid Difficulty Command
        for(RaidDifficulty d : RaidDifficulty.values())
        {
            literalArgumentBuilder.then(Commands.literal(d.toString().toLowerCase()).requires(css -> {
                try
                {
                    return css.getPlayerOrException().hasPermissions(2);
                }
                catch (CommandSyntaxException e)
                {
                    e.printStackTrace();
                    return false;
                }
            }).executes(css -> {
                DifficultRaidsConfig.RAID_DIFFICULTY.set(d);

                css.getSource().sendSuccess(new TextComponent("Set Raid Difficulty to " + d.getFormattedName() + "!"), true);
                return 1;
            }));
        }

        dispatcher.register(literalArgumentBuilder);
    }
}
