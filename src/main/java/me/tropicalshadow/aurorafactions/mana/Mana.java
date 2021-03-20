package me.tropicalshadow.aurorafactions.mana;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.utils.FileUtils;
import me.tropicalshadow.aurorafactions.utils.Logging;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Mana implements ConfigurationSerializable {

    public static Map<UUID,Mana> Players = new HashMap<>();

    public static Map<UUID,Mana> getPlayers(){
        return Players;
    }
    public static Mana getPlayer(Player player){
        if(!Players.containsKey(player.getUniqueId()))return addPlayer(player);

        return Players.get(player.getUniqueId());
    }
    public static Map<UUID,Mana> setPlayers(){
        Players = FileUtils.getOnlinePlayers();
        return Players;
    }
    public static Mana addPlayer(Player player){
        Mana mana = FileUtils.getManaForPlayer(player);
        Players.put(mana.getPlayerID(),mana);
        return mana;
    }
    public static void removePlayer(Player player){
        if(!Players.containsKey(player.getUniqueId())){
            return;
        }
        Players.remove(player.getUniqueId());
    }

    private final Lock queueLock = new ReentrantLock();
    public UUID playerID;
    public int use;
    public int max;

    public Mana(UUID playerID,int use,int max){
        this.playerID = playerID;
        this.use = use;
        this.max = max;
    }
    public Mana(Player player, int use, int max){
        this.playerID = player.getUniqueId();
        this.use = use;
        this.max = max;
    }
    public Mana(Map<String,Object> serialized){
        this.playerID = UUID.fromString((String) serialized.get("player"));
        this.use =(int) serialized.get("use");
        this.max = (int) serialized.get("max");
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public int getMax() {
        return max;
    }

    public int getUse() {
        return use;
    }

    public int changeUse(int delta){
        if(Bukkit.getPlayer(getPlayerID()).hasPermission("aurorafactions.infmana")){
            use = max;
            return max;
        }
        queueLock.lock();
        int localUse = getUse();
        if(((localUse+delta)>getMax()) || ((localUse+delta)<0)){
            queueLock.unlock();
            return -1;
        }
        use = localUse+delta;
        queueLock.unlock();
        DisplayMana.sendManaTitleToPlayer(Bukkit.getPlayer(getPlayerID()),getUse(),getMax());
        return use;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> output = new HashMap<>();
        output.put("player",this.playerID.toString());
        output.put("use",this.use);
        output.put("max",this.max);
        return output;
    }

}
