package me.tropicalshadow.aurorafactions.utils;

import org.bukkit.ChatColor;

public class StringUtils {

    public static String Colourfull(String str){
        return ChatColor.translateAlternateColorCodes('&',str);
    }
    public static boolean isEmptyOrNull(String text) { return text == null || text.isEmpty(); }
}
