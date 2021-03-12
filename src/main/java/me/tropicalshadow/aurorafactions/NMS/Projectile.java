package me.tropicalshadow.aurorafactions.NMS;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Projectile extends Reflection{

    public void sendHideProjectile(ArrayList<Player> players, Entity e){
        Class<?> DestoryEntityPacket = getNMSClass("PacketPlayOutEntityDestroy");
        try{
            Constructor<?> packetConstructor = DestoryEntityPacket.getConstructor(int.class);
            Object spacket = packetConstructor.newInstance(e.getEntityId());
            players.forEach(p -> sendPacket(p,spacket));
        }catch (Exception err){}
    }
}
