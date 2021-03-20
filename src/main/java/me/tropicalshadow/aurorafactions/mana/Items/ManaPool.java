package me.tropicalshadow.aurorafactions.mana.Items;

import me.tropicalshadow.aurorafactions.mana.Mana;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ManaPool implements ManaItemBase{

    public ItemStack getManaPool(){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.displayName(Component.text(ChatColor.AQUA+"Mana Pool"));
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        lore.add(ChatColor.AQUA.toString()+"Mana Pool");
        lore.add(ChatColor.RED.toString()+"OP Only");
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    @Override
    public void execute(Player player){
        Mana manaPlayer = Mana.getPlayer(player);
        manaPlayer.changeUse(manaPlayer.getMax()-manaPlayer.getUse());
    }

    @Override
    public ItemStack getItem() {
        return getManaPool();
    }
}
