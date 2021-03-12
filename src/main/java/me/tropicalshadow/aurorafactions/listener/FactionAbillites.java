package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FactionAbillites implements Listener {

    private static AuroraFactions instance;

    public FactionAbillites(){
        instance = AuroraFactions.getPlugin();
    }

    @EventHandler()
    public void onDamage(EntityDamageEvent event){
        if(!(event.getEntity() instanceof Player))return;
        FactionColours Colour = PermissionUtils.getFactionColour((Player)event.getEntity());
        if(Colour.equals(FactionColours.RED) && (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || event.getCause().equals(EntityDamageEvent.DamageCause.FIRE) || event.getCause().equals(EntityDamageEvent.DamageCause.HOT_FLOOR))){
            event.setCancelled(true);
        }else if(Colour.equals(FactionColours.BLUE) && event.getCause().equals(EntityDamageEvent.DamageCause.DROWNING)){
            event.setCancelled(true);
        }else if(Colour.equals(FactionColours.GREEN) && event.getCause().equals(EntityDamageEvent.DamageCause.FALL)){
            event.setCancelled(true);
        }else if(Colour.equals(FactionColours.YELLOW) && (event.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) || event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))){
            event.setCancelled(true);
        }
    }

}
