package me.sf90.therunners;

import me.sf90.therunners.commands.RunnersAdminCommands;
import me.sf90.therunners.commands.RunnersCommands;
import me.sf90.therunners.event.TheEvent;
import me.sf90.therunners.listener.PlayerFinishRaceListener;
import me.sf90.therunners.listener.PlayerLeaveEventListener;
import me.sf90.therunners.listener.PlayerMoveListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TheRunners extends JavaPlugin {

    private TheRunners instance;

    private File messagesConfigFile;
    private FileConfiguration messagesConfig;
    
    private TheEvent currentEvent;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        createMessagesConfig();

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFinishRaceListener(this), this);


        getCommand("runners").setExecutor(new RunnersCommands(this));
        getCommand("runnersadmin").setExecutor(new RunnersAdminCommands(this));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(!allLocationsSet()){
                    getLogger().warning("One of all locations was not set! Set it with /runnersadmin setspawn|setPointA|setPointB|setLobby");
                    return;
                }
                currentEvent = new TheEvent(instance, getConfig().getLocation("location.spawn"), getConfig().getLocation("location.lobby"));
                currentEvent.startEvent();
            }
        }, getConfig().getInt("general.eventStartDelay") * 20L, getConfig().getInt("general.eventPeriod") * 20L);
    }

    @Override
    public void onDisable() {}

    // CONFIG
    public FileConfiguration getMessagesConfig(){
        return messagesConfig;
    }

    private void createMessagesConfig() {
        messagesConfigFile = new File(getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
    }

    public void reloadAllConfigs() {
        if(!getDataFolder().exists()) return;
        reloadConfig();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
    }

    // EVENT
    private boolean allLocationsSet(){
        return getConfig().isSet("location.spawn") && getConfig().isSet("location.pointA") && getConfig().isSet("location.pointB") && getConfig().isSet("location.lobby");
    }

    public TheEvent getCurrentEvent(){
        return currentEvent;
    }
}
