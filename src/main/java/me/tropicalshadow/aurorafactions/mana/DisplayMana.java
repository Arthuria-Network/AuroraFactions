package me.tropicalshadow.aurorafactions.mana;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.NMS.Title;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

public class DisplayMana implements Listener {

    Title title;

    public DisplayMana(){
        AuroraFactions plugin = AuroraFactions.getPlugin();
        title = plugin.getTitle();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(player ->{
                Mana mana = Mana.getPlayer(player);
                sendManaTitleToPlayer(player,mana.getUse(),mana.getMax());
            });
        },(long) 20*10,(long) 20*2);
    }


    public void sendManaTitleToPlayer(Player player, int amount, int max){
        title.sendActionMessage(player,"Mana "+amount+"/"+max);
    }

    @EventHandler()
    public void onTestCase(PlayerEggThrowEvent event){
        return;
        //sendManaTitleToPlayer(event.getPlayer(),50,100);
    }
    @EventHandler()
    public void onLeave(PlayerQuitEvent event){
        Mana.removePlayer(event.getPlayer());
    }
    @EventHandler()
    public void onKick(PlayerKickEvent event){
        Mana.removePlayer(event.getPlayer());
    }
}
