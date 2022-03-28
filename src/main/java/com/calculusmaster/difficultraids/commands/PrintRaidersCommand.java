package com.calculusmaster.difficultraids.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class PrintRaidersCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands
                .literal("dr_printraiders")
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

                            Map<EntityType<?>, Integer> raiderTypeCounts = new HashMap<>();
                            alive.forEach(r -> raiderTypeCounts.put(r.getType(), raiderTypeCounts.getOrDefault(r.getType(), 0) + 1));

                            StringJoiner s = new StringJoiner("\n");
                            raiderTypeCounts.forEach((key, value) -> s.add(key.toShortString() + ": " + value));

                            String raiderList = "Raiders Currently Alive:\n" + s;
                            String totalRaiders = "Total Raiders Alive: " + raiderTypeCounts.values().stream().mapToInt(i -> i).sum();
                            css.getSource().sendSuccess(new TextComponent(raiderList + "\n" + totalRaiders), true);
                        }

                        return 1;
        });

        dispatcher.register(literalArgumentBuilder);
    }
}
