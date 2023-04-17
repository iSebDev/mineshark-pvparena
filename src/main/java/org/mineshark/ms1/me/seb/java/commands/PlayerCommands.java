package org.mineshark.ms1.me.seb.java.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mineshark.ms1.me.seb.java.MineShark1;

public class PlayerCommands implements CommandExecutor {
    private static MineShark1 plugin;

    public PlayerCommands(MineShark1 instance) {
        plugin = instance;

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(command.getName().equals("join") && args.length >= 1) {
            plugin.handler.joinPlayer(player, args[0]);
        }else if(command.getName().equals("leave")) {
            plugin.handler.leavePlayer(player);

            plugin.handler.teleportSpawn(player);
        }

        return false;
    }
}
