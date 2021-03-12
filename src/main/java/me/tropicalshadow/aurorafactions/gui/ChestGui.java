package me.tropicalshadow.aurorafactions.gui;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.listener.GuiListener;
import me.tropicalshadow.aurorafactions.utils.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

public class ChestGui implements InventoryHolder {

    protected Inventory inv;
    private static boolean hasRegisteredListeners;
    protected Consumer<InventoryClickEvent> onClick;

    protected Consumer<InventoryCloseEvent> onClose;

    boolean updating = false;
    public boolean canClick = true;
    private int rows;
    private static final Map<Inventory,ChestGui> GUI_INVETORIES = new WeakHashMap<>();

    public int getviewerCount(){
        return getInventory().getViewers().size();
    }
    public List<HumanEntity> getViewers(){
        return new ArrayList<>(getInventory().getViewers());
    }
    public void update(){
        updating = true;
        getViewers().forEach(this::show);
        if(!updating)
            throw new AssertionError("Gui#isUpdating became false before Gui#update finished");
        updating = false;

    }
    public int getRows(){
        if(inv == null)
            return this.rows;
        return ((int)inv.getSize()/9);
    }

    public ChestGui(String str,int rows){
        if(!(rows>=1&&rows<=6)){
            throw new IllegalArgumentException("Rows should be between 1 and 6");
        }
        this.rows = rows;
        this.title = str;
        if(!hasRegisteredListeners){
            Bukkit.getPluginManager().registerEvents(new GuiListener(),
                    AuroraFactions.getPlugin());
            hasRegisteredListeners = true;
        }
    }
    protected void addInvetory(Inventory inventory,ChestGui gui){
        GUI_INVETORIES.put(inventory,gui);
    }
    public static ChestGui getGui(Inventory inventory){
        return GUI_INVETORIES.get(inventory);
    }
    public void setOnClick(Consumer<InventoryClickEvent> onClick){
        this.onClick = onClick;
    }
    public void callOnClick(InventoryClickEvent event){
        callCallback(onClick,event,"onClick");
    }
    public void setOnClose(Consumer<InventoryCloseEvent> onClose){
        this.onClose = onClose;
    }
    public void callOnClose(InventoryCloseEvent event){
        callCallback(onClose,event,"onClose");
    }
    private <T extends InventoryEvent> void callCallback(Consumer<T> callback, T event, String callbackName){
        if(callback == null)return;
        try{
            callback.accept(event);
        }catch(Throwable t){
            String message = "Exception while handling "+ callbackName;
            if(event instanceof InventoryClickEvent){
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;
                message += ", slot=" + clickEvent.getSlot();
            }
            message += t.getMessage();
            Logging.danger(message);
        }
    }

    public boolean isUpdating(){
        return updating;
    }
    public void show(HumanEntity humanEntity){
        //getInventory().clear();

        humanEntity.openInventory(getInventory());
    }
    public Inventory getInventory(){
        if(this.inv == null){
            this.inv = createInventory();
        }
        return inv;
    }
    private String title;
    public Inventory createInventory(String title) {

        Inventory inv =  Bukkit.createInventory(this,getRows()*9,title);
        this.addInvetory(inv,this);
        return inv;
    }

    public Inventory getInventory(String title){
        if(this.inv == null){
            this.inv = createInventory(getTitle());
        }
        return inv;
    }

    public Inventory createInventory(){
        return createInventory(title);
    }

    public void setTitle(String title){
        List<HumanEntity> viewers = getViewers();
        this.inv = createInventory(title);
        this.title = title;

        updating = true;
        for (HumanEntity viewer: viewers){
            show(viewer);
        }
        updating = false;
    }
    public String getTitle(){
        return title;
    }
}
