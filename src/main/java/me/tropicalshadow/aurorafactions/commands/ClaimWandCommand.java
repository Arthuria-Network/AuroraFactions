package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;


@ShadowCommandInfo(name="claimwand", isPlayerOnly = true, permission ="aurorafactions.claim.admin")
public class ClaimWandCommand extends ShadowCommand {

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


    @Override
    public void execute(Player player, String[] args) {
        if(args.length > 0){
            if( args[0].equalsIgnoreCase("check")){
            player.sendMessage(String.valueOf(isPlayerAllowedToInteractInChunk(player.getChunk(),player)));
            FactionColours claiment = Claims.isChunkClaimed(player.getChunk());
            if(claiment.equals(FactionColours.NON))
                player.sendMessage("NON");
            if(PermissionUtils.getFactionColour(player).equals(claiment)){
                if(AuroraFactions.getPlugin().getClaims().isTrusted(claiment,player))
                    player.sendMessage("PERMISSION");
            }

            }else if(args[0].equalsIgnoreCase("checktrusts")){
                AuroraFactions.getPlugin().getClaims().getTrustedInClaims().forEach((f,ids)->{
                    player.sendMessage(f.formatedName);
                    ids.forEach(id -> player.sendMessage(id.toString()));
                    player.sendMessage("------------");
                });
            }else if(args[0].equalsIgnoreCase("checkchunks")){
                AuroraFactions.getPlugin().getClaims().getFactionClaims().forEach(claim-> player.sendMessage(claim.getFaction().toString() + " : "+claim.getChunk().getChunkKey()));
            }
            return;
        }
        player.getInventory().addItem( Claims.adminWand(Claims.AdminWandModes.REMOVE, FactionColours.NON));
        player.sendMessage(Component.text("You have been praised with the all mighty admin claiming wand"));
    }
}
