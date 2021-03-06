package me.tropicalshadow.aurorafactions.commands.utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ShadowCommand implements TabExecutor {
    private final ShadowCommandInfo commandInfo;
    private final ArrayList<String> aliases;

    public ShadowCommand() {
        commandInfo = getClass().getDeclaredAnnotation(ShadowCommandInfo.class);
        Objects.requireNonNull(commandInfo,"Commands must have ShadowCommandInfo Annotation");
        ShadowCommandAlias alias = getClass().getDeclaredAnnotation(ShadowCommandAlias.class);
        if(alias==null)
            aliases = new ArrayList<>();
        else
            aliases = new ArrayList<>(Arrays.asList(alias.alias()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        boolean isPlayer = (sender instanceof Player);
        if(!commandInfo.permission().isEmpty()){
            if(!sender.hasPermission(commandInfo.permission())){
                sender.sendMessage(commandInfo.permissionErr());
                return true;
            }
        }

        if(commandInfo.isPlayerOnly()){
            if(!isPlayer){
                sender.sendMessage(commandInfo.isPlayerOnlyErr());
                return true;
            }
            execute((Player)sender,args);
            return true;
        }
        execute(sender,args);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        boolean isPlayer = (sender instanceof Player);
        if(!commandInfo.permission().isEmpty()){
            if(!sender.hasPermission(commandInfo.permission())){
                return new ArrayList<>();
            }
        }

        if(commandInfo.isPlayerOnly()){
            if(!isPlayer){
                return new ArrayList<>();
            }
            return tabComplete((Player)sender,args);
        }

        return tabComplete(sender,args);

    }

    public void execute(Player player, String[] args){}

    public void execute(CommandSender sender, String[] args){}


    public ArrayList<String> tabComplete(CommandSender sender, String[] args){return new ArrayList<>();}
    public ArrayList<String> tabComplete(Player player, String[] args){return new ArrayList<>();}

    public ShadowCommandInfo getCommandInfo() {
        return commandInfo;
    }

    public ArrayList<String> getAliases() {
        return aliases;
    }
}
