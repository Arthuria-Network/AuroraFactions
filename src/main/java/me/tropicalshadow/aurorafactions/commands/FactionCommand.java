package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.listener.FactionAbillites;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.ItemUtils;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ShadowCommandInfo(name="faction",isPlayerOnly = true, usage = "/<command> [chat|kick|trust] ...")
public class FactionCommand extends ShadowCommand {

    @Override
    public void execute(Player player, String[] args) {
            if(PermissionUtils.getFactionColour(player)== FactionColours.NON)return;
            if(args.length<1){
                player.sendMessage(getCommandInfo().usage().replaceAll("<command>",getCommandInfo().name()));
                return;
            }
            if(args[0].equalsIgnoreCase("chat")){
                AuroraFactions.getPlugin().getChannelManger().toggleChat(player);
            }else if(args[0].equalsIgnoreCase("kick")&&player.hasPermission("aurorafactions.kick")){
                FactionColours colour = PermissionUtils.getFactionColour(player);
                boolean isLeader = PermissionUtils.isMemberLeader(player,colour);
                if(!isLeader){
                    player.sendMessage("you are not leader of your faction");
                    return;
                }

                //TODO - REMOVE FROM TRUSTED IF IN
                ChestGui kickGui = new ChestGui("Kick from "+colour.formatedName+" faction ",6);
                Inventory inv = kickGui.getInventory();
                kickGui.canClick = false;
                //kickGui.callOnClick((invClick)->{
                //TEST IF INVENTORY
                //GET PLAYER IF SKULL
                //REMOVE PLAYER FACTION ROLE
                //SEND TO SPAWN
                //
                //});//if head kick member
                for (int i = 0; i < inv.getSize(); i++) {
                    inv.setItem(i, ItemUtils.renameItemStack(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)," "));
                }
                int index = 0;
                List<? extends Player> players = AuroraFactions.getPlugin().getServer().getOnlinePlayers().stream().filter(p-> PermissionUtils.getFactionColour(p)==colour).collect(Collectors.toList());
                ItemStack[] items = getPlayerHeadsForKick((List<Player>) players);
                for (int i = 1; i < 8; i++) {
                    for (int j = 1; j < 5; j++) {
                        int num = i+(j*9);
                        inv.setItem(num, items[index]);
                        index++;
                    }
                }
            }else if(args[0].equalsIgnoreCase("unlocks")){
                FactionAbillites.sendUnlockScreen(player);
            }else if(args[0].equalsIgnoreCase("trust")){
                if(!PermissionUtils.isMemberLeader(player)){
                    player.sendMessage("You are not the faction leader");
                    return;
                }
                if(args.length<2){
                    player.sendMessage(ChatColor.RED+"/faction trust username");
                    return;
                }
                FactionColours faction = PermissionUtils.getFactionColour(player);
                if(args[1].equalsIgnoreCase("list")){
                    ArrayList<String> trusted = AuroraFactions.getPlugin().getClaims().getTrusted(faction);
                    if(trusted.isEmpty()){
                        player.sendMessage(Component.text("Trust a member before asking me who is trusted!!"));
                        return;
                    }
                    Component comp = Component.text(faction.colour.toString()+"--------trusted--------\n");
                    for (String s : trusted) {
                        comp = comp.append(Component.text(s+"\n"));
                    }
                    player.sendMessage(comp);
                    return;
                }
                Player trusting = Bukkit.getPlayer(args[1]);
                if(trusting==null){
                    player.sendMessage(ChatColor.RED.toString()+args[1]+" is a unknown player");
                    return ;
                }
                if(!PermissionUtils.getFactionColour(trusting).equals(faction)){
                    player.sendMessage(ChatColor.RED.toString()+args[1]+" is not in your faction");
                    return;
                }
                AuroraFactions.getPlugin().getClaims().trustMember(faction,trusting);
                player.sendMessage(ChatColor.GREEN.toString()+args[1]+" has been trusted to your claims");
            }

    }
    private ItemStack[] getPlayerHeadsForKick(List<Player> players){
        ItemStack[] items = new ItemStack[players.size()];
        for (int i = 0; i < players.size(); i++) {
            ItemStack tempHead = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) tempHead.getItemMeta();
            meta.setOwningPlayer(players.get(i));
            meta.setDisplayName(players.get(i).getDisplayName());
            ArrayList<String> list = new ArrayList<>();
            list.add(ChatColor.GREEN+"Click to kick member");
            meta.setLore(list);
            tempHead.setItemMeta(meta);
            items[i] = tempHead;
        }
        return items;
    }
}
