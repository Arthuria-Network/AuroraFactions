package me.tropicalshadow.aurorafactions.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionTags extends PlaceholderExpansion {
    @Override
    public boolean persist(){
        return true;
    }
    @Override
    public @NotNull String getIdentifier() {
        return "aurorafaction";
    }

    @Override
    public @NotNull String getAuthor() {
        return "TropicalShadow";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }
    @Override
    public String onRequest(OfflinePlayer player, @NotNull String identifier){
        if(player.getPlayer()==null)return null;
        // %example_factionname%
        if(identifier.equals("factionname")){
            return PermissionUtils.getFactionColour(player.getPlayer()).formatedName;
        }

        // %example_factionformatedname%
        if(identifier.equals("factionformatedname")){
            FactionColours colour = PermissionUtils.getFactionColour(player.getPlayer());
            return colour.colour.toString()+colour.formatedName;
        }
        if(identifier.equals("motd")){
            FactionColours colour = PermissionUtils.getFactionColour(player.getPlayer());
            if(colour.equals(FactionColours.NON)){
                return "Come join a faction to help defend you crystal";
            }
            return "Come help the "+colour.formatedName+" faction win against the rest";
        }
        if(identifier.startsWith("leader_")){
            if(identifier.endsWith("red")){
                return "";
            }else if(identifier.endsWith("blue")){
                return "";
            }else if(identifier.endsWith("green")){
                return "";
            }else if(identifier.endsWith("yellow")){
                return "Yellow";
            }else{
                return "Err";
            }
        }

        // We return null if an invalid placeholder (f.e. %example_placeholder3%)
        // was provided
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }
}
