package org.mineshark.ms1.me.seb.java;

import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineshark.ms1.me.seb.java.manager.Configuration;
import org.mineshark.ms1.me.seb.java.manager.Data;
import org.mineshark.ms1.me.seb.java.manager.GameHandler;
public final class MineShark1 extends JavaPlugin {

    public MineShark1 getInstance() {
        return this;
    }

    public GameHandler handler;

    public Configuration configuration;
    private Data data;

    public String format(String string) {
        return (
                ChatColor.translateAlternateColorCodes('&', string)
        );
    }

    public final ConsoleCommandSender console() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        super.onEnable();

        try {
            configuration = new Configuration(this);
            console().sendMessage(format("&e[MineShark] &aSuccess load of Configuration"));
        } catch (Exception e) {
            console().sendMessage(format("&e[MineShark] &cError when try to load Configuration."));
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        try {
            data = new Data(this);
            console().sendMessage(format("&e[MineShark] &aSuccess load of Data"));
        } catch (Exception e) {
            console().sendMessage(format("&e[MineShark] &cError when try to load Data."));
            e.printStackTrace();
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }

        try {
            handler = new GameHandler(this);
            console().sendMessage(format("&e[MineShark] &aSuccess load of GameHandler"));
        } catch (Exception e) {
            console().sendMessage(format("&e[MineShark] &cError when try to load GameHandler, enable debug in the config for more info!"));
            if (configuration.getConfig().getBoolean("debug")) {
                e.printStackTrace();
            }
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        super.onDisable();
    }

    @NotNull
    public final ConfigurationSection getData() {
        return this.data.getConfig().getConfigurationSection("data");
    }

    public void updateFiles() {
        configuration.saveConfig();
        configuration.reloadConfig();

        data.saveConfig();
        data.reloadConfig();

        handler.arenas.clear();
        handler.locations.clear();

        for(String id : getData().getKeys(false)) {
            handler.locations.put(id, getData().getStringList(id+".locations"));
        }
    }
}
