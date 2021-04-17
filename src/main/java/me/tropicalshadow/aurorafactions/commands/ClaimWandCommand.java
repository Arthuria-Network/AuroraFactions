package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;


@ShadowCommandInfo(name="claimwand", isPlayerOnly = true, permission ="aurorafactions.claim.admin")
public class ClaimWandCommand extends ShadowCommand {

    @Override
    public void execute(Player player, String[] args) {
        player.getInventory().addItem( Claims.adminWand(Claims.AdminWandModes.REMOVE, FactionColours.NON));
        player.sendMessage(Component.text("You have been praised with the all mighty admin claiming wand"));
    }
}
