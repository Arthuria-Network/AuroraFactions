package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandAlias;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Random;


@ShadowCommandInfo(name="Wild",isPlayerOnly = true)
@ShadowCommandAlias(alias = {"rtp", "randomtp"})
public class WildCommand extends ShadowCommand {

    private final AuroraFactions plugin;

    public WildCommand(){
        plugin = AuroraFactions.getPlugin();

    }

    @Override
    public void execute(Player player, String[] args) {
        String worldName = player.getWorld().getName();
        FileConfiguration config = plugin.getConfig();
        if(args.length>=1 && player.hasPermission("aurorafactions.setrtp")){
            if(args.length >= 3){
                if(!args[0].equalsIgnoreCase("set")){
                    player.sendMessage(Component.text(ChatColor.RED+"/wild set <-- ...[center|radius|enabed] <value> "));
                    return;
                }
                if(!args[1].equalsIgnoreCase("center") && !args[1].equalsIgnoreCase("enabled") && !args[1].equalsIgnoreCase("radius")){
                    player.sendMessage(Component.text(ChatColor.RED+"/wild set [center|radius|enabed] <-- ...<value> "));
                    return;
                }
                String type = args[1];
                if(type.equalsIgnoreCase("center")) {
                    if (!args[2].equalsIgnoreCase("me") && !args[2].equalsIgnoreCase("spawn")) {
                        player.sendMessage(Component.text(ChatColor.RED + "/wild set center [me | spawn]"));
                        return;
                    }
                    if (args[2].equalsIgnoreCase("me")) {
                        plugin.getConfig().set("rtp." + player.getWorld().getName() + ".center", player.getLocation());
                        plugin.saveConfig();
                        player.sendMessage(Component.text(ChatColor.GREEN+"Center set to your current location"));
                    } else {
                        plugin.getConfig().set("rtp." + player.getWorld().getName() + ".center", player.getWorld().getSpawnLocation());
                        plugin.saveConfig();
                        player.sendMessage(Component.text(ChatColor.GREEN+"Center set to world spawn"));

                    }
                    return;
                }else if(type.equalsIgnoreCase("radius")){
                    try{
                        int radius = Integer.parseInt(args[2]);
                        plugin.getConfig().set("rtp." + player.getWorld().getName() + ".radius", (double) radius);
                        plugin.saveConfig();
                        player.sendMessage(Component.text(ChatColor.GREEN+"Radius set to "+radius));
                    }catch (NumberFormatException e){
                        player.sendMessage(Component.text(ChatColor.RED + "/wild set radius [value]"));
                    }
                }else if(type.equalsIgnoreCase("enabled")){
                    if(args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("y") || args[2].equalsIgnoreCase("yes")){
                        plugin.getConfig().set("rtp." + player.getWorld().getName() + ".enabled", true);
                        plugin.saveConfig();
                        player.sendMessage(Component.text(ChatColor.GREEN+"RTP has been enabled in this world"));
                    }else if(args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("n") || args[2].equalsIgnoreCase("no")){
                        plugin.getConfig().set("rtp." + player.getWorld().getName() + ".enabled", false);
                        plugin.saveConfig();
                        player.sendMessage(Component.text(ChatColor.GREEN+"RTP has been disabled in this world"));
                    }else{
                        player.sendMessage(Component.text(ChatColor.RED+"/wild set enabled [true|false]"));
                    }
                    return;
                }
                return;

            }else{
                player.sendMessage(Component.text(ChatColor.RED+"/wild set [center|radius|enabed] <value> "));
                return;
            }
        }

        if(!config.isConfigurationSection("rtp."+worldName)){
            player.sendMessage(Component.text(ChatColor.RED+"This world has not got rtp setup in it"));
            return;
        }
        if(!config.getBoolean("rtp."+worldName+".enabled")){
            player.sendMessage(Component.text(ChatColor.RED+"RTP has been disabled in this world"));
            return;
        }
        if(!config.isLocation("rtp."+worldName+".center") || !config.isDouble("rtp."+worldName+".radius")){
            player.sendMessage(Component.text(ChatColor.RED+"RTP has been configure improperly in this world"));
            return;
        }
        Location loc = getRandomLocation(player.getWorld());
        while(!Claims.isChunkClaimed(loc.getChunk()).equals(FactionColours.NON)){
            loc = getRandomLocation(player.getWorld());
        }
        if(!loc.isChunkLoaded())
            loc.getChunk().load(true);

        player.teleportAsync(loc.add(0,1,0));
        player.sendMessage(Component.text(ChatColor.BLUE+"You have been teleported to "+loc.getBlockX()+ ", "+loc.getBlockY()+", "+loc.getBlockZ()));


    }

    private Location getRandomLocation(World world){
        FileConfiguration config = plugin.getConfig();
        Location center = config.getLocation("rtp."+world.getName()+".center");
        double radius = config.getDouble("rtp."+world.getName()+".radius");
        double x = ((new Random().nextInt(((int)radius*2)))-radius)+center.getX();
        double z = ((new Random().nextInt(((int)radius*2)))-radius)+center.getZ();
        Location loc = new Location(world,x,60,z);
        loc = loc.toHighestLocation();
        return loc;
    }
}
