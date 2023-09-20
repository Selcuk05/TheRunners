package me.sf90.therunners;

import me.sf90.therunners.commands.RunnersAdminCommands;
import me.sf90.therunners.commands.RunnersCommands;
import me.sf90.therunners.commands.completers.RunnersAdminCompleter;
import me.sf90.therunners.commands.completers.RunnersCompleter;
import me.sf90.therunners.event.TheEvent;
import me.sf90.therunners.listener.PlayerFinishRaceListener;
import me.sf90.therunners.listener.PlayerLeaveEventListener;
import me.sf90.therunners.listener.PlayerMoveListener;
import me.sf90.therunners.utils.UpdateChecker;
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
        messagesConfig = setupCustomConfig("messages.yml");

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerFinishRaceListener(this), this);

        getCommand("runners").setExecutor(new RunnersCommands(this));
        getCommand("runners").setTabCompleter(new RunnersCompleter());

        getCommand("runnersadmin").setExecutor(new RunnersAdminCommands(this));
        getCommand("runnersadmin").setTabCompleter(new RunnersAdminCompleter());

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if(!allLocationsSet()){
                getLogger().warning("One of all locations was not set! Set it with /runnersadmin setspawn | setPointA | setPointB | setLobby");
                return;
            }
            currentEvent = new TheEvent(instance, getConfig().getLocation("location.spawn"), getConfig().getLocation("location.lobby"));
            currentEvent.startEvent();
        }, getConfig().getInt("general.eventStartDelay") * 20L, getConfig().getInt("general.eventPeriod") * 20L);

        new UpdateChecker(this, 112655).getVersion(version -> {
            if (this.getDescription().getVersion().equals(version)) {
                getLogger().info("You are running the most recent version!");
            } else {
                getLogger().info(String.format("There is an update for TheRunners: %s", version));
            }
        });
    }

    @Override
    public void onDisable() {}

    // CONFIG
    private FileConfiguration setupCustomConfig(String resourcePath) {
        File configFile = new File(getDataFolder(), resourcePath);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            saveResource(resourcePath, false);
        }

        FileConfiguration configPointer = YamlConfiguration.loadConfiguration(configFile);
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

        return configPointer;
    }

    public void reloadAllConfigs() {
        if(!getDataFolder().exists()) return;
        reloadConfig();
        saveConfig();
        messagesConfig = setupCustomConfig("messages.yml");
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
