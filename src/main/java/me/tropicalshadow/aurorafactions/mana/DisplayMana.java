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

    public static String manaMessageFormat = "Mana {use}/{max}";
    Title title;
    int incrementalIncrease = 5;

    public Runnable getCalculator(Mana parentMana){
        return new Runnable() {
            Mana mana = parentMana;
            @Override
            public void run() {
                if(mana.getUse()<mana.getMax())mana.changeUse(incrementalIncrease);
            }
        };
    }

    public DisplayMana(){
        AuroraFactions plugin = AuroraFactions.getPlugin();
        title = plugin.getTitle();
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.getServer().getOnlinePlayers().forEach(player ->{
                Mana mana = Mana.getPlayer(player);
                plugin.getServer().getScheduler().runTask(plugin,getCalculator(mana));
                sendManaTitleToPlayer(player,mana.getUse(),mana.getMax());
            });
        },(long) 20*10,(long) 20*2);

    }


    public static void sendManaTitleToPlayer(Player player, int amount, int max){
        AuroraFactions.getPlugin().getTitle().sendActionMessage(player,manaMessageFormat.replace("{use}",String.valueOf(amount)).replace("{max}",String.valueOf(max)));
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
