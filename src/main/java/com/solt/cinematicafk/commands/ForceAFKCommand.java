package com.solt.cinematicafk.commands;

import com.solt.cinematicafk.CineIdle;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceAFKCommand implements BasicCommand {
    private final CineIdle plugin;

    public ForceAFKCommand(CineIdle plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String[] args) {
        if (!stack.getSender().hasPermission("cinematicafk.admin")) {
            stack.getSender().sendMessage("§cYou do not have permission to use this command.");
            return;
        }

        if (args.length == 0) {
            stack.getSender().sendMessage("§cUsage: /forceafk <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            stack.getSender().sendMessage("§cPlayer not found.");
            return;
        }

        boolean isAfk = plugin.getAfkManager().isAFK(target);
        plugin.getAfkManager().setAFK(target, !isAfk);
        stack.getSender().sendMessage("§aToggled cinematic AFK for " + target.getName());
    }
}
