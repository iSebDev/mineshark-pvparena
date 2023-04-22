package org.mineshark.ms1.me.seb.java;

import org.jetbrains.annotations.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.mineshark.ms1.me.seb.java.manager.ChestManager;
import org.mineshark.ms1.me.seb.java.manager.Configuration;
import org.mineshark.ms1.me.seb.java.manager.Data;
import org.mineshark.ms1.me.seb.java.manager.GameHandler;
public final class MineShark1 extends JavaPlugin {

    public MineShark1 getInstance() {
        return this;
    }

    public GameHandler handler;

    public ChestManager manaChest;

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

    private void initConfig() throws Exception {
        configuration = new Configuration(this);
        console().sendMessage(format("&e[MineShark] &aSuccess load of Configuration"));
    }

    private void initData() throws Exception {
        data = new Data(this);
        console().sendMessage(format("&e[MineShark] &aSuccess load of Data"));
    }

    private void initGameHandler() throws Exception {
        handler = new GameHandler(this);
        manaChest = new manaChest(this);
        console().sendMessage(format("&e[MineShark] &aSuccess load of GameHandler"));
        if(configuration.getConfig().getString("spawn.x") != null) {
            console().sendMessage(format("&e[MineShark] &aSpawn location and %a games loaded!".
                    replace("%a", String.valueOf(getData().getKeys(false).size()))));
        }else {
            console().sendMessage(format("&e[MineShark] &aNo spawn and  %agames loaded!".
                    replace("%a", String.valueOf(getData().getKeys(false).size()))));
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            initConfig();
            initData();
            initGameHandler();
        } catch (Exception e) {
            console().sendMessage(format("&e[MineShark] &cPlugin disabled due to an error during startup."));
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
