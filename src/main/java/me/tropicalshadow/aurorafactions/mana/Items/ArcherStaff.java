package me.tropicalshadow.aurorafactions.mana.Items;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ArcherStaff implements ManaItemBase {


    public ItemStack getArcherStaff(){
        ItemStack item = new ItemStack(Material.STICK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setDisplayName(ChatColor.YELLOW+"Archer Staff");
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        lore.add(ChatColor.YELLOW.toString()+"Ice Staff");
        lore.add(ChatColor.YELLOW.toString()+"Yellow Faction Only");
        lore.add(ChatColor.DARK_PURPLE.toString()+ChatColor.MAGIC.toString()+"||||||||||||||||");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void execute(Player player){
        Location loc = player.getEyeLocation();
        Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
            for(int i=-2;i<=2;i++){
                for(int k=-2;k<=2;k++) {
                    SpectralArrow arrow = player.launchProjectile(SpectralArrow.class, loc.getDirection().add(new Vector(i,0,k)));//multiply instead?
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                }
            }
        });
    }


    @Override
    public ItemStack getItem() {
        return getArcherStaff();
    }
}
