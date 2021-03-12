package me.tropicalshadow.aurorafactions.utils;

import org.bukkit.ChatColor;

public enum FactionColours {
    NON("non",ChatColor.WHITE,"non"),
    RED("Red",ChatColor.RED,"redfaction"),
    BLUE("Blue", ChatColor.BLUE,"bluefaction"),
    GREEN("Green",ChatColor.GREEN,"greenfaction"),
    YELLOW("Yellow",ChatColor.YELLOW,"yellowfaction");

    public String formatedName;
    public String channelName;
    public ChatColor colour;

    FactionColours(String Name, ChatColor textColour,String ChannelName){
        this.channelName = ChannelName;
        this.formatedName = Name;
        this.colour = textColour;

    }
    public static FactionColours getFromName(String name){
        FactionColours result = NON;
        for (FactionColours colour : FactionColours.values()) {
            if(colour.formatedName.equalsIgnoreCase(name))result=colour;
        }
        return result;
    }
    public static FactionColours getFromChannelName(String channelName){
        FactionColours result = NON;
        for (FactionColours colour : FactionColours.values()) {
            if(colour.channelName.equalsIgnoreCase(channelName))result=colour;
        }
        return result;
    }
}
