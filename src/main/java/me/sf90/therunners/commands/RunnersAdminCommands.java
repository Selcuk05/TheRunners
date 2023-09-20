package me.sf90.therunners.commands;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunnersAdminCommands implements CommandExecutor {

    private final TheRunners instance;

    public RunnersAdminCommands(TheRunners instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;
        Location loc = p.getLocation();
        if(!p.hasPermission("therunners.admin")){
            p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "command.notAdmin"));
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            instance.reloadAllConfigs();
            p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "command.reloadDone"));
            return true;
        }

        if(args[0].equalsIgnoreCase("setspawn")){
            locSetter(loc, "location.spawn");
            p.sendMessage(String.format(TextUtils.fc(instance.getMessagesConfig(), "command.locationSet"), "spawn"));
            return true;
        }

        if(args[0].equalsIgnoreCase("setPointA")){
            locSetter(loc, "location.pointA");
            p.sendMessage(String.format(TextUtils.fc(instance.getMessagesConfig(), "command.locationSet"), "point A"));
            return true;
        }

        if(args[0].equalsIgnoreCase("setPointB")){
            locSetter(loc, "location.pointB");
            p.sendMessage(String.format(TextUtils.fc(instance.getMessagesConfig(), "command.locationSet"), "point B"));
            return true;
        }

        if(args[0].equalsIgnoreCase("setLobby")){
            locSetter(loc, "location.lobby");
            p.sendMessage(String.format(TextUtils.fc(instance.getMessagesConfig(), "command.locationSet"), "lobby"));
            return true;
        }

        return false;
    }

    private void locSetter(Location location, String path){
        instance.getConfig().set(path, location);
        instance.saveConfig();
    }

}
