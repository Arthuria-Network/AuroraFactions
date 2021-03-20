package me.tropicalshadow.aurorafactions.claims;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.Logging;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Claims {

    private static AuroraFactions plugin;
    private final List<Claim> factionClaims = new ArrayList<>();
    public static NamespacedKey adminWandKey = null;
    public static NamespacedKey factionWandKey = null;

    public Claims(){
        plugin = AuroraFactions.getPlugin();
        loadFactionClaims();
        adminWandKey = new NamespacedKey(AuroraFactions.getPlugin(),"adminclaimwand");
        factionWandKey = new NamespacedKey(AuroraFactions.getPlugin(), "factionid");

    }
    public static enum AdminWandModes{
        REMOVE(0, ChatColor.RED + "Left click to remove claim",ChatColor.AQUA+"Right click to change mode"),
        SELECT(1,ChatColor.BLUE+"Left click to select a faction",ChatColor.AQUA+"Right click to change mode"),
        SET(2, ChatColor.GREEN+"Left click to set chunk to %faction% faction",ChatColor.AQUA+"Right click to change mode");

        private int id;
        private List<Component> lore;

        AdminWandModes(int id, String... lore){
            this.id = id;
            ArrayList<Component> comps = new ArrayList<>();
            for (String s : lore) {
                comps.add(Component.text(s));
            }
            this.lore = comps;
        }
        static public AdminWandModes fromID(int id){
            AdminWandModes output = null;
            for (AdminWandModes value : values()) {
                if(value.id==id){
                    output = value;
                }
            }
            return output;
        }

        public int getId() {
            return id;
        }

        public List<Component> getLore() {
            return lore;
        }
    }

    public static ItemStack adminWand(AdminWandModes adminWandModes, FactionColours factionColours){
        ItemStack item = new ItemStack(Material.NETHERITE_HOE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("Admin Claim Wand"));
        meta.lore(adminWandModes.getLore());
        meta.setUnbreakable(true);
        meta.getPersistentDataContainer().set(adminWandKey,PersistentDataType.INTEGER,adminWandModes.getId());
        meta.getPersistentDataContainer().set(factionWandKey,PersistentDataType.STRING,factionColours.name());
        item.setItemMeta(meta);
        return item;
    }

    public void renderLoadedChunks(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,()->{
            factionClaims.forEach(claim -> {
                //claim.chunk
                List<Entity> players = Arrays.stream(claim.chunk.getEntities()).filter((e)->(e instanceof Player)).collect(Collectors.toList());
                for (Entity entity : players) {
                    if(!(entity instanceof Player))continue;
                    Player player = ((Player) entity).getPlayer();
                    Chunk chunk = claim.chunk;
                    int minX = chunk.getX()*16;
                    int minZ = chunk.getZ()*16;
                    int minY = player.getLocation().getBlockY();
                    Color color = Color.BLACK;
                    switch (claim.faction.colour){
                        case RED:
                            color = Color.RED;
                            break;
                        case BLUE:
                            color = Color.BLUE;
                            break;
                        case YELLOW:
                            color = Color.YELLOW;
                            break;
                        case GREEN:
                            color = Color.GREEN;
                            break;
                        default:
                            color = Color.WHITE;
                            break;
                    }
                    Particle.DustOptions dustOptions = new Particle.DustOptions(color, 5);
                    int count = 4;
                    for (int y = minY; y<minY+10;y++) {
                        player.spawnParticle(Particle.REDSTONE, minX, y, minZ, count, dustOptions);
                        player.spawnParticle(Particle.REDSTONE, 16 + minX, y, minZ, count, dustOptions);
                        player.spawnParticle(Particle.REDSTONE, minX, y, 16 + minZ, count, dustOptions);
                        player.spawnParticle(Particle.REDSTONE, 16 + minX, y, minZ + 16, count, dustOptions);
                    }

                }

            });
        },10,40);
    }

    public void removeClaimsFromChunk(Chunk chunk){
        Claim toRemove = null;
        for (Claim factionClaim : this.factionClaims) {
            if(factionClaim.chunk == chunk){
                toRemove = factionClaim;
            }
        }
        if(toRemove!=null){
            factionClaims.remove(toRemove);
        }
    }

    public void addClaim(Claim claim){
        if(factionClaims.contains(claim))return;
        factionClaims.add(claim);
        saveFactionClaims();
    }


    public void loadFactionClaims(){
        if(!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        File file = new File(plugin.getDataFolder(),"factions.yml");
        try{
            if(!file.exists()){
                plugin.saveResource("factions.yml",true);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            for (FactionColours value : FactionColours.values()) {
                if(value!=FactionColours.NON){
                    String faction = value.name().toLowerCase();
                    if(yaml.contains(faction+".claims")){
                        ConfigurationSection claims =  yaml.getConfigurationSection(faction+".claims");
                        Map<String,Object> worldClaims = claims.getValues(false);
                        worldClaims.keySet().forEach((worldName)->{
                            World world = Bukkit.getWorld(worldName);
                            assert world != null;
                            for (long chunkLong :(List<Long>) worldClaims.get(worldName)){
                                int locX = (int) chunkLong;
                                int locY = (int) (chunkLong >> 32);
                                world.getChunkAtAsync(locX,locY,false,(tempChunk)->{
                                    Claim tempClaim = new Claim(value,tempChunk);
                                    if(!factionClaims.contains(tempClaim)){
                                        factionClaims.add(tempClaim);
                                    }
                                });

                            }

                        });
                    }
                }
            }
            yaml.save(file);
            Logging.info("Claims loaded");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void saveFactionClaims(){
        if(!plugin.getDataFolder().exists())plugin.getDataFolder().mkdir();
        File file = new File(plugin.getDataFolder(),"factions.yml");
        try{
            if(!file.exists()){
                plugin.saveResource("factions.yml",true);
            }
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            Map<FactionColours,List<Chunk>> map = new HashMap<>();
            factionClaims.forEach((claims)->{
                List<Chunk> chunks = map.getOrDefault(claims.faction,new ArrayList<>());
                chunks.add(claims.chunk);
                map.remove(claims.faction);
                map.put(claims.faction,chunks);
            });
            map.forEach((faction,chunks)->{
                Map<String, List<Long>> worlds = new HashMap<>();
                chunks.forEach(chunk -> {
                    List<Long> locations = worlds.getOrDefault(chunk.getWorld().getName(),new ArrayList<>());
                    locations.add(chunk.getChunkKey());
                    worlds.remove(chunk.getWorld().getName());
                    worlds.put(chunk.getWorld().getName(),locations);
                });
                worlds.forEach((worldName,locs)->{
                    yaml.set(faction.name().toLowerCase()+".claims."+worldName,locs);
                });
            });
            yaml.save(file);
            Logging.info("Claims saved");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static class Claim{
        FactionColours faction;
        Chunk chunk;

        public Claim(FactionColours faction, World world, int x, int y){
            this.faction = faction;
            chunk = world.getChunkAt(x,y);
        }
        public Claim(FactionColours faction, Chunk chunk){
            this.faction = faction;
            this.chunk = chunk;
        }
        public FactionColours getFaction() {
            return faction;
        }

        public Chunk getChunk() {
            return chunk;
        }
    }
}
