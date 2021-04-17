package me.tropicalshadow.aurorafactions.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.tropicalshadow.aurorafactions.AuroraFactions;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatChannel implements Listener {

    private static AuroraFactions instance;
    public ChatChannel(){
        instance = AuroraFactions.getPlugin();
    }


    @EventHandler
    public void onPlayerChat(AsyncChatEvent event){
        Player p = event.getPlayer();
        if(instance.getChannelManger().getCurrentTextChannel(p)==null)return;
        event.setCancelled(true);
        instance.getServer().getScheduler().runTask(instance, () -> instance.getChannelManger().submitMessageFromGame(event.getPlayer(), PlainComponentSerializer.plain().serialize(event.message())));
    }
}
