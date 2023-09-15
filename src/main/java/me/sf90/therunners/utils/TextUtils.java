package me.sf90.therunners.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class TextUtils {

    public static String fc(FileConfiguration conf, String path){
        return ChatColor.translateAlternateColorCodes('&', conf.getString(path));
    }
}
