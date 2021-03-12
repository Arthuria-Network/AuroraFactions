package me.tropicalshadow.aurorafactions;

import me.tropicalshadow.aurorafactions.gui.ChestGui;
import me.tropicalshadow.aurorafactions.mana.ItemManager;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.ItemUtils;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandSimper implements TabExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        String cmd = command.getName();
        if(isPlayer && cmd.equalsIgnoreCase("manaitem")){
            if(args.length<=0){
                sender.sendMessage(ChatColor.RED.toString()+"ERR: /manaitem NAMEOFITEM");
                return true;
            }
            if(args[0].equalsIgnoreCase("list")) {
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.GREEN.toString()).append("Current Mana Items");
                ItemManager.ManaItem.getNames().forEach(s-> builder.append("\n").append(s.toLowerCase()));

                sender.sendMessage(builder.toString());
                return true;
            }else if(ItemManager.ManaItem.getNames().contains(args[0].toUpperCase())){
                ItemManager.ManaItem item = ItemManager.ManaItem.getFromName(args[0]);
                if(item==null){
                    sender.sendMessage(ChatColor.RED+"Err: Name you entered was incorrect");
                    return true;
                }
                ((Player) sender).getInventory().addItem(item.getItem());
                sender.sendMessage(ChatColor.AQUA+"You have been given the "+item.getFriendlyName());
                return true;

            }else{
                sender.sendMessage(ChatColor.RED.toString()+"ERR: Invalid Item Name");
                return  true;
            }

        }else if(isPlayer && cmd.equalsIgnoreCase("plugins")){
            sender.sendMessage(ChatColor.WHITE.toString()+"Plugins (5): "+ChatColor.GREEN.toString()+"Never, Gonna, Give, You, Up" );
            return true;
        }else if(isPlayer && cmd.equalsIgnoreCase("factions")){
            if(args.length >= 2 && sender.hasPermission("aurorafactions.factions.op")){
                String groupName = args[0];
                ArrayList<String> Colours =  new ArrayList<String>( ) {{
                    add("red");
                    add("blue");
                    add("green");
                    add("yellow");
                }
                };
                if(!Colours.contains(groupName.toLowerCase().trim())){
                    sender.sendMessage(ChatColor.RED.toString()+"Err: colour not found, try /factions <colour> <username>");
                    return true;
                }
                String username = args[1];
                Player player = Bukkit.getPlayer(username);
                if(player==null){
                    sender.sendMessage(ChatColor.RED.toString()+"Err: Username not found, try /factions <colour> <username>");
                    return true;
                }
                PermissionUtils.addFactionRole(player,groupName);
            }else if(args.length == 1 && sender.hasPermission("aurorafactions.factions.op")){
                sender.sendMessage(ChatColor.RED.toString()+"Err: Invalid Format, try /factions <colour> <username> or /factions");
                return true;
            }else{

                if(!(sender.isOp() || sender.hasPermission("*")) && PermissionUtils.playerHasFactionRole(((Player) sender))){
                    sender.sendMessage("You are already in a faction.");
                    return true;
                }
                ChestGui gui = new ChestGui("Faction Selector",1);
                setupFactionSelectorGui(gui);
                gui.canClick = false;
                gui.setOnClick(event->{
                    int slotClick = event.getSlot();
                    if(event.getClickedInventory().getHolder() instanceof HumanEntity)return;
                    if(!(sender.isOp() || sender.hasPermission("*")) && PermissionUtils.playerHasFactionRole(((Player)event.getWhoClicked()))){
                        event.getWhoClicked().sendMessage("You are already in a faction.");
                        Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                            event.getWhoClicked().closeInventory();
                        });
                        return;
                    }
                    Player player = ((Player) event.getWhoClicked());
                    if(slotClick == 2){
                        PermissionUtils.addFactionRole(player,"red");
                    }else if(slotClick == 3){
                        PermissionUtils.addFactionRole(player,"blue");
                    }else if(slotClick == 5){
                        PermissionUtils.addFactionRole(player,"green");
                    }else if(slotClick==6){
                        PermissionUtils.addFactionRole(player,"yellow");
                    }
                    Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                        event.getWhoClicked().closeInventory();
                    });
                });
                gui.show(((Player) sender));
            }
        }else if(isPlayer && cmd.equalsIgnoreCase("powercrystal") && sender.hasPermission("aurorafactions.powercrystal.give")){
            Player player = ((Player)sender);
            if(args.length <= 1 || !args[0].equalsIgnoreCase("give")){
                player.sendMessage(ChatColor.RED.toString()+"Not Enough Arguments: /powercrystal <give> <colour>");
                return true;
            }
            if(!new ArrayList<String>(Arrays.asList("red", "blue", "green", "yellow","display")).contains(args[1].toLowerCase())){
                player.sendMessage(ChatColor.RED.toString()+"Colour not understood: /powercrystal <give> <colour> <- use one of these (red,blue,green,yellow)");
                return true;
            }
            if(args[1].equalsIgnoreCase("display")){
                ItemStack item = ItemUtils.getDisplayCrystal();
                player.getInventory().addItem(item);
                player.sendMessage("you have been given a display powercrystal.");
                return true;
            }
            FactionColours colour = FactionColours.getFromName(args[1]);
            ItemStack item = ItemUtils.getFactionCrystalSpawner(colour);
            Map<Integer,ItemStack> leftovers = player.getInventory().addItem(item);
            if(!leftovers.isEmpty()){
                player.sendMessage(ChatColor.RED.toString()+"Not enough room in your inventory.");
                return true;
            }
            player.sendMessage(ChatColor.GREEN.toString()+"Successfully given "+item.getItemMeta().getDisplayName());

        }else if(isPlayer && cmd.equalsIgnoreCase("faction")){
            Player player = ((Player)sender);
            if(PermissionUtils.getFactionColour(player)==FactionColours.NON)return true;
            if(args.length<1){
                return false;
            }
            if(args[0].equalsIgnoreCase("chat")){
                AuroraFactions.getPlugin().getChannelManger().toggleChat(player);
            }
            if(args[0].equalsIgnoreCase("kick")&&player.hasPermission("aurorafactions.kick")){
                FactionColours colour = PermissionUtils.getFactionColour(player);
                boolean isLeader = PermissionUtils.isMemberLeader(player,colour);
                if(!isLeader){
                    player.sendMessage("you are not leader of your faction");
                    return true;
                }
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
                    inv.setItem(i,ItemUtils.renameItemStack(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)," "));
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
            }
        }else if(isPlayer && cmd.equalsIgnoreCase("resourcepack")){
            Player player = ((Player)sender);
            player.setResourcePack("https://arthuria.club/resourcepacks/AuroraResourcePack.zip");
            player.sendMessage("Set your resource pack to aurora resource pack");
            return true;
        }
        return false;
    }
    @EventHandler()
    public void beforePluginsGetTheirPeskyHandsOnCommands(PlayerCommandPreprocessEvent event){
        boolean plugins = event.getMessage().toLowerCase().startsWith("/plugins");
        boolean pl = event.getMessage().toLowerCase().startsWith("/pl") && !event.getMessage().toLowerCase().startsWith("/plotme") && !event.getMessage().toLowerCase().startsWith("/plot") && !event.getMessage().toLowerCase().startsWith("/plotgenversion") && !event.getMessage().toLowerCase().startsWith("/pluginmanager") && !event.getMessage().toLowerCase().startsWith("/plugman") && !event.getMessage().toLowerCase().startsWith("/plane") && !event.getMessage().toLowerCase().startsWith("/planeshop") && !event.getMessage().toLowerCase().startsWith("/player") && !event.getMessage().toLowerCase().startsWith("/playtime");
        boolean bukkitplugin = event.getMessage().toLowerCase().startsWith("/bukkit:plugins");
        boolean bukkitpl = event.getMessage().toLowerCase().startsWith("/bukkit:pl");
        if(pl || plugins || bukkitpl || bukkitplugin){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.WHITE.toString()+"Plugins (5): "+ChatColor.GREEN.toString()+"Never, Gonna, Give, You, Up" );
            return;

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

    private void setupFactionSelectorGui(ChestGui gui) {
        for (int i = 0; i < gui.getRows()*9; i++) {
            gui.getInventory().setItem(i,ItemUtils.renameItemStack(new ItemStack(Material.PURPLE_STAINED_GLASS_PANE)," "));
        }
        gui.getInventory().setItem(2, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.RED_CONCRETE),"&4&lRed &rFaction")));
        gui.getInventory().setItem(3, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.BLUE_CONCRETE),"&b&lBlue &rFaction")));
        gui.getInventory().setItem(5, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.GREEN_CONCRETE),"&a&lGreen &rFaction")));
        gui.getInventory().setItem(6, ItemUtils.factionSelectorItem( ItemUtils.renameItemStack(new ItemStack(Material.YELLOW_CONCRETE),"&e&lYellow &rFaction")));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
