package org.mineshark.ms1.me.seb.java.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mineshark.ms1.me.seb.java.MineShark1;

public class AdminCommand implements CommandExecutor {
    private static MineShark1 plugin;

    public AdminCommand(MineShark1 instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        if(args.length >= 2) {
            if(args[0].equalsIgnoreCase("create")) {
                plugin.handler.createArena(player, args[1]);
            }else if(args[0].equalsIgnoreCase("setup")) {
                if(plugin.handler.inSetup.containsKey(player.getUniqueId())) {
                    plugin.handler.setupLeave(player);
                    return false;
                }
                plugin.handler.setupArena(player, args[1]);
            }else if(args[0].equalsIgnoreCase("delete")) {
                plugin.handler.removeArena(player, args[1]);
            }else {
                player.sendMessage(plugin.format("&e[Mineshark] &f/mineshark1 <setup, create, delete> <arena>"));
            }
        }else if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.updateFiles();
            player.sendMessage(plugin.format(
                    "&e[Mineshark] &aEl plugin ha sido recargado correctamente!"));
        }else if(args.length == 1 && args[0].equalsIgnoreCase("setspawn")) {
            plugin.handler.setSpawn(player);
        }else {
            player.sendMessage(plugin.format("&e[Mineshark] &f/minesharkpvp <setup, create, delete> <arena>"));
            player.sendMessage(plugin.format("&e[Mineshark] &f/minesharkpvp <reload, setspawn>"));
        }

        return false;
    }
}
