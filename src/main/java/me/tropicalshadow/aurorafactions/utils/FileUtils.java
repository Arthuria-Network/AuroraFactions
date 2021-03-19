package me.tropicalshadow.aurorafactions.utils;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.mana.Mana;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtils {


    public static Map<UUID,Mana> getOnlinePlayers() {
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"playerdata.yml");
        try {
            if (!file.exists()){
                file.createNewFile();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.createSection("players");
                yaml.save(file);

            }
        }catch (Exception e){}
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection conf = yaml.getConfigurationSection("players");
        List<UUID> ids = AuroraFactions.getPlugin().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList());
        Map<UUID,Mana> output = new HashMap<>();
        assert conf != null;
        Set<String> keys = conf.getKeys(false);
        ids.forEach(id->{
            if(keys.contains(id.toString())){
                output.put(id,(Mana)conf.get(id.toString()));
            }else{
                Mana mana = new Mana(id,100,100);
                output.put(id,mana);
                addPlayerToFile(mana);
            }
        });
        return output;
    }

    public static void saveUnlocks(Map<FactionColours,Integer> vals){
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"factions.yml");
        try{
            if(!file.exists()){
                AuroraFactions.getPlugin().saveResource("factions.yml",true);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (FactionColours value : vals.keySet()) {
                if(value!=FactionColours.NON){
                    yaml.set(value.name().toLowerCase(),vals.get(value));
                }
            }
            yaml.save(file);
        }catch(Exception e){}


    }

    public static Map<FactionColours,Integer> getUnlocks(){
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"factions.yml");
        try{
            if(!file.exists()){
                AuroraFactions.getPlugin().saveResource("factions.yml",true);
            }
        }catch(Exception e){}
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        Map<FactionColours, Integer> output = new HashMap<>();
        for (FactionColours value : FactionColours.values()) {
            if(value!=FactionColours.NON){
                output.put(value,yaml.getInt(value.name().toLowerCase(),0));
            }
        }
        return output;
    }

    public static Mana getManaForPlayer(Player player) {
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"playerdata.yml");
        try {
            if (!file.exists()){
                file.createNewFile();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.createSection("players");
                yaml.save(file);
            }
        }catch (Exception e){}
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection conf = yaml.getConfigurationSection("players");
        Mana mana;
        assert conf != null;
        if(conf.contains(player.getUniqueId().toString())) {
            mana = (Mana) conf.get(player.getUniqueId().toString());
        }else{
            mana = new Mana(player,100,100);
            addPlayerToFile(mana);
        }
        return mana;
    }

    public static void addPlayerToFile(Player player){
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"playerdata.yml");
        try {
            if (!file.exists()){
                file.createNewFile();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.createSection("players");
                yaml.save(file);

            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection conf = yaml.getConfigurationSection("players");
            assert conf != null;
            if(conf.getKeys(false).contains(player.getUniqueId().toString()))return;
            yaml.set("players."+player.getUniqueId().toString(),new Mana(player,100,100));
            yaml.save(file);
        }catch (Exception e){}

    }
    public static void addPlayerToFile(Mana mana){
        if(!AuroraFactions.getPlugin().getDataFolder().exists())AuroraFactions.getPlugin().getDataFolder().mkdir();
        File file = new File(AuroraFactions.getPlugin().getDataFolder(),"playerdata.yml");
        try {
            if (!file.exists()){
                file.createNewFile();
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.createSection("players");
                yaml.save(file);

            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection conf = yaml.getConfigurationSection("players");
            assert conf != null;
            if(conf.getKeys(false).contains(mana.playerID.toString()))return;
            yaml.set("players."+mana.playerID.toString(),new Mana(mana.playerID,100,100));
            yaml.save(file);
        }catch (Exception e){}

    }
}
