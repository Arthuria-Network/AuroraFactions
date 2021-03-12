package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.HashSet;
import java.util.Set;

public class GuiListener implements Listener {
    private final Set<ChestGui> activateGuiInstances = new HashSet<>();

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event){
        ChestGui gui = ChestGui.getGui(event.getInventory());

        if(gui == null){
            return;
        }
        //Logging.info(event.getWhoClicked().getName() +": clicked : "+gui.canClick);
        if(!gui.canClick){
            event.setCancelled(true);
        }
        InventoryView view = event.getView();
        Inventory inventory = view.getInventory(event.getRawSlot());
        if(inventory == null){
            //Clicked outside both inventories
            return;
        }
        gui.callOnClick(event);
    }
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event){
        ChestGui gui = ChestGui.getGui(event.getInventory());
        if(gui == null){
            return;
        }
        if(gui.isUpdating()){
            gui.callOnClose(event);
            if(gui.getviewerCount() == 1){
                activateGuiInstances.remove(gui);
            }
        }else{
            //Logging.info("Force Closing of GUI");
            Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                HumanEntity humanEntity = event.getPlayer();
                humanEntity.closeInventory();
            });
        }
        gui.callOnClose(event);
    }
}
