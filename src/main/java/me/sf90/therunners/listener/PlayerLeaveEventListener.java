package me.sf90.therunners.listener;

import me.sf90.therunners.TheRunners;
import me.sf90.therunners.event.TheEvent;
import me.sf90.therunners.utils.TextUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerLeaveEventListener implements Listener {

    private final TheRunners instance;

    public PlayerLeaveEventListener(TheRunners instance){
        this.instance = instance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        TheEvent currentEvent = instance.getCurrentEvent();
        Player p = e.getPlayer();
        if(currentEvent == null) return;
        if(!currentEvent.playerIsInEvent(p)) return;
        currentEvent.leaveEvent(p);
    }

    @EventHandler
    public void onPlayerTP(PlayerTeleportEvent e){
        TheEvent currentEvent = instance.getCurrentEvent();
        Player p = e.getPlayer();
        if(currentEvent == null) return;
        if(!currentEvent.playerIsInEvent(p)) return;
        currentEvent.leaveEvent(p);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        TheEvent currentEvent = instance.getCurrentEvent();
        Player p = e.getPlayer();
        if(currentEvent == null) return;
        if(!currentEvent.playerIsInEvent(p)) return;
        e.setCancelled(true);
        p.sendMessage(TextUtils.fc(instance.getMessagesConfig(), "messages.canNotUseCommands"));
    }
}
