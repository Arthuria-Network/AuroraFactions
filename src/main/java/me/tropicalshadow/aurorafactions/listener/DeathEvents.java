package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.object.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathEvents implements Listener {

    private final AuroraFactions plugin;

    public DeathEvents(){
        this.plugin = AuroraFactions.getPlugin();
    }

    @EventHandler()
    public void onDeath(PlayerDeathEvent event){
        if(AuroraFactions.getGameState().equals(GameState.BEFORE)){
            event.setKeepInventory(true);
            event.setKeepLevel(true);
            event.getDrops().clear();
            event.setDroppedExp(0);
        }else if(AuroraFactions.getGameState().equals(GameState.WAR)){
            //
        }
    }
}
