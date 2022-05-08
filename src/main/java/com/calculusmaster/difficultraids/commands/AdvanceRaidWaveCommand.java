package com.calculusmaster.difficultraids.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

import java.util.List;

public class AdvanceRaidWaveCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands
                .literal("dr_nextwave")
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

                        if(raid == null) css.getSource().sendFailure(new TextComponent("You must be near a Raid to use this command!"));
                        else
                        {
                            List<Raider> alive = raid.getAllRaiders().stream().filter(LivingEntity::isAlive).toList();

                            alive.forEach(r -> r.hurt(DamageSource.STARVE, r.getHealth() + 1.0F));
                            css.getSource().sendSuccess(new TextComponent("Wave successfully cleared!"), true);
                        }

                        return 1;
        });

        dispatcher.register(literalArgumentBuilder);
    }
}
