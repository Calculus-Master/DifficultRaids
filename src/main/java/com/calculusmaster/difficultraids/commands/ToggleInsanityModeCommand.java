package com.calculusmaster.difficultraids.commands;

import com.calculusmaster.difficultraids.setup.DifficultRaidsConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.raid.Raid;

public class ToggleInsanityModeCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands
                .literal("difficultraids_insanitymode")
                .requires(css -> {
                        try { return css.getPlayerOrException().hasPermissions(2); }
                        catch (CommandSyntaxException e)
                        {
                            e.printStackTrace();
                            return false;
                        }
                })
                .executes(css -> {
                    ServerPlayer player = css.getSource().getPlayerOrException();
                    ServerLevel level = player.getLevel();
                    Raid raid = level.getRaidAt(player.blockPosition());

                    boolean current = DifficultRaidsConfig.INSANITY_MODE.get();
                    DifficultRaidsConfig.INSANITY_MODE.set(!current);
                    boolean after = DifficultRaidsConfig.INSANITY_MODE.get();

                    MutableComponent result = Component.literal(after ? "Insanity Mode is now activated. What have you done?" : "Insanity Mode is now deactivated. Good.").withStyle(ChatFormatting.DARK_RED);

                    if(raid != null) result.append(Component.literal(" (This change will apply on the next wave of the current Raid.)"));

                    css.getSource().sendSuccess(result, true);
                    return 1;
        });

        dispatcher.register(literalArgumentBuilder);
    }
}
