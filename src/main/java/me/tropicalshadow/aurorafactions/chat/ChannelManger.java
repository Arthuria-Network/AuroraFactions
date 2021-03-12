package me.tropicalshadow.aurorafactions.chat;

import com.vdurmont.emoji.EmojiParser;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import me.tropicalshadow.aurorafactions.AuroraFactions;
import me.tropicalshadow.aurorafactions.object.MappedPlaceholder;
import me.tropicalshadow.aurorafactions.utils.FactionColours;
import me.tropicalshadow.aurorafactions.utils.PermissionUtils;
import me.tropicalshadow.aurorafactions.utils.StringUtils;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import static org.bukkit.Bukkit.getServer;

public class ChannelManger {

    private static AuroraFactions instance;

    public ArrayList<String> ChannelNames = new ArrayList<String>(Arrays.asList("redfaction", "bluefaction", "greenfaction", "yellowfaction"));
    public HashMap<String, ArrayList<Player>> channels = new HashMap<>();
    public HashMap<Player, String> playerChannel = new HashMap<Player, String>();

    public ChannelManger(){
        instance = AuroraFactions.getPlugin();
    }

    public void toggleChat(Player player){
        FactionColours colour = PermissionUtils.getFactionColour(player);
        if(playerChannel.get(player)!=null){
            player.sendMessage(ChatColor.GREEN.toString()+"You left the "+colour.formatedName+" chat");
            leaveChannel(player,playerChannel.get(player));
        }else if(!colour.equals(FactionColours.NON)){
            joinChannel(player,colour.channelName);
        }
    }

    public void joinChannel(Player player, String channelName){
        if(playerChannel.get(player)!=null){
            leaveChannel(player,playerChannel.get(player));
        }
        ArrayList<Player> players = channels.get(channelName);
        if(players==null){
            players = new ArrayList<>();
        }
        players.add(player);
        channels.put(channelName,players);
        playerChannel.put(player,channelName);
        FactionColours colour = FactionColours.getFromChannelName(channelName);
        player.sendMessage(ChatColor.GREEN.toString()+"You joined "+colour.formatedName+" faction chat");
    }
    public void leaveChannel(Player player, String channelName){
        ArrayList<Player> players = channels.get(channelName);
        players.remove(player);
        channels.put(channelName,players);
        playerChannel.remove(player);
    }

    public ArrayList<Player> getChannel(Player player){
        String channelName = playerChannel.get(player);
        return channels.get(channelName);
    }

    public HashMap<String,TextChannel> getTextChannels(){
        HashMap<String,TextChannel> textChannels = new HashMap<>();
        if(!instance.getDiscordSRVHooked())return textChannels;
        ChannelNames.forEach(channel->textChannels.put(channel,DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(channel)));
        return textChannels;
    }
    public TextChannel getCurrentTextChannel(Player player){
        String channelName = playerChannel.get(player);
        if(channelName==null)return null;
        return getTextChannels().get(channelName);
    }
    public boolean canPlayerSeeChannel(Player player, FactionColours colour){
        return PermissionUtils.getFactionColour(player)==colour || player.hasPermission("aurorafactions.factionchatoverride");
    }
    private void inGameAnnounce(String message,String ChannelName){
        String content = StringUtils.Colourfull(message);

        getServer().getOnlinePlayers().stream()
                .filter(player->canPlayerSeeChannel(player,FactionColours.getFromChannelName(ChannelName)))
                .forEach(colour -> colour.sendMessage(content));

        getServer().getConsoleSender().sendMessage(content);
    }

    private void updatePlaceholdersThenAnnounceInGame(String format, MappedPlaceholder placeholders,String ChannelName){
        // If the value of %message% doesn't exist for some reason, don't announce.
        if (StringUtils.isEmptyOrNull(placeholders.get("message"))) { return; }
        inGameAnnounce(placeholders.update(format),ChannelName);
    }
    public void submitMessageFromGame(Player author,String message){
        String channelName = playerChannel.get(author);
        FactionColours colour = FactionColours.getFromChannelName(channelName);
        String format = "("+colour.colour.toString()+colour.formatedName+ChatColor.RESET.toString()+" Faction) "+"%nickname% => %message%";
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
        {
            format = PlaceholderAPI.setPlaceholders(author, format);
        }
        MappedPlaceholder placholders = new MappedPlaceholder();

        placholders.map("message", "content", "text").to(() -> message);
        placholders.map("user", "name", "username", "player", "sender").to(author::getName);
        placholders.map("nickname", "displayname").to(author::getDisplayName);

        updatePlaceholdersThenAnnounceInGame(format, placholders,channelName);
        if(instance.getDiscordSRVHooked() && !getTextChannels().isEmpty() && getTextChannels().get(channelName)!=null){
            getServer().getScheduler().runTaskAsynchronously(instance, () ->
                    DiscordSRV.getPlugin().processChatMessage(author, message, channelName, false)
            );
        }
    }
    public void submitMessageFromDiscord(User author, Message message,String channelName){
        String text = EmojiParser.parseToAliases(message.getContentStripped());
        FactionColours colour = FactionColours.getFromChannelName(channelName);
        String format = "(Discord | "+colour.colour.toString()+colour.formatedName+ChatColor.RESET.toString()+" Faction) %username% => %text%";
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI"))
        {
            // Update format's PAPI placeholders before inserting the message.
            format = PlaceholderAPI.setPlaceholders(null, format);
        }

        MappedPlaceholder placholders = new MappedPlaceholder();

        placholders.map("message", "content", "text").to(() -> text);
        placholders.map("user", "name", "username", "sender").to(author::getName);
        placholders.map("nickname", "displayname").to(message.getGuild().getMember(author)::getEffectiveName);
        placholders.map("discriminator", "discrim").to(author::getDiscriminator);

        updatePlaceholdersThenAnnounceInGame(format, placholders,channelName);
    }
    @Subscribe
    public void onDiscordChat(DiscordGuildMessagePreProcessEvent event){
        if (this.getTextChannels().containsValue(event.getChannel())){
            event.setCancelled(true); // Cancel this message from getting sent to global chat.
            AtomicReference<String> ChannelName = new AtomicReference<>("");
            this.getTextChannels().forEach((key,value)->{
                if(value==event.getChannel()){
                    ChannelName.set(key);
                }
            });
            instance.getServer().getScheduler().runTask(instance, () ->
                    this.submitMessageFromDiscord(event.getAuthor(), event.getMessage(),ChannelName.get())
            );
        }
    }
}
