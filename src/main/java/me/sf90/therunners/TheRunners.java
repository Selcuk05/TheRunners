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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class TheRunners extends JavaPlugin {

    private TheRunners instance;

    private FileConfiguration messagesConfig;
    
    private TheEvent currentEvent;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        // setupMessagesConfig();
        setupCustomConfig("messages.yml", messagesConfig);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFinishRaceListener(this), this);


        getCommand("runners").setExecutor(new RunnersCommands(this));
        getCommand("runnersadmin").setExecutor(new RunnersAdminCommands(this));

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if(!allLocationsSet()){
                getLogger().warning("One of all locations was not set! Set it with /runnersadmin setspawn | setPointA | setPointB | setLobby");
                return;
            }
            currentEvent = new TheEvent(instance, getConfig().getLocation("location.spawn"), getConfig().getLocation("location.lobby"));
            currentEvent.startEvent();
        }, getConfig().getInt("general.eventStartDelay") * 20L, getConfig().getInt("general.eventPeriod") * 20L);
    }

    @Override
    public void onDisable() {}

    // CONFIG

    /* private void setupMessagesConfig() {
        File messagesConfigFile = new File(getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        InputStream messagesStream = getResource("messages.yml");
        InputStreamReader reader = new InputStreamReader(messagesStream);
        FileConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
        for (String path : defaults.getKeys(true)) {
            if (!this.messagesConfig.contains(path)) {
                this.messagesConfig.set(path, defaults.get(path));
            }
        }
        try {
            this.messagesConfig.save(messagesConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    } */

    private void setupCustomConfig(String resourcePath, FileConfiguration configPointer) {
        File configFile = new File(getDataFolder(), resourcePath);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(resourcePath, false);
        }

        configPointer = YamlConfiguration.loadConfiguration(configFile);
        InputStream messagesStream = getResource(resourcePath);
        InputStreamReader reader = new InputStreamReader(messagesStream);
        FileConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
        for (String path : defaults.getKeys(true)) {
            if (!configPointer.contains(path)) {
                configPointer.set(path, defaults.get(path));
            }
        }
        try {
            configPointer.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void reloadAllConfigs() {
        if(!getDataFolder().exists()) return;
        reloadConfig();
        saveConfig();
        // setupMessagesConfig();
        setupCustomConfig("messages.yml", messagesConfig);
    }

    public FileConfiguration getMessagesConfig(){
        return messagesConfig;
    }

    // EVENT
    private boolean allLocationsSet(){
        return getConfig().isSet("location.spawn") && getConfig().isSet("location.pointA") && getConfig().isSet("location.pointB") && getConfig().isSet("location.lobby");
    }

    public TheEvent getCurrentEvent(){
        return currentEvent;
    }
}
