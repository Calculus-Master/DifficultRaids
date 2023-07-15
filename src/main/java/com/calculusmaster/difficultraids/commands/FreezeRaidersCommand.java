package com.calculusmaster.difficultraids.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class FreezeRaidersCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands
                .literal("difficultraids_freezeraiders")
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

                    if(raid == null) css.getSource().sendFailure(Component.literal("You must be near a Raid to use this command!"));
                    else
                    {
                        List<Raider> alive = raid.getAllRaiders().stream().filter(LivingEntity::isAlive).toList();
                        alive.forEach(r ->
                        {
                            if(r.isNoAi()) r.setNoAi(false);
                            r.lookAt(css.getSource().getAnchor(), new Vec3(player.getX() - r.getX(), player.getY() - r.getY(), player.getZ() - r.getZ()));
                            r.setNoAi(true);
                        });
                        css.getSource().sendSuccess(Component.literal(alive.size() + " raiders frozen!"), true);
                    }

                    return 1;
                });

        dispatcher.register(literalArgumentBuilder);
    }
}
