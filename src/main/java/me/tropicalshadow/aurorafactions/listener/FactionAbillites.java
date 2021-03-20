package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.utils.*;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

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

    public ItemStack IterateFactionWand(ItemStack item){
        if(!item.getItemMeta().getPersistentDataContainer().has(Claims.adminWandKey, PersistentDataType.INTEGER))return null;
        int IDkey = item.getItemMeta().getPersistentDataContainer().get(Claims.adminWandKey,PersistentDataType.INTEGER);
        String FactionKey = item.getItemMeta().getPersistentDataContainer().get(Claims.factionWandKey,PersistentDataType.STRING);
        Claims.AdminWandModes mode = Claims.AdminWandModes.fromID(IDkey+1);
        if(mode == null)mode = Claims.AdminWandModes.REMOVE;
        return Claims.adminWand(mode,FactionColours.getFromName(FactionKey));
    }

    @EventHandler()
    public void claimingWand(PlayerInteractEvent event){
        if(event.getItem() == null)return;
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if(!item.getItemMeta().getPersistentDataContainer().has(Claims.adminWandKey, PersistentDataType.INTEGER))return;
        int value = item.getItemMeta().getPersistentDataContainer().get(Claims.adminWandKey,PersistentDataType.INTEGER);
        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            ItemStack newItem = IterateFactionWand(item);
            event.getPlayer().getInventory().setItemInMainHand(newItem);
            int id = newItem.getItemMeta().getPersistentDataContainer().get(Claims.adminWandKey,PersistentDataType.INTEGER);
            event.getPlayer().sendMessage(Component.text("Admin Wand changed mode to "+Claims.AdminWandModes.fromID(id).name().toLowerCase()));
                    
            return;
        }
        if(!event.getAction().equals(Action.LEFT_CLICK_AIR)  && !event.getAction().equals(Action.LEFT_CLICK_BLOCK))return;
        Claims.AdminWandModes mode = Claims.AdminWandModes.fromID(value);
        if (mode == Claims.AdminWandModes.REMOVE) {
            AuroraFactions.getPlugin().getClaims().removeClaimsFromChunk(event.getPlayer().getChunk());
            event.getPlayer().sendMessage(Component.text("All claims removed from chunk"));
        }else if(mode == Claims.AdminWandModes.SET){
            AuroraFactions.getPlugin().getClaims().removeClaimsFromChunk(event.getPlayer().getChunk());
            FactionColours factionColours = FactionColours.getFromName(item.getItemMeta().getPersistentDataContainer().get(Claims.factionWandKey,PersistentDataType.STRING));
            AuroraFactions.getPlugin().getClaims().addClaim(new Claims.Claim(factionColours,event.getPlayer().getLocation().getChunk()));
            event.getPlayer().sendMessage(Component.text("Claimed this chunk"));

        }else if(mode == Claims.AdminWandModes.SELECT){
            ChestGui gui = new ChestGui("Admin Claim Wand Selector",1);
            gui.canClick = false;
            gui.getInventory().setItem(2,new ItemBuilder(Material.RED_CONCRETE).name(ChatColor.RED+"Red Faction").build());
            gui.getInventory().setItem(3,new ItemBuilder(Material.BLUE_CONCRETE).name(ChatColor.BLUE+"Blue Faction").build());

            gui.getInventory().setItem(5,new ItemBuilder(Material.GREEN_CONCRETE).name(ChatColor.GREEN+"Green Faction").build());
            gui.getInventory().setItem(6,new ItemBuilder(Material.YELLOW_CONCRETE).name(ChatColor.YELLOW+"Yellow Faction").build());

            gui.setOnClick((e)->{
                if(e.getClickedInventory() == e.getWhoClicked().getInventory())return;
                Material mat = e.getCurrentItem().getType();
                ItemStack mainHand = e.getWhoClicked().getInventory().getItemInMainHand();
                if(mat.equals(Material.RED_CONCRETE)){
                    mainHand = Claims.adminWand(Claims.AdminWandModes.SELECT,FactionColours.RED);
                }else if (mat.equals(Material.BLUE_CONCRETE)){
                    mainHand = Claims.adminWand(Claims.AdminWandModes.SELECT,FactionColours.BLUE);
                }else if(mat.equals(Material.GREEN_CONCRETE)){
                    mainHand = Claims.adminWand(Claims.AdminWandModes.SELECT,FactionColours.GREEN);
                }else if(mat.equals(Material.YELLOW_CONCRETE)){
                    mainHand = Claims.adminWand(Claims.AdminWandModes.SELECT,FactionColours.YELLOW);
                }
                e.getWhoClicked().getInventory().setItemInMainHand(mainHand);
            });
            gui.show(event.getPlayer());
        }

    }

}
