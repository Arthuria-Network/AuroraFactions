package me.tropicalshadow.aurorafactions.mana.Items;

import me.tropicalshadow.aurorafactions.mana.ItemManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
    public void execute(Player player){
        Location loc = player.getEyeLocation();
        World world = player.getWorld();
        FallingBlock falling = world.spawnFallingBlock(loc.add(loc.getDirection()), Material.BLUE_ICE.createBlockData());
        falling.setGravity(true);
        falling.setGlowing(true);
        falling.setInvulnerable(true);
        falling.setDropItem(false);
        falling.setPersistent(false);
        falling.setVelocity(loc.getDirection().multiply(2));
        falling.getPersistentDataContainer().set(ItemManager.playerIdentifier, PersistentDataType.STRING,player.getUniqueId().toString());

    }

    @Override
    public ItemStack getItem() {
        return getIceStaff();
    }
}
