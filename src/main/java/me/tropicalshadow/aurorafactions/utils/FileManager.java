package me.tropicalshadow.aurorafactions.utils;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {

    private static YamlConfiguration config;
    private static File file;
    private AuroraFactions plugin;


    public FileManager(){
        plugin = AuroraFactions.getPlugin();
        plugin.saveDefaultConfig();
        config = (YamlConfiguration) plugin.getConfig();
        file = new File(plugin.getDataFolder(),"config.yml");
    }

    public void reloadConfig(){
        plugin.reloadConfig();
    }

    public void saveAll(){
        plugin.saveConfig();
    }
}
