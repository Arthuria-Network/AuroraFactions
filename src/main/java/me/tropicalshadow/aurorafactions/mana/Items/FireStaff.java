package me.tropicalshadow.aurorafactions.mana.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class FireStaff implements ManaItemBase{


    public ItemStack getFireStaff(){
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.RED+"Fire Staff");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        lore.add(ChatColor.RED.toString()+"Fire Staff");
        lore.add(ChatColor.RED.toString()+"Red Faction Only");
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    @Override
    public int getCost(){
        return 50;
    }

    @Override
    public ItemStack getItem() {
        return getFireStaff();
    }

}
