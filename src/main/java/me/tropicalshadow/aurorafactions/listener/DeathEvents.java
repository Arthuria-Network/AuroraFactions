package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.object.GameState;
import me.tropicalshadow.aurorafactions.utils.Logging;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvents implements Listener {

    private final AuroraFactions plugin;

    public DeathEvents(){
        this.plugin = AuroraFactions.getPlugin();
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event){
        if(AuroraFactions.getGameState().equals(GameState.WAR)){
            event.setKeepInventory(false);
            event.setKeepLevel(false);
        }else{
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();
            event.setDroppedExp(0);
            if(AuroraFactions.getGameState().equals(GameState.BEFORE)){
                event.getEntity().sendMessage( ChatColor.GREEN+"Your items were saved as the great war has not started yet.");
            }else if(AuroraFactions.getGameState().equals(GameState.WINNER)){
                event.getEntity().sendMessage(ChatColor.GREEN+"This season is over, your items will remain in your inventory.");
            }
        }
    }
}
