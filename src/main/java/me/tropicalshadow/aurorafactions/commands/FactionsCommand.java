package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.utils.ItemUtils;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;


@ShadowCommandInfo(name="factions",isPlayerOnly = true)
public class FactionsCommand extends ShadowCommand {


    @Override
    public void execute(Player player, String[] args) {
        if(args.length >= 2 && player.hasPermission("aurorafactions.factions.op")){
            String groupName = args[0];
            ArrayList<String> Colours =  new ArrayList<String>( ) {{
                add("red");
                add("blue");
                add("green");
                add("yellow");
            }
            };
            if(!Colours.contains(groupName.toLowerCase().trim())){
                player.sendMessage(ChatColor.RED.toString()+"Err: colour not found, try /factions <colour> <username>");
                return;
            }
            String username = args[1];
            Player altPlayer = Bukkit.getPlayer(username);
            if(altPlayer==null){
                player.sendMessage(ChatColor.RED.toString()+"Err: Username not found, try /factions <colour> <username>");
                return;
            }
            PermissionUtils.addFactionRole(altPlayer,groupName);
        }else if(args.length == 1 && player.hasPermission("aurorafactions.factions.op")){
            player.sendMessage(ChatColor.RED.toString()+"Err: Invalid Format, try /factions <colour> <username> or /factions");
        }else{

            if(!(player.isOp() || player.hasPermission("*")) && PermissionUtils.playerHasFactionRole((player))){
                player.sendMessage("You are already in a faction.");
                return;
            }
            ChestGui gui = new ChestGui("Faction Selector",1);
            setupFactionSelectorGui(gui);
            gui.canClick = false;
            gui.setOnClick(event->{
                int slotClick = event.getSlot();
                if(event.getClickedInventory().getHolder() instanceof HumanEntity)return;
                if(!(player.isOp() || player.hasPermission("*")) && PermissionUtils.playerHasFactionRole(((Player)event.getWhoClicked()))){
                    event.getWhoClicked().sendMessage("You are already in a faction.");
                    Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                        event.getWhoClicked().closeInventory();
                    });
                    return;
                }
                Player clicked = ((Player) event.getWhoClicked());
                if(slotClick == 2){
                    PermissionUtils.addFactionRole(clicked,"red");
                }else if(slotClick == 3){
                    PermissionUtils.addFactionRole(clicked,"blue");
                }else if(slotClick == 5){
                    PermissionUtils.addFactionRole(clicked,"green");
                }else if(slotClick==6){
                    PermissionUtils.addFactionRole(clicked,"yellow");
                }
                Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                    event.getWhoClicked().closeInventory();
                });
            });
            gui.show(player);

        }

    }
    private void setupFactionSelectorGui(ChestGui gui) {
        for (int i = 0; i < gui.getRows()*9; i++) {
            gui.getInventory().setItem(i, ItemUtils.renameItemStack(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)," "));
        }
        gui.getInventory().setItem(2, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.RED_CONCRETE),"&4&lRed &rFaction")));
        gui.getInventory().setItem(3, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.BLUE_CONCRETE),"&b&lBlue &rFaction")));
        gui.getInventory().setItem(5, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.GREEN_CONCRETE),"&a&lGreen &rFaction")));
        gui.getInventory().setItem(6, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.YELLOW_CONCRETE),"&e&lYellow &rFaction")));
    }

}