package com.calculusmaster.difficultraids.commands;

import com.calculusmaster.difficultraids.raids.RaidEnemyRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DumpRaidWavesCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands
                .literal("difficultraids_dumpwaves")
                .requires(css -> {
                        try { return css.getPlayerOrException().hasPermissions(2); }
                        catch (CommandSyntaxException e)
                        {
                            e.printStackTrace();
                            return false;
                        }
                })
                .executes(css -> {

                    RaidEnemyRegistry.printWaveData(LogUtils.getLogger());

                    css.getSource().sendSuccess(Component.literal("Dumped Raid Wave Data into the console!"), true);
                    return 1;
        });

        dispatcher.register(literalArgumentBuilder);
    }
}
