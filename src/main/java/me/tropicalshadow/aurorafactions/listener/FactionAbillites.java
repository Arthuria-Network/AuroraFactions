package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.utils.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class FactionAbillites implements Listener {

    private static AuroraFactions instance;
    private static Map<FactionColours,Integer> factionUnlocked = new HashMap<>();

    public FactionAbillites(){
        instance = AuroraFactions.getPlugin();
        factionUnlocked = FileUtils.getUnlocks();
    }

    private void saveFactionUnlocks(){
        FileUtils.saveUnlocks(factionUnlocked);
    }

    public static void sendUnlockScreen(Player player){
        FactionColours faction = PermissionUtils.getFactionColour(player);

        ChestGui gui = new ChestGui("Faction Skill Unlock",3);
        gui.canClick = false;
        for (int i=0; i<9*3;i++){
            if(i<=9 || i>=17) {
                gui.getInventory().setItem(i, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).name(" ").build());
            }else{
                int index = i-10;
                if(index <= factionUnlocked.get(faction)){
                    gui.getInventory().setItem(i,new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("Unlocked "+index).addLoreLine(factionUnlocked.get(faction)+ "").build());
                }else{
                    gui.getInventory().setItem(i,new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("To unlock "+index).addLoreLine(factionUnlocked.get(faction)+"").build());
                }
            }
        }
        gui.show(player);
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
