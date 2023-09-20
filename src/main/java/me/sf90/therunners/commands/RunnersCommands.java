package me.sf90.therunners.commands;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.event.TheEvent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RunnersCommands implements CommandExecutor {

    private final TheRunners instance;

    public RunnersCommands(TheRunners instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;

        TheEvent currentEvent = instance.getCurrentEvent();
        if(currentEvent == null) return true;

        if(args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("katıl")){
            currentEvent.joinEvent(p);
            return true;
        }

        if(args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("ayrıl")){
            currentEvent.leaveEvent(p);
            return true;
        }

        return false;
    }
}
