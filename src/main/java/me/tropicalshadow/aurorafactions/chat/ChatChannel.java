package me.tropicalshadow.aurorafactions.chat;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatChannel implements Listener {

    private static AuroraFactions instance;
    public ChatChannel(){
        instance = AuroraFactions.getPlugin();
    }


    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event){
        Player p = event.getPlayer();
        if(instance.getChannelManger().getCurrentTextChannel(p)==null)return;
        event.setCancelled(true);
        instance.getServer().getScheduler().runTask(instance, () ->{
                instance.getChannelManger().submitMessageFromGame(event.getPlayer(), event.getMessage());
            }
        );
    }
}
