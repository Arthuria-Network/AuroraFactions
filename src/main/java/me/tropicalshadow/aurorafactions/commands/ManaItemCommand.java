package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.mana.ItemManager;
import me.tropicalshadow.aurorafactions.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;


@ShadowCommandInfo(name="manaitem",isPlayerOnly = true,permission = "aurorafactions.manaitems.give")
public class ManaItemCommand extends ShadowCommand {


    @Override
    public void execute(Player player, String[] args) {
            if(args.length<=0){
                player.sendMessage(ChatColor.RED.toString()+"ERR: /manaitem NAMEOFITEM");
                return;
            }
            if(args[0].equalsIgnoreCase("list")) {
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.GREEN.toString()).append("Current Mana Items");
                ItemManager.ManaItem.getNames().forEach(s-> builder.append("\n").append(s.toLowerCase()));

                player.sendMessage(builder.toString());
                return;
            }else if(args[0].equalsIgnoreCase("gui")){
                ItemManager.ManaItem[] items = ItemManager.ManaItem.values();
                ChestGui gui = new ChestGui("Mana Items - Creative Menu",6);
                gui.canClick = false;
                for (int index=0; index<(6*9);index++){
                    if(index >= 5*9){
                        gui.getInventory().setItem(index, new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
                    }else {
                        if(index+1<=items.length){
                            gui.getInventory().setItem(index, items[index].getItem());
                        }else{
                            gui.getInventory().setItem(index,  new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(" ").build());
                        }
                    }
                }
                gui.setOnClick((event) ->{
                    if(event.getClickedInventory().getHolder() instanceof Player)return;
                    if(event.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE))return;
                    if(event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE))return;
                    event.getWhoClicked().getInventory().addItem(event.getCurrentItem());
                    event.setCurrentItem(event.getCurrentItem());

                });
                gui.show(player);
            }else if(ItemManager.ManaItem.getNames().contains(args[0].toUpperCase())){
                ItemManager.ManaItem item = ItemManager.ManaItem.getFromName(args[0]);
                if(item==null){
                    player.sendMessage(ChatColor.RED+"Err: Name you entered was incorrect");
                    return;
                }
                player.getInventory().addItem(item.getItem());
                player.sendMessage(ChatColor.AQUA+"You have been given the "+item.getFriendlyName());

            }else{
                player.sendMessage(ChatColor.RED.toString()+"ERR: Invalid Item Name");
            }

    }
}
