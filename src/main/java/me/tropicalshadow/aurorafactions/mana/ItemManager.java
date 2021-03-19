package me.tropicalshadow.aurorafactions.mana;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.mana.Items.*;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.Logging;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ItemManager implements Listener {

    private static NamespacedKey playerIdentifier;

    public ItemManager(){
        playerIdentifier = new NamespacedKey(AuroraFactions.getPlugin(),"PlayerIdentifier");
    }

    private void sendCostFailure(Player player){
        player.sendMessage(ChatColor.RED+"You don't have enough mana to use this");
    }
    
    @EventHandler()
    public void onClick(PlayerInteractEvent event){
        Action action = event.getAction();
        if(event.getItem()==null)return;
        if((action.equals(Action.LEFT_CLICK_AIR) || action.equals(Action.LEFT_CLICK_BLOCK))&& !event.getPlayer().getGameMode().equals(GameMode.SPECTATOR) ){
            Player player = event.getPlayer();
            if(ManaItem.isManaItem(event.getItem(),ManaItem.FIRESTAFF)){
                if(Mana.getPlayer(player).changeUse(-ManaItem.FIRESTAFF.getCost()) == -1){ sendCostFailure(player);return;}

                Location loc = player.getEyeLocation();
                World world = player.getWorld();
                loc.getDirection();
                world.spawn(loc.add(loc.getDirection()), Fireball.class,(e)->{
                    e.setDirection(loc.getDirection());
                    e.setGlowing(true);
                    e.setIsIncendiary(true);
                    e.setYield(0);
                    e.setShooter(player);
                });
                event.setCancelled(true);
            }else if(ManaItem.isManaItem(event.getItem(),ManaItem.ICESTAFF)){
                if(Mana.getPlayer(player).changeUse(-ManaItem.ICESTAFF.getCost()) == -1){ sendCostFailure(player);return;}
                Location loc = player.getEyeLocation();
                World world = player.getWorld();
                FallingBlock falling = world.spawnFallingBlock(loc.add(loc.getDirection()), Material.BLUE_ICE.createBlockData());
                falling.setGravity(true);
                falling.setGlowing(true);
                falling.setInvulnerable(true);
                falling.setDropItem(false);
                falling.setPersistent(false);
                falling.setVelocity(loc.getDirection().multiply(2));
                falling.getPersistentDataContainer().set(playerIdentifier, PersistentDataType.STRING,player.getUniqueId().toString());

                //Fireball project = world.spawn(loc.add(loc.getDirection()).add(0,1,0), Fireball.class,(e)->{
                    //e.setDirection(loc.getDirection());
                    //e.setIsIncendiary(false);
                    //e.setFireTicks(0);
                    //e.setYield(0);
                    //e.setShooter(player);
                    //e.addPassenger(falling);
                //});

                event.setCancelled(true);
            }else if(ManaItem.isManaItem(event.getItem(),ManaItem.POISONSTAFF)){
                if(Mana.getPlayer(player).changeUse(-ManaItem.POISONSTAFF.getCost()) == -1){ sendCostFailure(player);return;}
                Location loc = player.getEyeLocation();
                World world = player.getWorld();
                FallingBlock falling = world.spawnFallingBlock(loc.add(loc.getDirection()), Material.NETHER_WART_BLOCK.createBlockData());
                falling.setGravity(true);
                falling.setGlowing(true);
                falling.setInvulnerable(true);
                falling.setDropItem(false);
                falling.setPersistent(false);
                falling.setVelocity(loc.getDirection().multiply(2));
                falling.getPersistentDataContainer().set(playerIdentifier, PersistentDataType.STRING,player.getUniqueId().toString());

                event.setCancelled(true);

            }else if(ManaItem.isManaItem(event.getItem(),ManaItem.ARCHERSTAFF)){
                if(Mana.getPlayer(player).changeUse(-ManaItem.ARCHERSTAFF.getCost()) == -1){ sendCostFailure(player);return;}
                Location loc = player.getEyeLocation();
                event.setCancelled(true);
                Bukkit.getScheduler().runTask(AuroraFactions.getPlugin(),()->{
                    for(int i=-2;i<=2;i++){
                        for(int k=-2;k<=2;k++) {
                            SpectralArrow arrow = player.launchProjectile(SpectralArrow.class, loc.getDirection().add(new Vector(i,0,k)));//multiply instead?
                            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                        }
                    }
                });
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if(e.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock fb = (FallingBlock) e.getEntity();
            if(fb.getBlockData().getMaterial().equals(Material.BLUE_ICE)){
                BlockData mat = e.getBlock().getBlockData();
                if(e.getBlock().getType().equals(Material.WATER) || e.getBlock().getType().equals(Material.LAVA)){
                    e.setCancelled(true);
                    return;
                }
                Player player = null;
                if(e.getEntity().getPersistentDataContainer().has(playerIdentifier,PersistentDataType.STRING)){
                    String playerID = e.getEntity().getPersistentDataContainer().get(playerIdentifier,PersistentDataType.STRING);
                    assert playerID != null;
                    UUID id = UUID.fromString(playerID);
                    player = Bukkit.getPlayer(id);
                }
                Player finalPlayer = player;
                Bukkit.getScheduler().runTaskLater(AuroraFactions.getPlugin(),()->{
                    e.getBlock().setBlockData(mat);
                    if(finalPlayer!=null){
                        e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(),5,false,false, finalPlayer);
                    }else{
                        e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(),5,false,false);
                    }
                },20*3);

                //e.getBlock().setType(Material.AIR);
            }else if(fb.getBlockData().getMaterial().equals(Material.NETHER_WART_BLOCK)){
                e.setCancelled(true);
                Location loc = e.getBlock().getLocation();
                World world = e.getBlock().getWorld();
                Collection<Entity> players = world.getNearbyEntities(loc,2,2,2);
                //Logging.info("Size = "+players.size());
                players.forEach(p->{
                    if(p instanceof Player){
                        if(PermissionUtils.getFactionColour((Player)p)!=FactionColours.GREEN){
                            ((Player)p).addPotionEffect((new PotionEffect(PotionEffectType.POISON, 20*3, 2)));
                            ((Player)p).addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION, 20*3, 10)));
                        }
                        if(((Player) p).getHealth() <=2){
                            Player player = null;
                            if(e.getEntity().getPersistentDataContainer().has(playerIdentifier,PersistentDataType.STRING)){
                                String playerID = e.getEntity().getPersistentDataContainer().get(playerIdentifier,PersistentDataType.STRING);
                                assert playerID != null;
                                UUID id = UUID.fromString(playerID);
                                player = Bukkit.getPlayer(id);
                                assert player != null;
                                player.attack(p);
                            }
                        }
                    }
                });
            }
        }
    }
    public enum ManaItem{
        FIRESTAFF("Fire Staff",new FireStaff(), FactionColours.RED,100),
        ICESTAFF("Ice Staff", new IceStaff(),FactionColours.BLUE,100),
        POISONSTAFF("Poison Staff", new PoisonStaff(),FactionColours.GREEN,100),
        ARCHERSTAFF("Archer Saff",new ArcherStaff(),FactionColours.YELLOW,100);

        private final String friendlyName;
        private final ManaItemBase item;
        private final FactionColours colour;
        private final int cost;

        ManaItem(String friendlyName, ManaItemBase item, FactionColours colour,int cost){
            this.friendlyName = friendlyName;
            this.item = item;
            this.colour = colour;
            this.cost = cost;
        }
        public ItemStack getItem(){
            return this.item.getItem();
        }
        public String getFriendlyName(){
            return this.friendlyName;
        }
        public int getCost(){return this.cost;}

        public FactionColours getColour() {
            return this.colour;
        }

        static boolean isManaItem(ItemStack item, ManaItem manaItem){
            if(item.equals(manaItem.getItem())){
                return true;
            }
            ItemMeta itemMeta = item.getItemMeta();
            if(manaItem.getItem()==null)return false;
            ItemMeta manaItemMeta = manaItem.getItem().getItemMeta();
            assert itemMeta != null;
            assert manaItemMeta != null;
            return manaItemMeta.getDisplayName().equalsIgnoreCase(itemMeta.getDisplayName()) && manaItemMeta.getLore().equals(itemMeta.getLore()) && manaItem.getItem().getType().equals(item.getType());
        }

        static ManaItem isItem(ItemStack item){
            ItemMeta itemMeta = item.getItemMeta();
            for (ManaItem manaItem : ManaItem.values()) {
                if(!manaItem.getItem().getType().equals(item.getType()))continue;
                ItemMeta manaItemMeta = manaItem.getItem().getItemMeta();
                assert manaItemMeta != null;
                assert itemMeta != null;
                if(manaItemMeta.getDisplayName().equalsIgnoreCase(itemMeta.getDisplayName())){
                    if (manaItemMeta.getLore().equals(itemMeta.getLore())){
                        return manaItem;
                    }
                }
            }
            return null;
        }
        public static ArrayList<String> getNames(){
            ArrayList<String> names = new ArrayList<>();
            for (ManaItem manaItem : ManaItem.values()) {
                names.add(manaItem.name());
            }
            return names;
        }
        public static ManaItem getFromName(String name){
            ManaItem item = null;
            for (ManaItem manaItem : ManaItem.values()) {
                if(manaItem.name().equalsIgnoreCase(name)){
                    item = manaItem;
                }
                
            }
            return item;
        }
    }

}
