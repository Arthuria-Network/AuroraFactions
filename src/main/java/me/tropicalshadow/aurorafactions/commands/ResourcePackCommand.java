package me.tropicalshadow.aurorafactions.commands;

import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommand;
import me.tropicalshadow.aurorafactions.commands.utils.ShadowCommandInfo;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


@ShadowCommandInfo(name="resourcepack",isPlayerOnly = true)
public class ResourcePackCommand extends ShadowCommand {

    @Override
    public void execute(Player player, String[] args) {
        player.setResourcePack("https://arthuria.club/resourcepacks/AuroraResourcePack.zip");
        player.sendMessage("Set your resource pack to aurora resource pack");

    }
}
