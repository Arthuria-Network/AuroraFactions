package me.tropicalshadow.aurorafactions.mana.Items;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class IceStaff implements ManaItemBase {


    public ItemStack getIceStaff(){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.BLUE+"Ice Staff");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        lore.add(ChatColor.BLUE.toString()+"Ice Staff");
        lore.add(ChatColor.BLUE.toString()+"Blue Faction Only");
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
        return getIceStaff();
    }
}
