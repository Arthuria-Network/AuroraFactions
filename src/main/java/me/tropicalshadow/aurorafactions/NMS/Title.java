package me.tropicalshadow.aurorafactions.NMS;

import me.tropicalshadow.aurorafactions.utils.Logging;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class Title extends Reflection {
    private static Class<?> chatOutClass;
    private static Class<?> iChatBaseComponent;
    private static Class<?> packetPlayOutTitle;

    private static boolean Flagged = false;

    public Title(){
        try {
            chatOutClass = getNMSClass("PacketPlayOutChat");
            iChatBaseComponent = getNMSClass("IChatBaseComponent");
            packetPlayOutTitle = getNMSClass("PacketPlayOutTitle");
            if(chatOutClass ==null ){
                Logging.warning("chatOut Is Null");
            }
            if(iChatBaseComponent ==null ){
                Logging.warning("iChatBaseComponent IS NULL");
            }
            if(packetPlayOutTitle ==null ){
                Logging.warning("packetPlayOutTitle IS NULL");
            }
        } catch (Exception e) {
            Logging.warning("Failed to register in Title");
        }
    }

    public void sendTitle(Player player, int fadeInTime, int showTime, int fadeOutTime, String title, String subtitle) {
        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> stitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object spacket = stitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
                    fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, spacket);
        } catch (Exception ex) {
        }
    }
    public void sendActionMessage(Player player,String message) {
        //ComponentBuilder componentBuilder = new ComponentBuilder();
        //componentBuilder.color(ChatColor.AQUA);
        //componentBuilder.append(message);
        //player.spigot().sendMessage(ChatMessageType.ACTION_BAR, componentBuilder.create());
        try{
            Object chatsTitle = iChatBaseComponent.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + message + "\",\"color\":\"blue\"}");
            Constructor<?> stitleConstructor = packetPlayOutTitle.getConstructor(packetPlayOutTitle.getDeclaredClasses()[0], iChatBaseComponent,
                    int.class, int.class, int.class);
            Object spacket = stitleConstructor.newInstance(packetPlayOutTitle.getDeclaredClasses()[0].getField("ACTIONBAR").get(null), chatsTitle,
                    0, 72000, 0);
            sendPacket(player,spacket);
        }catch (Exception e){}
    }
}
