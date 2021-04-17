package me.tropicalshadow.aurorafactions.utils;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataType;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;

import java.util.Collection;

public class PermissionUtils {
    private static final LuckPerms luckperms = AuroraFactions.getLuckPerms();

    public static boolean playerHasFactionRole(Player player){
        boolean flag = false;
        Collection<Group> inheritedGroups=null;
        if(player.isOp()) {
            User user = luckperms.getUserManager().loadUser(player.getUniqueId()).join();
            inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
        }
        for(String groupName : new String[] {"red", "blue","green", "yellow"}){
            if(player.hasPermission("group."+groupName) && !player.isOp()){
                flag = true;
            }else if(player.isOp()){
                assert inheritedGroups != null;
                if(inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName))){
                   flag = true;
                }
            }
        }
        return flag;
    }

    public static void removeAllFactions(User user){
        for(String groupName : new String[] {"red", "blue","green", "yellow"}){
            InheritanceNode groupNode = InheritanceNode.builder(groupName).build();
            user.data().remove(groupNode);

            luckperms.getUserManager().saveUser(user);
        }
    }
    public static void addFactionRole(Player player,String groupName){
        User user = luckperms.getUserManager().getUser(player.getUniqueId());
        assert user != null;
        removeAllFactions(user);
        Group group = luckperms.getGroupManager().getGroup(groupName);
        assert group != null;
        InheritanceNode node = InheritanceNode.builder(group.getName()).value(true).build();

        NodeMap data = user.getData(DataType.NORMAL);
        data.add(node);

        luckperms.getUserManager().saveUser(user);
    }

    public static FactionColours getFactionColour(Player player){
        FactionColours result = FactionColours.NON;
        Collection<Group> inheritedGroups=null;
        if(player.isOp()) {
            User user = luckperms.getUserManager().loadUser(player.getUniqueId()).join();
            inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
        }
        for(String groupName : new String[] {"red", "blue","green", "yellow"}){
            if((player.hasPermission("group."+groupName) && !player.isOp())){
                result = FactionColours.getFromName(groupName);
            }else if(player.isOp()){
                assert inheritedGroups != null;
                if(inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName))){
                    result = FactionColours.getFromName(groupName);
                }
            }
        }
        return result;
    }
    public static boolean isMemberLeader(Player player){
        boolean isLeader= false;
        Collection<Group> inheritedGroups=null;
        if(player.isOp()) {
            User user = luckperms.getUserManager().loadUser(player.getUniqueId()).join();
            inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
        }
        for(String groupName : new String[] {"redleader", "blueleader","greenleader", "yellowleader"}){
            if(player.hasPermission("group."+groupName) && !player.isOp()){
                isLeader = true;
            }else if(player.isOp()){
                assert inheritedGroups != null;
                if(inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName))){
                    isLeader = true;
                }
            }
        }
        return isLeader;
    }
    public static boolean isMemberLeader(Player player,FactionColours colour){
        boolean isLeader= false;
        if(colour.equals(FactionColours.NON))
            return false;
        Collection<Group> inheritedGroups=null;
        if(player.isOp()) {
            User user = luckperms.getUserManager().loadUser(player.getUniqueId()).join();
            inheritedGroups = user.getInheritedGroups(user.getQueryOptions());
        }
        String groupName = colour.name().toLowerCase();
            if(player.hasPermission("group."+groupName+"leader") && !player.isOp()){
                isLeader = true;
            }else if(player.isOp()){
                assert inheritedGroups != null;
                if(inheritedGroups.stream().anyMatch(g -> g.getName().equals(groupName+"leader"))){
                    isLeader = true;
                }
            }
        return isLeader;
    }
}
