package org.mineshark.ms1.me.seb.java.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mineshark.ms1.me.seb.java.MineShark1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Data {
    private final MineShark1 plugin;
    private File configFile = null;
    private FileConfiguration dataConfig = null;
    public Data(MineShark1 instance) {
        plugin = instance;
        saveDefaultConfig();
    }
    public void reloadConfig() {
        if (this.configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "data.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);
        InputStream defaultStream = this.plugin.getResource("data.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }
    public FileConfiguration getConfig() {
        if (this.dataConfig == null) reloadConfig();
        return this.dataConfig;
    }
    public void saveConfig(){
        if (this.dataConfig == null || this.configFile == null) return;
        try {
            this.getConfig().save(this.configFile);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public void saveDefaultConfig() {
        if(!this.plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if(this.configFile == null) this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
        if(!this.configFile.exists()) this.plugin.saveResource("config.yml", false);
    }
}

