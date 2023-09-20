package me.sf90.therunners.commands.completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RunnersCompleter implements TabCompleter {

    private final String[] COMMANDS = { "join", "leave" };
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        final List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        return completions;
    }
}
