package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.ItemUtils;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@ShadowCommandInfo(name="powercrystal",isPlayerOnly = true,permission = "aurorafactions.powercrystal.give")
public class PowerCrystalCommand extends ShadowCommand {


    @Override
    public void execute(Player player, String[] args) {
        if(args.length <= 1 || !args[0].equalsIgnoreCase("give")){
            player.sendMessage(ChatColor.RED.toString()+"Not Enough Arguments: /powercrystal <give> <colour>");
            return;
        }
        if(!new ArrayList<>(Arrays.asList("red", "blue", "green", "yellow", "display")).contains(args[1].toLowerCase())){
            player.sendMessage(ChatColor.RED.toString()+"Colour not understood: /powercrystal <give> <colour> <- use one of these (red,blue,green,yellow)");
            return;
        }
        if(args[1].equalsIgnoreCase("display")){
            ItemStack item = ItemUtils.getDisplayCrystal();
            player.getInventory().addItem(item);
            player.sendMessage("you have been given a display powercrystal.");
            return;
        }
        FactionColours colour = FactionColours.getFromName(args[1]);
        ItemStack item = ItemUtils.getFactionCrystalSpawner(colour);
        Map<Integer,ItemStack> leftovers = player.getInventory().addItem(item);
        if(!leftovers.isEmpty()){
            player.sendMessage(ChatColor.RED.toString()+"Not enough room in your inventory.");
            return;
        }
        player.sendMessage(ChatColor.GREEN.toString()+"Successfully given "+ PlainComponentSerializer.plain().serialize(item.getItemMeta().displayName()));


    }
}
