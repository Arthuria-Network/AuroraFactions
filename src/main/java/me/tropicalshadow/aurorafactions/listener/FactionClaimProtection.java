package me.tropicalshadow.aurorafactions.listener;

import com.google.common.collect.ImmutableList;
import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import org.bukkit.Chunk;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class FactionClaimProtection implements Listener {



    private boolean isPlayerAllowedToInteractInChunk(Chunk chunk, Player player){
        FactionColours claiment = Claims.isChunkClaimed(chunk);
        if(claiment.equals(FactionColours.NON))
            return true;

        if(PermissionUtils.getFactionColour(player).equals(claiment)){
            if(AuroraFactions.getPlugin().getClaims().isTrusted(claiment,player))
                return true;
        }

        return false;
    }

    @EventHandler()
    public void onExplosionUnknownSource(BlockExplodeEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if(Claims.isChunkClaimed(chunk).equals(FactionColours.NON))return;
        event.setCancelled(true);
    }

    @EventHandler()
    public void onExplison(EntityExplodeEvent event){
        ImmutableList.copyOf(event.blockList()).forEach(block->{
            if(!Claims.isChunkClaimed(block.getChunk()).equals(FactionColours.NON)){
                event.blockList().remove(block);
            }
        });
    }

    public void onAttack(EntityDamageByEntityEvent event){ // questionable
        if(event.getEntity().getType().equals(EntityType.ENDER_CRYSTAL))return;
        Chunk chunk = event.getEntity().getChunk();
        FactionColours colour = Claims.isChunkClaimed(chunk);
        if(event.getDamager().getType().equals(EntityType.PLAYER) && !event.getEntity().getType().equals(EntityType.PLAYER)){
            if(!colour.equals(PermissionUtils.getFactionColour((Player) event.getDamager()))){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler()
    public void onBreakBlock(BlockBreakEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if(!isPlayerAllowedToInteractInChunk(chunk, event.getPlayer()) && !event.getPlayer().isOp()){
            event.getPlayer().sendMessage("This chunk is claimed");
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onPlaceBlock(BlockPlaceEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if(!isPlayerAllowedToInteractInChunk(chunk, event.getPlayer()) && !event.getPlayer().isOp()){
            event.getPlayer().sendMessage("This chunk is claimed");
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void onMultiBlockPlace(BlockMultiPlaceEvent event){
        Chunk chunk = event.getBlock().getChunk();
        if(!isPlayerAllowedToInteractInChunk(chunk, event.getPlayer()) && !event.getPlayer().isOp()){
            event.getPlayer().sendMessage("This chunk is claimed");
            event.setCancelled(true);
        }
    }
    @EventHandler()
    public void playerInteraction(PlayerInteractEvent event){
        Chunk chunk;
        if(event.getClickedBlock()!=null)
            chunk = event.getClickedBlock().getChunk();
        else if(event.getInteractionPoint()!= null)
            chunk = event.getInteractionPoint().getChunk();
        else
            chunk = event.getPlayer().getChunk();
        if(!isPlayerAllowedToInteractInChunk(chunk, event.getPlayer()) && !event.getPlayer().isOp()){
            event.getPlayer().sendMessage("This chunk is claimed");
            event.setCancelled(true);
        }
    }


    @EventHandler()
    public void onTeleport(PlayerTeleportEvent event){
        if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT) || event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)){
            Chunk chunk = event.getTo().getChunk();
            if(!isPlayerAllowedToInteractInChunk(chunk, event.getPlayer()) && !event.getPlayer().isOp()){
                event.getPlayer().sendMessage("This chunk is claimed");
                event.setCancelled(true);
            }
        }
    }
}
