package me.tropicalshadow.aurorafactions.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtils {


    public static ItemStack renameItemStack(ItemStack item, String name){
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack factionSelectorItem(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED.toString()+"!Warning!");
        lore.add(ChatColor.WHITE.toString()+"You can't change factions");
        lore.add(ChatColor.WHITE.toString()+"Once you have selected");
        assert meta != null;
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static boolean isCrystalItem(ItemStack item){
        if(item == null || !item.getType().equals(getFactionCrystalSpawner(FactionColours.NON).getType())){
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if(meta==null)return false;
        if(meta.getLore()==null||meta.getLore().isEmpty())return false;
        boolean match = false;
        for (FactionColours colour : FactionColours.values()) {
            if(meta.getLore().equals(getCrystalLore(colour)))match=true;
        }
        return match;
    }
    public static FactionColours getFactionColourFromItem(ItemStack item){
        FactionColours results = FactionColours.NON;
        ItemMeta meta = item.getItemMeta();
        if(meta==null)return results;
        if(meta.getLore()==null || meta.getLore().size()<=3)return results;
        String lore = meta.getLore().get(3);
        String[] ColourLine = lore.split(":");
        if(ColourLine.length<=1)return results;
        String ColourName = ColourLine[1].trim().toLowerCase();
        //Logging.info("got "+ColourName+" from item");
        return FactionColours.getFromName(ColourName);
    }

    private static ArrayList<String> getCrystalLore(FactionColours colour){
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE.toString()+"Right click block to");
        lore.add(ChatColor.WHITE.toString()+"place your factions");
        lore.add(ChatColor.WHITE.toString()+"Power crystal");
        lore.add(colour.colour.toString()+"Colour: "+colour.formatedName);
        return lore;
    }
    public static ItemStack getFactionCrystalSpawner(FactionColours colour){
        ItemStack item = new ItemStack(Material.CONDUIT);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(colour.colour.toString()+colour.formatedName+" Crystal");
        ArrayList<String> lore = getCrystalLore(colour);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack getDisplayCrystal(){
        ItemStack item = new ItemStack(Material.GLASS);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.LIGHT_PURPLE+"Join Faction");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN+"Place down to summon");
        lore.add(ChatColor.GREEN+"Display crystal");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
