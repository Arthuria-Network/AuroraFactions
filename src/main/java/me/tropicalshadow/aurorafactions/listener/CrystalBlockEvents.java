package me.tropicalshadow.aurorafactions.listener;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.object.PowerCrystal;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.ItemUtils;
import me.tropicalshadow.aurorafactions.utils.Logging;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrystalBlockEvents implements Listener {


    private static AuroraFactions instance;
    public static NamespacedKey CrystalHealthKey;
    public static NamespacedKey CrystalColourKey;

    public CrystalBlockEvents() {
        instance = AuroraFactions.getPlugin();
        CrystalHealthKey = new NamespacedKey(instance, "Health");
        CrystalColourKey = new NamespacedKey(instance, "Colour");
    }

    public void CrystalDestroyEvent(FactionColours colour){
        //TODO
    }
    @EventHandler()
    public void onDisplayCrystalPlace(PlayerInteractEvent event) {
        if(event.getItem() == null )return;
        ItemStack item = event.getItem();
        if (!item.isSimilar(ItemUtils.getDisplayCrystal())) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Player player = event.getPlayer();
        if(!player.isOp() && !player.hasPermission("aurorafactions.factions.op")){
            event.setCancelled(true);
            return;
        }

            World world = player.getWorld();
            Location loc = player.getLocation();
            EnderCrystal TheCrystal = world.spawn(event.getClickedBlock().getLocation(), EnderCrystal.class, (entity) -> {
                entity.setCustomNameVisible(true);
                entity.setCustomName(ChatColor.LIGHT_PURPLE+"Join Faction");
                PersistentDataContainer container = entity.getPersistentDataContainer();
                container.set(CrystalColourKey, PersistentDataType.STRING,"Display");
                entity.setGravity(false);
                entity.setShowingBottom(false);
            });
            event.setCancelled(true);
            return;

    }
    @EventHandler()
    public void onCrystalPlace(PlayerInteractEvent event) {
        if (event.getItem() == null || !ItemUtils.isCrystalItem(event.getItem())) return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        FactionColours colour = ItemUtils.getFactionColourFromItem(event.getItem());
        if (colour == null || colour.equals(FactionColours.NON)) {
            event.setCancelled(true);
            //Logging.warning("Failed to detect Faction Colour");
            return;
        }
        Player player = event.getPlayer();
        if(!PermissionUtils.isMemberLeader(player,colour) && !player.isOp()){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED.toString()+"You need to be the leader of the "+colour.formatedName+ " faction to place the powercrystal");
            return;
        }
        Block Clickedblock = event.getClickedBlock();
        assert Clickedblock != null;
        Location loc = Clickedblock.getLocation();
        loc.add(event.getBlockFace().getDirection());
        //loc.getWorld().spawn(loc, PowerCrystal.class);
        if(loc.getWorld()==null)return;
        World world = loc.getWorld();
        EnderCrystal TheCrystal = world.spawn(loc, EnderCrystal.class, (entity) -> {
            //entity.setBeamTarget(loc.clone().add(0, 1, 0));
            entity.setCustomNameVisible(true);
            entity.setCustomName(colour.colour.toString()+ colour.formatedName + " Crystal");
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set(CrystalHealthKey, PersistentDataType.DOUBLE, 2000.0);
            container.set(CrystalColourKey, PersistentDataType.STRING, colour.name());
            entity.setGravity(false);
            entity.setShowingBottom(false);
        });
        //world.strikeLightningEffect(loc);

        event.setCancelled(true);//TODO - Remove Item From Inventory & save location to file

    }

    @EventHandler()
    public void attackCrystal(EntityDamageByEntityEvent event) {
        if (!event.getEntityType().equals(EntityType.ENDER_CRYSTAL)) return;
        if(event.getEntity().getCustomName()==null)return;

        final String regex = "(.*)(blue|red|green|yellow) Crystal";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(event.getEntity().getCustomName());
        final String regex2 = "(.*)Join Faction";
        final Pattern pattern2 = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE);
        final Matcher matcher2 = pattern2.matcher(event.getEntity().getCustomName());
        boolean match = matcher.matches();
        boolean match2 = matcher2.matches();
        if(!match&&!match2)return;

        if((!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) || (!(event.getDamager() instanceof Player)))){
            event.setCancelled(true);
            return;
        }
        Player player = ((Player)event.getDamager());
        if(match2 && !player.getGameMode().equals(GameMode.CREATIVE)){
            player.performCommand("factions");
        }
        if(player.getGameMode().equals(GameMode.CREATIVE)){
            if(!player.isSneaking()) {
                player.sendMessage("To remove this powercrystal you need to shift and hit this crystal");
            }else{
                player.sendMessage("Removing Crystal!!!");
                event.getEntity().remove();
            }
            event.setCancelled(true);
            return;
        }else if(match2){
            event.setCancelled(true);
            return;
        }
        String Colour = matcher.group(2);
        FactionColours colour = PermissionUtils.getFactionColour(player);
        if(colour.name().equalsIgnoreCase(Colour.trim())){
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED.toString()+"You can't damage your own crystal.");
            return;
        }
        EnderCrystal powerCrystal = ((EnderCrystal)event.getEntity());
        PersistentDataContainer container = powerCrystal.getPersistentDataContainer();//TODO - Read and write location to file
        if(!container.has(CrystalHealthKey,PersistentDataType.DOUBLE)){
            Logging.danger("Crystal failed to get persistentdatacontainer");
            event.setCancelled(true);
            return;
        }
        double health = container.getOrDefault(CrystalHealthKey,PersistentDataType.DOUBLE,0.0);
        double newHealth = health-event.getFinalDamage();

        FactionColours crystalColour = FactionColours.getFromName(Colour);
        if(newHealth<=0 || event.getEntity().isDead()){
            player.sendMessage(ChatColor.GREEN.toString()+"You have destroyed the "+crystalColour.formatedName+" powercrystal");
            ComponentBuilder MessageBuilder = new ComponentBuilder();
            MessageBuilder.append(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',"&4&k|||||||&4&l "+Colour+"'s &4powercrystal has been destroyed by "+((Player) event.getDamager()).getDisplayName() +" &4&k|||||||"));
            Location loc = powerCrystal.getLocation();
            powerCrystal.getWorld().spawnParticle(Particle.EXPLOSION_HUGE,loc,5);
            Bukkit.spigot().broadcast(MessageBuilder.create()[0]);
            powerCrystal.remove();
            event.setCancelled(true);
            return;
        }
        ComponentBuilder MessageBuilder = new ComponentBuilder();
        boolean flag = false;
        if(health>1500 && newHealth <= 1500){
            MessageBuilder.color(ChatColor.GREEN.asBungee());
            MessageBuilder.append(crystalColour.formatedName+"'s powercrystal has been damaged to 75%");
            flag = true;
        }else if(health>1000 && newHealth <= 1000){
            MessageBuilder.color(ChatColor.DARK_GREEN.asBungee());
            MessageBuilder.append(crystalColour.formatedName+"'s powercrystal has been lowered to 50%");
            flag = true;
        }else if(health>500 && newHealth <= 500){
            MessageBuilder.color(ChatColor.YELLOW.asBungee());
            MessageBuilder.append(crystalColour.formatedName+"'s powercrystal has been lowered to 25%");
            flag = true;
        }else if(health>200 && newHealth <=200){
            MessageBuilder.color(ChatColor.RED.asBungee());
            MessageBuilder.append(crystalColour.formatedName+"'s powercrystal has been lowered to 10%");
            flag = true;
        }else if(health>100 && newHealth <= 100){
            MessageBuilder.color(ChatColor.DARK_RED.asBungee());
            MessageBuilder.append(crystalColour.formatedName+"'s powercrystal has been lowered to 5%");
            flag = true;
        }
        if(flag){
            Bukkit.spigot().broadcast(MessageBuilder.create()[0]);
        }else{
            Location loc = powerCrystal.getLocation();
            loc.getWorld().playEffect(loc,Effect.STEP_SOUND,Material.REDSTONE_BLOCK);
        }
        container.set(CrystalHealthKey,PersistentDataType.DOUBLE,newHealth);
        event.setCancelled(true);
    }

    @EventHandler()
    public void BlockDamageEntity(EntityDamageByBlockEvent event){
        if (!event.getEntityType().equals(EntityType.ENDER_CRYSTAL)) return;
        if(event.getEntity().getCustomName()==null)return;

        final String regex = "(blue|red|green|yellow) Crystal";
        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(event.getEntity().getCustomName());
        boolean match = matcher.matches();
        if(!match)return;
        if(!event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            event.setCancelled(true);
            return;
        }
    }
}