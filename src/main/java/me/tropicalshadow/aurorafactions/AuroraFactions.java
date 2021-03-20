package me.tropicalshadow.aurorafactions;

import github.scarsz.discordsrv.DiscordSRV;
import me.tropicalshadow.aurorafactions.NMS.Title;
import me.tropicalshadow.aurorafactions.claims.Claims;
import me.tropicalshadow.aurorafactions.listener.CrystalBlockEvents;
import me.tropicalshadow.aurorafactions.listener.DeathEvents;
import me.tropicalshadow.aurorafactions.listener.FactionAbillites;
import me.tropicalshadow.aurorafactions.listener.GuiListener;
import me.tropicalshadow.aurorafactions.chat.ChannelManger;
import me.tropicalshadow.aurorafactions.chat.ChatChannel;
import me.tropicalshadow.aurorafactions.mana.DisplayMana;
import me.tropicalshadow.aurorafactions.mana.ItemManager;
import me.tropicalshadow.aurorafactions.mana.Mana;
import me.tropicalshadow.aurorafactions.object.GameState;
import me.tropicalshadow.aurorafactions.placeholderapi.FactionTags;
import me.tropicalshadow.aurorafactions.utils.FileManager;
import me.tropicalshadow.aurorafactions.utils.Logging;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class AuroraFactions extends JavaPlugin {

    private static AuroraFactions plugin;
    private static Permission perms = null;
    private static LuckPerms luckPerms = null;
    private CrystalBlockEvents crystalBlockEvents;
    private boolean isDiscordSRVHooked = false;
    private FactionAbillites factionAbillites;
    private ChannelManger channelManger;
    private CommandSimper commandSimper;
    private GuiListener guiListener;
    private ChatChannel chatChannel;
    private DisplayMana displayMana;
    private ItemManager itemManager;
    private FileManager fileManager;
    private DeathEvents deathEvents;
    private Claims claims;
    private Title title;
    public static Permission getPermissions() {
        return perms;
    }

    private static GameState gameState = GameState.NON;

    @Override
    public void onEnable() {
        plugin = this;
        ConfigurationSerialization.registerClass(Mana.class);

        fileManager = new FileManager();
        gameState = checkGameState();
        claims = new Claims();
        channelManger = new ChannelManger();
        chatChannel = new ChatChannel();
        guiListener = new GuiListener();
        commandSimper = new CommandSimper();
        setupPermissions();
        crystalBlockEvents = new CrystalBlockEvents();
        factionAbillites = new FactionAbillites();
        title = new Title();
        displayMana = new DisplayMana();
        itemManager = new ItemManager();
        deathEvents = new DeathEvents();

        getServer().getPluginManager().registerEvents(chatChannel,this);
        getServer().getPluginManager().registerEvents(guiListener,this);
        getServer().getPluginManager().registerEvents(crystalBlockEvents,this);
        getServer().getPluginManager().registerEvents(factionAbillites,this);
        getServer().getPluginManager().registerEvents(displayMana,this);
        getServer().getPluginManager().registerEvents(itemManager,this);
        getServer().getPluginManager().registerEvents(commandSimper,this);
        getServer().getPluginManager().registerEvents(deathEvents,this);

        checkForDiscordSrvThenSubscribe();

        registerCommands("factions","faction","powercrystal","manaitem","plugins","resourcepack","claim");
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            FactionTags ppapitags = new FactionTags();
            ppapitags.register();
        }
        Mana.setPlayers();
        claims.renderLoadedChunks();
        Logging.info("Game State has been set to "+gameState.getName());
    }

    public GameState checkGameState(){
        reloadConfig();
        int gameStateID = getConfig().getInt("GameState");
        return GameState.fromID(gameStateID);
    }

    public void registerCommands(String... commands){
        for (String command : commands) {
            getCommand(command).setExecutor(commandSimper);
            getCommand(command).setTabCompleter(commandSimper);
        }
        ArrayList<String> string = new ArrayList<>();
        string.add("pl");
        getCommand("plugins").setAliases(string);

    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        deathEvents = null;
        itemManager = null;
        displayMana = null;
        factionAbillites = null;
        crystalBlockEvents = null;
        guiListener = null;
        commandSimper = null;
        title = null;
        fileManager.saveAll();
        fileManager = null;
        claims = null;
        this.plugin = null;

    }

    public static AuroraFactions getPlugin() {
        return plugin;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp!=null) {
            perms = rsp.getProvider();
        }
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();

        }
        return perms != null || luckPerms != null;
    }

    private void checkForDiscordSrvThenSubscribe(){
        if (getServer().getPluginManager().isPluginEnabled("DiscordSRV")){
            if (!isDiscordSRVHooked){
                this.isDiscordSRVHooked = true;
                DiscordSRV.api.subscribe(getChannelManger());
            }
        }else{
            Logging.warning("DiscordSRV is not currently enabled (messages will not be sent to Discord).");
            Logging.warning("Staff chat messages will still work in-game, however.");
        }
    }

    public static LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public boolean getDiscordSRVHooked(){
        return this.isDiscordSRVHooked;
    }
    public ChannelManger getChannelManger(){
        return channelManger;
    }

    public Title getTitle() {
        return title;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public static GameState getGameState() {
        return gameState;
    }

    public Claims getClaims() {
        return claims;
    }
}
