package me.tropicalshadow.aurorafactions.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Logging {

    private static String PREFIX = ChatColor.GREEN.toString()+"[AF] ";
    public static void info(Object obj){
        Bukkit.getLogger().info(PREFIX+ChatColor.BLUE.toString()+obj);
    }
    public static void warning(Object obj){
        Bukkit.getLogger().warning(PREFIX+ChatColor.YELLOW.toString()+obj);
    }
    public static void danger(Object obj){
        Bukkit.getLogger().severe(PREFIX+ChatColor.RED.toString()+obj);
    }
}
