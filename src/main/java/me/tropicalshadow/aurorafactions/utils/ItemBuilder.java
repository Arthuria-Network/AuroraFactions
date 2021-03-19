package me.tropicalshadow.aurorafactions.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    String name;
    Material material;
    ArrayList<String> lore = new ArrayList<>();



    public ItemBuilder(Material mat){
        material = mat;
    }

    public ItemBuilder name(String newName){
        name = newName;
        return this;
    }
    public ItemBuilder addLoreLine(String newLine){
        lore.add(newLine);
        return this;
    }
    private String colorise(String str){
        return ChatColor.translateAlternateColorCodes('&',str);
    }
    private Component componentColor(String str){
        return Component.text(colorise(str));
    }

    public ItemStack build(){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(componentColor(name));
        if(!lore.isEmpty()){
            List<Component> compLore = new ArrayList<>();
            lore.forEach(loreItem -> compLore.add(componentColor(loreItem)));
            meta.lore(compLore);
        }

        item.setItemMeta(meta);
        return item;
    }
}
